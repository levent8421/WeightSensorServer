package com.berrontech.dsensor.dataserver.tcpclient.vo.data;

import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/6/11 11:20
 * Class Name: RuokVo
 * Author: Levent8421
 * Description:
 * RUOK Value Object
 *
 * @author Levent8421
 */
@Data
public class RuokVo {
    private Integer pid;
    private String appVersion;
    private String dbVersion;
    private String connectionStatus;
}
