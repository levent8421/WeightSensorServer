package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Create By Levent8421
 * Create Time: 2020/6/24 15:56
 * Class Name: DashboardController
 * Author: Levent8421
 * Description:
 * 数据看板相关数据访问控制器
 *
 * @author Levent8421
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController extends AbstractController {
    private final WeightDataHolder weightDataHolder;

    public DashboardController(WeightDataHolder weightDataHolder) {
        this.weightDataHolder = weightDataHolder;
    }

    /**
     * 获取当前实时称重数据
     *
     * @return GR with weight data
     */
    @GetMapping("/slot-data")
    public GeneralResult<Map<String, MemorySlot>> slotData() {
        return GeneralResult.ok(weightDataHolder.getSlotTable());
    }
}
