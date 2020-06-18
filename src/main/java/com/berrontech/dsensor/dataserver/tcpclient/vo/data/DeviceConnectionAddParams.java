package com.berrontech.dsensor.dataserver.tcpclient.vo.data;

import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/6/16 19:07
 * Class Name: DeviceConnectionAddParams
 * Author: Levent8421
 * Description:
 * 新增连接参数
 *
 * @author Levent8421
 */
@Data
public class DeviceConnectionAddParams {
    /**
     * Type
     */
    private String type;
    /**
     * Target
     */
    private String target;
}
