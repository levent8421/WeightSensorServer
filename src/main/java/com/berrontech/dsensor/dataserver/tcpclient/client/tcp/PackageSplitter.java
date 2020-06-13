package com.berrontech.dsensor.dataserver.tcpclient.client.tcp;

/**
 * Create By Levent8421
 * Create Time: 2020/6/11 9:21
 * Class Name: PackageSplitter
 * Author: Levent8421
 * Description:
 * Package Splitter
 *
 * @author Levent8421
 */
public interface PackageSplitter {
    /**
     * add data
     *
     * @param buffer buffer
     * @param pos    start pos
     * @param len    read length
     */
    void appendDate(byte[] buffer, int pos, int len);

    /**
     * Set The Package Find Listener
     *
     * @param listener listener
     */
    void setNewPackageListener(NewPackageListener listener);
}
