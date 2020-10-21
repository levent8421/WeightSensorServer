package com.berrontech.dsensor.dataserver.web.vo;

import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/9/17 15:21
 * Class Name: FirmwareUpgradeProgress
 * Author: Levent8421
 * Description:
 * 固件升级
 *
 * @author Levent8421
 */
@Data
public class FirmwareUpgradeProgress {
    public static final int MODE_SENSOR = 0x01;
    public static final int MODE_E_LABEL = 0x02;
    public static final int STATE_INIT = 0x00;
    public static final int STATE_SUCCESS = 0x01;
    public static final int STATE_FAIL = 0x02;
    public static final int STATE_PROGRESS = 0x03;
    private Integer address;
    private int total;
    private int current;
    private int state;
    private int upgradeMode;
}
