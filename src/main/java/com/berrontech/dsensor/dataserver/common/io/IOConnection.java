package com.berrontech.dsensor.dataserver.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 10:34
 * Class Name: IOConnection
 * Author: Levent8421
 * Description:
 * Basic IO Connection
 *
 * @author Levent8421
 */
public interface IOConnection {
    /**
     * 获取输入流
     *
     * @return ins
     * @throws IOException IOE
     */
    InputStream getInputStream() throws IOException;

    /**
     * 获取输出流
     *
     * @return outs
     * @throws IOException IOE
     */
    OutputStream getOutputStream() throws IOException;

}
