package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractEntityController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@RestController
@RequestMapping("/api/slot")
public class SlotController extends AbstractEntityController<Slot> {
    private final SlotService slotService;
    private final WeightController weightController;
    private final WeightSensorService weightSensorService;

    protected SlotController(SlotService slotService,
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
}
