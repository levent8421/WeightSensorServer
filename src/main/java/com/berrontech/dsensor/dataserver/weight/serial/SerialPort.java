/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.berrontech.dsensor.dataserver.weight.serial;

import com.berrontech.dsensor.dataserver.common.util.OSUtils;
import com.berrontech.dsensor.dataserver.weight.serial.util.SerialUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Arrays;
import java.util.List;


/**
 * Create By Levent8421
 * Create Time: 2020/6/18 14:07
 * Class Name: SerialPort
 * Author: Levent8421
 * Description:
 * Serial Port
 *
 * @author Levent8421
 */
@Slf4j
public class SerialPort {
    private static final int OPEN_SERIAL_FLAG = 0;

    File device;
    int baudrate;

    /**
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    @SuppressWarnings("all")
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public SerialPort(File device, int baudrate) {
        this.device = device;
        this.baudrate = baudrate;
    }

    public void open() throws Exception {
        int flags = OPEN_SERIAL_FLAG;
        mFd = open0(device.getAbsolutePath(), baudrate, flags);
        if (mFd == null) {
            throw new IOException("Open Device File Descriptor failed!");
        }
        if (isStreamSupported()) {
            mFileInputStream = new FileInputStream(mFd);
            mFileOutputStream = new FileOutputStream(mFd);
        }
    }

    public void close() throws Exception {
        if (mFileOutputStream != null) {
            mFileOutputStream.flush();
            mFileOutputStream.close();
        }
        if (mFileInputStream != null) {
            mFileInputStream.close();
        }
        close0();
    }

    public int read(byte[] buffer, int offset, int length) throws Exception {
        if (isStreamSupported()) {
            return doStreamRead(buffer, offset, length);
        } else {
            return read0(buffer, offset, length);
        }
    }


    public int write(byte[] buffer) throws Exception {
        return write(buffer, 0, buffer.length);
    }

    public int write(byte[] buffer, int offset, int length) throws Exception {
        if (isStreamSupported()) {
            return doStreamWrite(buffer, offset, length);
        } else {
            return write0(buffer, offset, length);
        }
    }

    private int doStreamRead(byte[] buffer, int offset, int length) throws Exception {
        int cnt = mFileInputStream.available();
        cnt = Math.min(cnt, length);
        cnt = mFileInputStream.read(buffer, offset, cnt);
        return Math.max(cnt, 0);
    }

    private int doStreamWrite(byte[] buffer, int offset, int length) throws Exception {
        if (mFileOutputStream != null) {
            mFileOutputStream.write(buffer, offset, length);
        }
        return length;
    }

    private boolean isStreamSupported() {
        return !OSUtils.isWindows();
    }


    /**
     * Native Open Serial Port
     *
     * @param path     device file path
     * @param baudrate baud Rate
     * @param flags    open device file with this flag
     * @return FD
     */
    private native static FileDescriptor open0(String path, int baudrate, int flags);

    /**
     * Native Close Method
     */
    public native void close0();

    private native int read0(byte[] buffer, int offset, int length);

    private native int write0(byte[] buffer, int offset, int length);

    public static List<String> findSerialPorts() throws IOException {
        if (OSUtils.isWindows()) {
            return Arrays.asList(findSerialPortDevices0());
        }
        return SerialUtils.scanLinuxPorts();
    }

    /**
     * Find System Serial Port devices
     *
     * @return device <code>AbsolutePath</code>
     */
    private native static String[] findSerialPortDevices0();
}
