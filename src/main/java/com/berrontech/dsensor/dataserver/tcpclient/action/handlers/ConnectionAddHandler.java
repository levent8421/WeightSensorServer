package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.service.general.DeviceConnectionService;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.DeviceConnectionAddParams;
import com.berrontech.dsensor.dataserver.weight.task.SensorMetaDataService;
import lombok.val;

import java.util.Map;
import java.util.Objects;

import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notEmpty;
import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notNull;

/**
 * Create By Levent8421
 * Create Time: 2020/6/16 19:02
 * Class Name: ConnectionAddHandler
 * Author: Levent8421
 * Description:
 * 新增连接
 *
 * @author Levent8421
 */
@ActionHandlerMapping("connection.add")
public class ConnectionAddHandler implements ActionHandler {
    private final DeviceConnectionService deviceConnectionService;
    private final SensorMetaDataService sensorMetaDataService;

    public ConnectionAddHandler(DeviceConnectionService deviceConnectionService,
                                SensorMetaDataService sensorMetaDataService) {
        this.deviceConnectionService = deviceConnectionService;
        this.sensorMetaDataService = sensorMetaDataService;
    }

    @Override
    public Message onMessage(Message message) throws Exception {
        final DeviceConnectionAddParams params = MessageUtils.asObject(message.getData(), DeviceConnectionAddParams.class);
        checkParams(params);

        final int type = asConnectionType(params.getType());
        final String target = params.getTarget();
        final DeviceConnection connection = new DeviceConnection();
        connection.setTarget(target);
        connection.setType(type);
        val saved = deviceConnectionService.save(connection);
        val data = Payload.ok(saved.getId());
        sensorMetaDataService.refreshSlotTable();
        return MessageUtils.replyMessage(message, data);
    }

    private void checkParams(DeviceConnectionAddParams params) {
        notNull(params, BadRequestException.class, "Invalidate Params!");
        notEmpty(params.getType(), BadRequestException.class, "Type could not be blank!");
        notEmpty(params.getTarget(), BadRequestException.class, "Target could not be empty!");
        for (Map.Entry<Integer, String> entry : DeviceConnection.TYPE_NAME_TABLE.entrySet()) {
            if (Objects.equals(entry.getValue(), params.getType())) {
                return;
            }
        }
        throw new BadRequestException("Invalidate Connection Type!");
    }

    /**
     * Convert string connection type to int enum connection type
     *
     * @param type string connection type
     * @return int enum connection type
     */
    private int asConnectionType(String type) {
        for (Map.Entry<Integer, String> entry : DeviceConnection.TYPE_NAME_TABLE.entrySet()) {
            if (Objects.equals(entry.getValue(), type)) {
                return entry.getKey();
            }
        }
        return -1;
    }
}
