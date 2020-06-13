package com.berrontech.dsensor.dataserver.common.io;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 14:26
 * Class Name: PackageReadConnection
 * Author: Levent8421
 * Description:
 * 分包读取连接
 *
 * @author Levent8421
 */
public interface PackageReadConnection extends AsyncReadConnection {
    /**
     * 分包器
     */
    interface PackageSplitter {
        /**
         * Append Data
         *
         * @param data     data
         * @param startPos start index
         * @param len      data length
         */
        void appendData(byte[] data, int startPos, int len);

        /**
         * Set Package Listener
         *
         * @param listener package listener
         */
        void setListener(PackageListener listener);
    }

    /**
     * 新数据包回调
     */
    @FunctionalInterface
    interface PackageListener {
        /**
         * 新数据包回调
         *
         * @param packet 数据包
         */
        void onNewPackage(byte[] packet);

        /**
         * On Error
         *
         * @param error error Exception
         */
        default void onReadPackageError(Throwable error) {
            // Do Nothing
        }
    }
}
