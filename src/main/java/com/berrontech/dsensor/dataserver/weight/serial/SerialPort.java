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

import lombok.extern.slf4j.Slf4j;

import java.io.*;


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
    /**
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    @SuppressWarnings("all")
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
        if (!device.exists()) {
            throw new FileNotFoundException("File " + device.getAbsolutePath() + " not found!");
        }
        if (!device.canRead() || !device.canWrite()) {
            throw new IOException("Device file can not be read or write!");
        }

        mFd = open(device.getAbsolutePath(), baudrate, flags);
        if (mFd == null) {
            throw new IOException("Open Device File Descriptor failed!");
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    /**
     * Get InoutStream for this serial port
     *
     * @return Stream
     */
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    /**
     * Get OutputStream for this serial port
     *
     * @return stream
     */
    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    /**
     * Native Open Serial Port
     *
     * @param path     device file path
     * @param baudrate baud Rate
     * @param flags    open device file with this flag
     * @return FD
     */
    private native static FileDescriptor open(String path, int baudrate, int flags);

    /**
     * Native Close Method
     */
    public native void close();
}
