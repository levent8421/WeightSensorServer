package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractEntityController;
import com.berrontech.dsensor.dataserver.web.vo.CompensationStateParam;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notEmpty;
import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notNull;

/**
 * Create By Levent8421
 * Create Time: 2020/6/20 11:42
 * Class Name: SlotController
 * Author: Levent8421
 * Description:
 * 货道相关数据访问组件
 *
 * @author Levent8421
 */
@Slf4j
@RestController
@RequestMapping("/api/slot")
public class SlotController extends AbstractEntityController<Slot> {
    private static final int MAX_SKU_NO_LENGTH = 9;
    private static final String COMPOSE_SKU_NO_FEATURES = "#";
    private final SlotService slotService;
    private final WeightController weightController;
    private final WeightSensorService weightSensorService;

    public SlotController(SlotService slotService,
                          WeightController weightController,
                          WeightSensorService weightSensorService) {
        super(slotService);
        this.slotService = slotService;
        this.weightController = weightController;
        this.weightSensorService = weightSensorService;
    }

    /**
     * 获取全部货道
     *
     * @return GR
     */
    @GetMapping("/")
    public GeneralResult<List<Slot>> all() {
        val res = slotService.all();
        return GeneralResult.ok(res);
    }

    /**
     * 全部清零
     *
     * @return zero
     */
    @PostMapping("/zero-all")
    public GeneralResult<Void> doZeroAll() {
        weightController.doZeroAll();
        return GeneralResult.ok();
    }

    /**
     * 设置全部补偿状态
     *
     * @return 补偿状态
     */
    @PostMapping("/_all-compensation")
    public GeneralResult<Void> setAllCompensationState(@RequestBody CompensationStateParam param) {
        notNull(param, BadRequestException.class, "No Params!");
        notNull(param.getEnableCompensation(), BadRequestException.class, "No state!");
        weightController.setAllCompensationStatus(param.getEnableCompensation());
        return GeneralResult.ok();
    }

    /**
     * 清零指定货道
     *
     * @param slotNo 货道号
     * @return GR
     */
    @PostMapping("/{slotNo}/zero")
    public GeneralResult<Void> zoZero(@PathVariable("slotNo") String slotNo) {
        weightController.doZero(slotNo);
        return GeneralResult.ok();
    }

    /**
     * 货道详细信息
     *
     * @param id slot if
     * @return GR
     */
    @GetMapping("/{id}")
    public GeneralResult<Slot> slotDetail(@PathVariable("id") Integer id) {
        val slot = slotService.get(id);
        final List<WeightSensor> sensors = weightSensorService.findBySlot(id);
        slot.setSensors(sensors);
        return GeneralResult.ok(slot);
    }

    /**
     * 更新Slot
     *
     * @param id    slot id
     * @param param params
     * @return GR
     */
    @PostMapping("/{id}")
    public GeneralResult<Void> update(@PathVariable("id") Integer id, @RequestBody Slot param) {
        val slot = slotService.require(id);
        checkAndCopyUpdateParam(slot, param);
        slotService.updateById(slot);
        return GeneralResult.ok();
    }

    private void checkAndCopyUpdateParam(Slot slot, Slot param) {
        val ex = BadRequestException.class;
        notNull(param, ex, "No Param!");
        notEmpty(param.getSlotNo(), ex, "Empty SlotNo!");
        notEmpty(param.getSkuNo(), ex, "Empty SkuNo!");
        notEmpty(param.getSkuName(), ex, "Empty SkuName!");
        notNull(param.getSkuApw(), ex, "Empty SkuApw!");
        notNull(param.getSkuTolerance(), ex, "Empty SkuTolerance!");

        slot.setSlotNo(param.getSlotNo());
        slot.setSkuNo(param.getSkuNo());
        slot.setSkuName(param.getSkuName());
        slot.setSkuApw(param.getSkuApw());
        slot.setSkuTolerance(param.getSkuTolerance());
        slot.setSkuShelfLifeOpenDays(param.getSkuShelfLifeOpenDays());
    }

    /**
     * 设置ELabel状态
     *
     * @param id    slot id
     * @param param rb
     * @return GR
     */
    @PostMapping("/{id}/has-e-label")
    public GeneralResult<Void> toggleElabelState(@PathVariable("id") Integer id, @RequestBody Slot param) {
        notNull(param, BadRequestException.class, "No Params!");
        notNull(param.getHasElabel(), BadRequestException.class, "No ELabel state!");
        slotService.setElabelState(id, param.getHasElabel());
        weightSensorService.setElabelStateBySlotId(id, param.getHasElabel());
        return GeneralResult.ok();
    }

    /**
     * 电子标签高亮
     *
     * @param param params
     * @return GR
     */
    @PostMapping("/highlight")
    public GeneralResult<List<Slot>> highlightBySku(@RequestBody Slot param) {
        notNull(param, BadRequestException.class, "No available Params!");
        notEmpty(param.getSkuNo(), BadRequestException.class, "No sku param!");
        final String skuNo = normalizeSkuNo(param.getSkuNo());

        final List<Slot> slots = slotService.findBySku(skuNo);
        if (slots.isEmpty()) {
            return GeneralResult.badRequest("Can not find sku[" + skuNo + "]");
        }
        final Set<String> slotNoSet = slots.stream()
                .map(Slot::getSlotNo)
                .collect(Collectors.toSet());
        weightController.highlight(slotNoSet, ApplicationConstants.ELabel.ELABEL_HIGHLIGHT_DURATION);
        return GeneralResult.ok(slots);
    }

    /**
     * 预处理SkuNo
     *
     * @param skuNo sku no
     * @return sku no
     */
    private String normalizeSkuNo(String skuNo) {
        if (skuNo.length() > MAX_SKU_NO_LENGTH && skuNo.contains(COMPOSE_SKU_NO_FEATURES)) {
            return skuNo.substring(1, MAX_SKU_NO_LENGTH + 1);
        }
        return skuNo;
    }

    /**
     * Find All Slot With Sensor
     *
     * @return GR
     */
    @GetMapping("/_with-sensors")
    public GeneralResult<List<Slot>> findAllWithSensors() {
        final List<WeightSensor> sensors = weightSensorService.all();
        final List<Slot> slots = slotService.all();
        final Map<Integer, Slot> slotMap = slots.stream()
                .peek(s -> s.setSensors(new ArrayList<>()))
                .collect(Collectors.toMap(Slot::getId, v -> v));
        final Slot defaultSlot = new Slot();
        defaultSlot.setSensors(new ArrayList<>());
        defaultSlot.setSlotNo("No bind");
        defaultSlot.setId(-1);
        for (WeightSensor sensor : sensors) {
            final Integer slotId = sensor.getSlotId();
            if (slotMap.containsKey(slotId)) {
                final Slot slot = slotMap.get(slotId);
                slot.getSensors().add(sensor);
            } else {
                defaultSlot.getSensors().add(sensor);
            }
        }
        slots.add(defaultSlot);
        return GeneralResult.ok(slots);
    }
}
