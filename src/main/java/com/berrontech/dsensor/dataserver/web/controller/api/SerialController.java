package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import com.berrontech.dsensor.dataserver.weight.DataPacket;
import com.berrontech.dsensor.dataserver.weight.SensorController;
import com.berrontech.dsensor.dataserver.weight.serial.SerialException;
import com.berrontech.dsensor.dataserver.weight.serial.SerialSensorController;
import com.berrontech.dsensor.dataserver.weight.serial.util.SerialUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
    private SensorController sensorController;

    @GetMapping("/sensor-connect")
    public GeneralResult<?> connectSensor(@RequestParam("port") String port) throws SerialException, IOException {
        sensorController = new SerialSensorController(port, 115200);
        sensorController.startRead();
        return GeneralResult.ok();
    }

    @GetMapping("/sensor-send")
    public GeneralResult<?> testSensorSend() throws IOException {
        DataPacket packet = new DataPacket();
        packet.setVersion(0x21);
        packet.setAddress(0x00);
        packet.setCommand(0x72);

        sensorController.send(packet);
        return GeneralResult.ok();
    }

    @GetMapping("/scan")
    public GeneralResult<List<String>> scanSerialPorts() {
        final List<String> ports = SerialUtils.scan();
        return GeneralResult.ok(ports);
    }
}
