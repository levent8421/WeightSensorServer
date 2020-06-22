package com.berrontech.dsensor.dataserver.tcpclient.vo.data;

import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/6/22 18:07
 * Class Name: BalanceStateParams
 * Author: Levent8421
 * Description:
 * 设备货道状态参数
 *
 * @author Levent8421
 */
@Data
public class BalanceStateParams {
    private String slotNo;
    private String state;
}
