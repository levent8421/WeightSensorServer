package com.berrontech.dsensor.dataserver.common.util;

import com.berrontech.dsensor.dataserver.common.exception.CopyException;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

class DeepCopyUtilsTest implements Runnable, Serializable {

    @Test
    void deepCopy() {
        for (int i = 0; i < 10; i++) {
            new Thread(new DeepCopyUtilsTest()).start();
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            try {
                DeepCopyUtils.deepCopy(this);
            } catch (CopyException e) {
                e.printStackTrace();
            }
        }
    }
}