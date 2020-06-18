package com.berrontech.dsensor.dataserver.tcpclient.vo.data;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import lombok.Data;
import lombok.val;

/**
 * Create By Levent8421
 * Create Time: 2020/6/17 15:50
 * Class Name: DeviceConnectionVo
 * Author: Levent8421
 * Description:
 * 设备连接视图类
 *
 * @author Levent8421
 */
@Data
public class DeviceConnectionVo {
    public static DeviceConnectionVo of(DeviceConnection connection) {
        val vo = new DeviceConnectionVo();
        vo.setId(connection.getId());
        vo.setType(DeviceConnection.TYPE_NAME_TABLE.get(connection.getType()));
        vo.setTarget(connection.getTarget());
        return vo;
    }

    private Integer id;
    private String type;
    private String target;
}
