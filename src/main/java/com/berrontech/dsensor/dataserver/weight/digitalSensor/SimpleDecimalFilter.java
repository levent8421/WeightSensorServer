package com.berrontech.dsensor.dataserver.weight.digitalSensor;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * @author lastn
 */
@Slf4j
@Data
public class SimpleDecimalFilter {
    BigDecimal[] buffer;
    int bufferPos = 0;
    int peakDepth = 1;
    int averageDepth = 3;

    int calcBufferLength() {
        return peakDepth * 2 + averageDepth;
    }

    void prepareBuffer() {
        BigDecimal[] newBuffer = null;
        if (buffer == null || buffer.length != calcBufferLength()) {
            newBuffer = new BigDecimal[calcBufferLength()];
        }
        if (newBuffer != null) {
            if (buffer == null) {
                buffer = newBuffer;
            } else {
                if (buffer.length < newBuffer.length) {
                    // buf: 3, newbuf: 5
                    System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                    bufferPos = buffer.length;
                } else {
                    // buf: 5, newbuf: 3
                    System.arraycopy(buffer, newBuffer.length - buffer.length, newBuffer, 0, newBuffer.length);
                    bufferPos = newBuffer.length;
                }
            }
        }
    }

    BigDecimal calcResult() {
        if (bufferPos > 0) {
            BigDecimal min, max;
            BigDecimal total;
            min = buffer[0];
            max = min;
            total = min;
            for (int pos = 1; pos < bufferPos && pos < buffer.length; pos++) {
                BigDecimal v = buffer[pos];
                if (v.compareTo(min) < 0) {
                    min = v;
                }
                if (v.compareTo(max) > 0) {
                    max = v;
                }
                total = total.add(v);
            }
            int count = bufferPos;
            if (bufferPos < calcBufferLength() || peakDepth <= 0) {
                // buffer is not full, only calc as average
            } else {
                // buffer is full, and has peak filter
                total = total.subtract(min);
                total = total.subtract(max);
                count -= 2;
            }
            return total.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal push(BigDecimal val) {
        try {
            prepareBuffer();
            if (bufferPos >= buffer.length) {
                bufferPos = buffer.length;
                // remove old value
                System.arraycopy(buffer, 1, buffer, 0, buffer.length - 1);
                // set new value to end
                buffer[buffer.length - 1] = val;
            } else {
                // set new value to end
                buffer[bufferPos++] = val;
            }
            return calcResult();
        } catch (Exception ex) {
            log.warn("push decimal value({}) error", val, ex);
        }
        return BigDecimal.ZERO;
    }


    public void setDepth(int depth) {
        if (depth <= 0) {
            setPeakDepth(0);
            setAverageDepth(1);
        } else {
            setPeakDepth(1);
            setAverageDepth(depth);
        }
    }

}
