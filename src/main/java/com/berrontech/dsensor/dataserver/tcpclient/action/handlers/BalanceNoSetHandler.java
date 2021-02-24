package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.common.exception.ResourceNotFoundException;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.weight.task.SensorMetaDataService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create By Levent8421
 * Create Time: 2020/6/19 14:40
 * Class Name: BalanceNoSetHandler
 * Author: Levent8421
 * Description:
 * 设置逻辑货道号
 *
 * @author Levent8421
 */
@Slf4j
@ActionHandlerMapping("balance.no.set")
public class BalanceNoSetHandler implements ActionHandler {
    private final SlotService slotService;
    private final SensorMetaDataService sensorMetaDataService;

    public BalanceNoSetHandler(SlotService slotService,
                               SensorMetaDataService sensorMetaDataService) {
        this.slotService = slotService;
        this.sensorMetaDataService = sensorMetaDataService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Message onMessage(Message message) throws Exception {
        final Map<Object, String> data = MessageUtils.asObject(message.getData(), Map.class);
        if (data == null) {
            throw new BadRequestException("Empty Param Data!");
        }

        final Map<Integer, String> params = new HashMap<>(16);
        for (Map.Entry<Object, String> entry : data.entrySet()) {
            final Object addressObj = entry.getKey();
            final Integer address;
            if (addressObj instanceof Integer) {
                address = (Integer) addressObj;
            } else if (addressObj instanceof String) {
                address = Integer.parseInt((String) addressObj);
            } else {
                throw new BadRequestException("Can not resolve unknown type address: " + addressObj);
            }
            params.put(address, entry.getValue());
        }
        final Map<Integer, String> slotTable = loadSlotTableFromDatabase();
        mergeParamsIntoSlotTable(slotTable, params);
        final Map<Integer, String> failureTable = new HashMap<>(16);
        final Map<Integer, String> checkedSlotTable = checkDuplicate(slotTable, params, failureTable);
        boolean changed = false;
        for (Map.Entry<Integer, String> entry : checkedSlotTable.entrySet()) {
            final Integer address = entry.getKey();
            if (!params.containsKey(address)) {
                continue;
            }
            try {
                doSetSlotNo(address, entry.getValue());
            } catch (Exception e) {
                failureTable.put(address, e.getMessage());
                log.warn("Can not set slotNo[{}] for address[{}]", entry.getValue(), entry.getKey(), e);
            }
            changed = true;
        }
        if (changed) {
            sensorMetaDataService.refreshSlotTable();
        }
        final Payload<Map<Integer, String>> payload = Payload.ok(failureTable);
        return MessageUtils.replyMessage(message, payload);
    }

    private Map<Integer, String> checkDuplicate(Map<Integer, String> slotTable, Map<Integer, String> params, Map<Integer, String> failureTable) {
        final Map<Integer, String> res = new HashMap<>(16);
        final Map<String, List<Integer>> exists = new HashMap<>(16);
        for (Map.Entry<Integer, String> entry : slotTable.entrySet()) {
            final String slotNo = entry.getValue();
            final Integer address = entry.getKey();
            final List<Integer> addressList = exists.computeIfAbsent(slotNo, k -> new ArrayList<>());
            addressList.add(address);
        }
        exists.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .forEach(e -> {
                    final List<Integer> addressList = e.getValue();
                    final String error = String.format("以下地址货道号重复：Duplicate slotNo for address: %s", addressList.toString());
                    log.warn("Duplicate : {}", error);
                    for (Integer address : e.getValue()) {
                        if (!params.containsKey(address)) {
                            continue;
                        }
                        failureTable.put(address, error);
                    }
                });
        for (Map.Entry<String, List<Integer>> entry : exists.entrySet()) {
            final List<Integer> addressList = entry.getValue();
            if (addressList.size() == 1) {
                res.put(addressList.get(0), entry.getKey());
            }
        }
        return res;
    }

    private void mergeParamsIntoSlotTable(Map<Integer, String> slotTable, Map<Integer, String> params) {
        for (Map.Entry<Integer, String> entry : params.entrySet()) {
            final Integer address = entry.getKey();
            final String slotNo = entry.getValue();
            slotTable.put(address, slotNo);
        }
    }

    private Map<Integer, String> loadSlotTableFromDatabase() {
        final List<Slot> slotList = slotService.all();
        return slotList.stream()
                .collect(Collectors.toMap(Slot::getAddress, Slot::getSlotNo));
    }

    /**
     * 执行设置货道号操作
     *
     * @param address address
     * @param slotNo  slotNo
     */
    private void doSetSlotNo(Integer address, String slotNo) {
        if (address == null) {
            throw new BadRequestException("Can not resolve address for null!");
        }
        final Slot slot = slotService.findByAddress(address);
        if (slot == null) {
            final String error = String.format("物理地址[%s]不存在!", address);
            log.warn("Address Not Found [{}]!", error);
            throw new ResourceNotFoundException(error);
        }
        slotService.updateSlotNo(slot.getId(), slotNo);
    }
}
