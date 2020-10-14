package com.berrontech.dsensor.dataserver.weight.digitalSensor;

import lombok.Data;

/**
 * @author lastn
 */
@Data
public class SimpleFilter {
    double[] buffer;
    int bufferPos = 0;
    int peakDepth = 1;
    int averageDepth = 3;

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
            double total = 0;
            min = buffer[0];
            max = min;
            for (int pos = 0; pos < bufferPos && pos < buffer.length; pos++) {
                double v = buffer[pos];
                min = Math.min(min, v);
                max = Math.max(max, v);
                total += buffer[pos];
            }
            int count = bufferPos;
            if (bufferPos < calcBufferLength()) {
                // buffer is not full, only calc as average
            } else {
                // buffer is full
                total -= min;
                total -= max;
                count = bufferPos - 2;
            }
            return total / count;
        }
        return 0;
    }

    public double push(double val) {
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
    }
}

