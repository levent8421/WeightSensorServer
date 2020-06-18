package com.berrontech.dsensor.dataserver.weight.holder;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Create By Levent8421
 * Create Time: 2020/6/18 19:33
 * Class Name: WeightDataHolder
 * Author: Levent8421
 * Description:
 * 称重数据内存保持器
 *
 * @author Levent8421
 */
@Data
@Component
@Scope("singleton")
public class WeightDataHolder {
    /**
     * 货道表
     */
    private Map<String, MemorySlot> slotTable;
}
