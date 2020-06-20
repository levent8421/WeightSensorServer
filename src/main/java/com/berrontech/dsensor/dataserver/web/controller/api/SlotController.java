package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractEntityController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    protected SlotController(SlotService slotService) {
        super(slotService);
        this.slotService = slotService;
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
}
