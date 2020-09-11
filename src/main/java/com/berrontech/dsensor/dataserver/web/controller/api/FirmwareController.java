package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.weight.firmware.FirmwareLoader;
import com.berrontech.dsensor.dataserver.weight.firmware.FirmwareResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create By Levent8421
 * Create Time: 2020/9/11 15:27
 * Class Name: FirmwareController
 * Author: Levent8421
 * Description:
 * 传感器固件相关数据访问控制器
 *
 * @author Levent8421
 */
@RestController
@RequestMapping("/api/firmware")
@Slf4j
public class FirmwareController extends AbstractController {
    private final FirmwareLoader firmwareLoader;

    public FirmwareController(FirmwareLoader firmwareLoader) {
        this.firmwareLoader = firmwareLoader;
    }

    /**
     * 加载固件文件到内存
     *
     * @return GR
     */
    @PostMapping("/_load")
    public ResponseEntity<byte[]> loadFirmware() {
        final FirmwareResource resource = firmwareLoader.loadResource().getFirmwareResource();
        log.debug("Firmware name [{}]", resource.getFileName());
        return ResponseEntity.ok(resource.getContent());
    }
}
