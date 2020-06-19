package com.berrontech.dsensor.dataserver.repository.mapper;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.repository.AbstractMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Create By Levent8421
 * Create Time: 2020/6/16 18:17
 * Class Name: DeviceConnectionMapper
 * Author: Levent8421
 * Description:
 * 设备连接相关数据库访问组件
 *
 * @author Levent8421
 */
@Repository
public interface DeviceConnectionMapper extends AbstractMapper<DeviceConnection> {
    /**
     * 通过type和target查询数量
     *
     * @param type   type
     * @param target target
     * @return count
     */
    int countByTypeAndTarget(@Param("type") Integer type, @Param("target") String target);
}
