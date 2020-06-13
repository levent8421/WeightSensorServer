package com.berrontech.dsensor.dataserver.tcpclient.client.tcp;

/**
 * Create By Levent8421
 * Create Time: 2020/6/11 9:22
 * Class Name: NewPackageListener
 * Author: Levent8421
 * Description:
 * New Package Listener
 *
 * @author Levent8421
 */
public interface NewPackageListener {
    /**
     * Callback when Find A New Package
     *
     * @param packet package
     */
    void onNewPackage(byte[] packet);
}
