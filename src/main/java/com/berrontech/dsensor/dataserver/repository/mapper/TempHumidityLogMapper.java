package com.berrontech.dsensor.dataserver.repository.mapper;

import com.berrontech.dsensor.dataserver.common.entity.TempHumidityLog;
import com.berrontech.dsensor.dataserver.repository.AbstractMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Create By Levent8421
 * Create Time: 2020/9/24 14:17
 * Class Name: TempHumidityLogMapper
 * Author: Levent8421
 * Description:
 * 温湿度日志相关数据库访问组件
 *
 * @author Levent8421
 */
@Repository
public interface TempHumidityLogMapper extends AbstractMapper<TempHumidityLog> {
    /**
     * 清除N个小时前的日志
     *
     * @param logRetentionHours 小时
     * @return rows
     */
    int cleanLog(@Param("logRetentionHours") int logRetentionHours);
}
