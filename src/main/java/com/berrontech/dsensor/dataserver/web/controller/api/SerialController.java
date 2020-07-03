package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.util.NativeUtils;
import com.berrontech.dsensor.dataserver.common.util.TextUtils;
import com.berrontech.dsensor.dataserver.conf.SerialConfiguration;
import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.web.vo.LoadLibraryParam;
import com.berrontech.dsensor.dataserver.weight.serial.util.SerialUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/6/17 17:18
 * Class Name: SerialController
 * Author: Levent8421
 * Description:
 * Serial Test Controller
 *
 * @author Levent8421
 */
@Slf4j
@RestController
@RequestMapping("/api/serial")
public class SerialController extends AbstractController {
    private final SerialConfiguration serialConfiguration;

    public SerialController(SerialConfiguration serialConfiguration) {
        this.serialConfiguration = serialConfiguration;
    }

    /**
     * 扫描串口
     *
     * @return GR
     */
    @GetMapping("/scan")
    public GeneralResult<List<String>> scanSerialPorts() {
        final List<String> ports = SerialUtils.scan();
        return GeneralResult.ok(ports);
    }

    /**
     * 加载本地库
     *
     * @param param path
     * @return GR
     */
    @PostMapping("/load-lib")
    public GeneralResult<String> loadLibrary(@RequestBody LoadLibraryParam param) {
        final String path;
        if (param == null || TextUtils.isTrimedEmpty(param.getLibPath())) {
            path = serialConfiguration.getLibPath();
        } else {
            path = param.getLibPath();
        }
        try {
            NativeUtils.loadLibrary(path);
        } catch (Throwable e) {
            log.warn("Error on load lib[{}]", path, e);
            return GeneralResult.error("Can not load lib[" + path + "], error=" + e);
        }
        return GeneralResult.ok(path);
    }
}
