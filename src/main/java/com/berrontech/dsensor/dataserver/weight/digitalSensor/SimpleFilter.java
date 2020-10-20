package com.berrontech.dsensor.dataserver.weight.digitalSensor;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lastn
 */
@Slf4j
@Data
public class SimpleFilter {
    double[] buffer;
    int bufferPos = 0;
    int peakDepth = 0;
    int averageDepth = 1;

    int calcBufferLength() {
        return peakDepth * 2 + averageDepth;
    }

    void prepareBuffer() {
        double[] newBuffer = null;
        if (buffer == null || buffer.length != calcBufferLength()) {
            newBuffer = new double[calcBufferLength()];
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

    double calcResult() {
        if (bufferPos > 0) {
            double min, max;
            double total;
            min = buffer[0];
            max = min;
            total = min;
            for (int pos = 1; pos < bufferPos && pos < buffer.length; pos++) {
                double v = buffer[pos];
                min = Math.min(min, v);
                max = Math.max(max, v);
                total += buffer[pos];
            }
            int count = bufferPos;
            if (bufferPos < calcBufferLength() || peakDepth <= 0) {
                // buffer is not full, only calc as average
            } else {
                // buffer is full, and has peak filter
                total -= min;
                total -= max;
                count -= 2;
            }
            return total / count;
        }
        return 0;
    }

    public double push(double val) {
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
            log.warn("push double value({}) error", val, ex);
        }
        return 0;
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

