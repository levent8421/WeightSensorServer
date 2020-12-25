package com.berrontech.dsensor.dataserver.tcpclient.vo.data;

import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/6/10 16:49
 * Class Name: Heartbeat
 * Author: Levent8421
 * Description:
 * Heartbeat Payload
 *
 * @author Levent8421
 */
@Data
public class Heartbeat {
    /**
     * Alive Flag
     */
    private Boolean alive;
    /**
     * AppName
     */
    private String appName;
    /**
     * Timestamp
     */
    private String timestamp;
    /**
     * Database Version
     */
    private String dbVersion;
    /**
     * Application Version
     */
    private String appVersion;
    /**
     * StationId
     */
    private String stationId;
}
