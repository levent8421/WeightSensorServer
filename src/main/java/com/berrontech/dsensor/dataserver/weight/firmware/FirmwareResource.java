package com.berrontech.dsensor.dataserver.weight.firmware;

import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/9/11 14:45
 * Class Name: FirmwareResource
 * Author: Levent8421
 * Description:
 * 固件资源
 *
 * @author Levent8421
 */
@Data
public class FirmwareResource {
    private String fileName;
    private byte[] content;
}
