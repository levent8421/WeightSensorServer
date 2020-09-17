package com.berrontech.dsensor.dataserver.common.util;

import com.berrontech.dsensor.dataserver.common.exception.CopyException;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightData;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

class DeepCopyUtilsTest implements Runnable, Serializable {

    @Test
    void deepCopy() {
//        for (int i = 0; i < 1; i++) {
//            new Thread(new DeepCopyUtilsTest()).start();
//        }
        run();
    }

    @Override
    public void run() {
        final MemorySlot memorySlot = new MemorySlot();
        final MemoryWeightData data = new MemoryWeightData();
        data.setWeight(123);
        memorySlot.setData(data);
        for (int i = 0; i < 20; i++) {
            try {
                final MemorySlot copy = DeepCopyUtils.deepCopy(memorySlot);
                final MemoryWeightData copyData = copy.getData();
                if (copyData == null) {
                    throw new InternalServerErrorException("Copy lost data");
                }
                System.out.println(data.getWeight());
            } catch (CopyException e) {
                e.printStackTrace();
            }
        }
    }
}