package com.berrontech.dsensor.dataserver.repository.mapper;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.repository.AbstractMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Create By Levent8421
 * Create Time: 2020/6/17 12:08
 * Class Name: SlotMapper
 * Author: Levent8421
 * Description:
 * 货位相关数据库访问组件
 *
 * @author Levent8421
 */
@Repository
public interface SlotMapper extends AbstractMapper<Slot> {
    /**
     * 通过SlotNo更新Sku信息
     *
     * @param slot slot
     * @return rows
     */
    int updateSkuInfoBySlotNo(@Param("slot") Slot slot);

    /**
     * Update Slot No By Id
     *
     * @param id     id
     * @param slotNo SlotNo
     * @return rows
     */
    int updateSlotNoById(@Param("id") Integer id, @Param("slotNo") String slotNo);
}
