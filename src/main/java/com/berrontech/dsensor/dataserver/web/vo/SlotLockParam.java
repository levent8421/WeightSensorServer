package com.berrontech.dsensor.dataserver.web.vo;

import lombok.Data;

/**
 * 接收到的货道锁定密码
 *
 * @author 黄荷翔
 * @date 2021/3/2 16:27
 */
@Data
public class SlotLockParam {
    private String password;
    private Integer slotId;
    private Boolean indivisible;
}
