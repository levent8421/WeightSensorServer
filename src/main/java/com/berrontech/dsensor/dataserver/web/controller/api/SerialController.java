package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.weight.serial.util.SerialUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
@RequestMapping("/api/serial")
public class SerialController extends AbstractController {
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
}
