package com.berrontech.dsensor.dataserver.repository.mapper;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.repository.AbstractMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    /**
     * 通过物料号或物料名称搜索
     *
     * @param skuNo   物料号
     * @param skuName 物料名称
     * @return 货道列表
     */
    List<Slot> selectBySkuLike(@Param("skuNo") String skuNo, @Param("skuName") String skuName);

    /**
     * 更新货道状态
     *
     * @param id    id
     * @param state state
     * @return rows
     */
    int updateState(@Param("id") Integer id, @Param("state") int state);

    /**
     * 设置货道是否存在电子标签
     *
     * @param id        id
     * @param hasElabel 是否存在电子标签
     * @return rows
     */
    int updateHasELable(@Param("id") Integer id, @Param("hasElable") Boolean hasElabel);

    /**
     * Select Slot by address (original address)
     *
     * @param address address
     * @return slot
     */
    Slot selectByAddress(@Param("address") int address);

    /**
     * Delete By Address List
     *
     * @param addressList Address List
     * @return rows
     */
    int deleteByAddress(@Param("addressList") List<Integer> addressList);

    /**
     * select slot where slot.address in ()addrList
     *
     * @param addrList addrList
     * @return slots
     */
    List<Slot> selectByAddressList(@Param("addrList") List<Integer> addrList);

    /**
     * select sub slots and primary slot by primary slot id
     *
     * @param id primary slot id
     * @return slots
     */
    List<Slot> selectSlotGroupByPrimarySlot(@Param("id") Integer id);
}
