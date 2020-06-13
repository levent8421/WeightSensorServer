package com.berrontech.dsensor.dataserver.weight.serial;

import com.berrontech.dsensor.dataserver.weight.DataPacket;
import org.junit.jupiter.api.Test;

class SerialSensorControllerTest {
    @Test
    public void test() throws Exception {
        SerialSensorController controller = new SerialSensorController("COM21", 115200);
        controller.startRead();

        DataPacket packet = new DataPacket();
        packet.setVersion(0x21);
        packet.setAddress(0x00);
        packet.setCommand(0x72);

        controller.send(packet);
        Thread.sleep(1000000);
    }
}