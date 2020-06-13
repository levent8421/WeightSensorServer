package com.berrontech.dsensor.dataserver.common.io;

import java.io.IOException;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 13:54
 * Class Name: AsyncReadConnection
 * Author: Levent8421
 * Description:
 * Async Read Connection
 *
 * @author Levent8421
 */
public interface AsyncReadConnection extends IOConnection {
    /**
     * Start Read Task
     *
     * @throws IOException ioe
     */
    void startRead() throws IOException;

    /**
     * 停止读取
     */
    void stopRead();

    /**
     * Set Read Listener
     *
     * @param listener listener
     */
    void setListener(ReadListener listener);

    /**
     * Data Listener
     */
    interface ReadListener {
        /**
         * 读取到数据时调用
         *
         * @param data     data
         * @param startPos start position
         * @param len      data len
         */
        void onReadData(byte[] data, int startPos, int len);

        /**
         * 读取错误时调用
         *
         * @param error error
         */
        default void onReadError(Throwable error) {
            //Do Nothing
        }
    }
}
