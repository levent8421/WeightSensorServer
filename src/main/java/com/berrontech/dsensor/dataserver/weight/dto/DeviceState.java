package com.berrontech.dsensor.dataserver.weight.dto;

import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/10/9 14:17
 * Class Name: DeviceState
 * Author: Levent8421
 * Description:
 * 设备状态
 *
 * @author Levent8421
 */
@Data
public class DeviceState {
    /**
     * 连接ID
     */
    private Integer connectionId;
    /**
     * 物理地址
     */
    private Integer address;
    /**
     * 设备状态：参考AbstractDevice485
     */
    private Integer deviceState;
    /**
     * 是否存在电子标签
     */
    private boolean hasElabel = false;
    /**
     * 电子标签状态：参考AbstractDevice485
     */
    private Integer eLabelState;
}
