package com.berrontech.dsensor.dataserver.repository.mapper;

import com.berrontech.dsensor.dataserver.common.entity.WeightDataRecord;
import com.berrontech.dsensor.dataserver.repository.AbstractMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Create By Levent8421
 * Create Time: 2020/12/8 17:29
 * Class Name: WeightDataRecordMapper
 * Author: Levent8421
 * Description:
 * 重力传感器数据记录表
 *
 * @author Levent8421
 */
@Repository
public interface WeightDataRecordMapper extends AbstractMapper<WeightDataRecord> {
    /**
     * 删除记录
     *
     * @param keepDays 保存时间
     * @return rows
     */
    int deleteOnCreateTimeBefore(@Param("keepDays") int keepDays);
}
