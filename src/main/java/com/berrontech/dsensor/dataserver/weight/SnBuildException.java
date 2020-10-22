package com.berrontech.dsensor.dataserver.weight;

import lombok.Getter;

/**
 * Create By Levent8421
 * Create Time: 2020/10/22 10:53
 * Class Name: SnBuildException
 * Author: Levent8421
 * Description:
 * 分配SN错误异常
 *
 * @author Levent8421
 */
public class SnBuildException extends Exception {
    @Getter
    private final Integer connectionId;
    @Getter
    private final Integer address;
    @Getter
    private final String sn;

    public SnBuildException(String msg, Integer connectionId, Integer address, String sn) {
        super(msg);
        this.address = address;
        this.connectionId = connectionId;
        this.sn = sn;
    }

    public SnBuildException(Integer connectionId, Integer address, String sn) {
        this("Error on rebuild SN!", connectionId, address, sn);
    }
}
