package com.berrontech.dsensor.dataserver.common.io;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;

import java.io.*;

/**
 * Create By Levent8421
 * Create Time: 2020/7/4 18:38
 * Class Name: IOUtils
 * Author: Levent8421
 * Description:
 * IO相关工具类
 *
 * @author Levent8421
 */
public class IOUtils {
    private static final int BUFFER_SIZE = 1024;

    /**
     * Do not instance this class
     */
    private IOUtils() {
    }

    /**
     * 读取文件内容为字符串
     *
     * @param source 文件
     * @return content text
     * @throws IOException IOE
     */
    public static String readAsString(File source) throws IOException {
        final byte[] bytes = readAsBytes(source);
        return new String(bytes, ApplicationConstants.Context.DEFAULT_CHARSET);
    }

    /**
     * 读取文件内容为bytes
     *
     * @param source 文件
     * @return bytes
     * @throws IOException any exception
     */
    public static byte[] readAsBytes(File source) throws IOException {
        try (final FileInputStream fis = new FileInputStream(source)) {
            return readAsBytes(fis);
        }
    }

    public static byte[] readAsBytes(InputStream in) throws IOException {
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            final byte[] buffers = new byte[1024];
            int readLen;
            while ((readLen = in.read(buffers)) > 0) {
                bos.write(buffers, 0, readLen);
            }
            return bos.toByteArray();
        }
    }
}
