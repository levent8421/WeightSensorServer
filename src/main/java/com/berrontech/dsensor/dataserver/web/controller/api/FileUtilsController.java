package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.service.general.DeviceConnectionService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.weight.serial.util.SerialDeviceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Create By Levent8421
 * Create Time: 2020/12/17 19:15
 * Class Name: FileUtilsController
 * Author: Levent8421
 * Description:
 * File Utils Controller
 *
 * @author Levent8421
 */
@RestController
@RequestMapping("/api/file")
@Slf4j
public class FileUtilsController extends AbstractController {
    private final DeviceConnectionService deviceConnectionService;

    public FileUtilsController(DeviceConnectionService deviceConnectionService) {
        this.deviceConnectionService = deviceConnectionService;
    }

    @PostMapping("/_info")
    public GeneralResult<?> fileInfo(@RequestParam("filename") String filename) throws IOException {
        final File file = new File(filename);
        final String f1 = file.getCanonicalFile().getAbsolutePath();
        final String f2 = file.getAbsolutePath();
        log.debug("[{}]/[{}]", f1, f2);
        return GeneralResult.ok(f1);
    }

    @GetMapping("/_connection-id")
    public GeneralResult<String> connectionId(@RequestParam("id") Integer id) throws IOException {
        final DeviceConnection connection = deviceConnectionService.require(id);
        if (!Objects.equals(connection.getType(), DeviceConnection.TYPE_SERIAL)) {
            throw new BadRequestException("This connection is not a serial connection!");
        }
        final String deviceName = connection.getTarget();
        final String usbId = SerialDeviceUtils.getUsbTtyDeviceId(deviceName);
        return GeneralResult.ok(usbId);
    }
}
