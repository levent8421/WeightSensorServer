package com.berrontech.dsensor.dataserver.tcpclient.client.tcp.impl;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.tcpclient.client.tcp.NewPackageListener;
import com.berrontech.dsensor.dataserver.tcpclient.client.tcp.PackageSplitter;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/6/11 9:26
 * Class Name: BytePackageSplitter
 * Author: Levent8421
 * Description:
 * Package Splitter
 *
 * @author Levent8421
 */
@Component
public class BytePackageSplitter implements PackageSplitter {
    /**
     * Default Buffer Size=1M Byte
     */
    private byte[] recvBuffer = new byte[1024 * 1024];
    private int currentPos = 0;
    private NewPackageListener listener;

    @Override
    public void appendDate(byte[] buffer, int pos, int len) {
        for (int i = pos; i < pos + len; i++) {
            final byte b = buffer[i];
            if (b == ApplicationConstants.Message.PACKAGE_START) {
                currentPos = 0;
            }
            recvBuffer[currentPos] = b;
            if (b == ApplicationConstants.Message.PACKAGE_END) {
                this.callListener(recvBuffer, this.currentPos + 1);
                this.currentPos = 0;
            }
            currentPos++;
        }
    }

    @Override
    public void setNewPackageListener(NewPackageListener listener) {
        this.listener = listener;
    }

    private void callListener(byte[] buffer, int len) {
        if (listener == null) {
            return;
        }
        final byte[] packet = new byte[len];
        System.arraycopy(buffer, 0, packet, 0, len);
        listener.onNewPackage(packet);
    }
}
