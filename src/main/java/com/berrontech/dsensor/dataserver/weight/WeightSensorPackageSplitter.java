package com.berrontech.dsensor.dataserver.weight;

import com.berrontech.dsensor.dataserver.common.io.PackageReadConnection;
import com.berrontech.dsensor.dataserver.common.util.ByteUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 15:24
 * Class Name: WeightSensorPackageSplitter
 * Author: Levent8421
 * Description:
 * 数字城中传感器 分包器
 *
 * @author Levent8421
 */
@Slf4j
public class WeightSensorPackageSplitter implements PackageReadConnection.PackageSplitter {
    private static final byte STATUS_PACKAGE_START_1 = 0x01;
    private static final int STATUS_PACKAGE_START_2 = 0x02;
    private static final int STATUS_PACKAGE_LENGTH = 0x03;
    private static final int STATUS_PACKAGE_DATA = 0x04;

    /**
     * 10 K Byte Buffer
     */
    private final byte[] buffer = new byte[255 * 2];
    private int dataIndex = 0;
    private PackageReadConnection.PackageListener packageListener;
    private int status = STATUS_PACKAGE_START_1;
    private int dataLength;

    @Override
    public void appendData(byte[] data, int startPos, int len) {
        for (int i = 0; i < startPos + len; i++) {
            final byte b = data[i];
            switch (status) {
                case STATUS_PACKAGE_START_1:
                    if (b == DataPacket.PACKAGE_START_1) {
                        this.status = STATUS_PACKAGE_START_2;
                    } else {
                        log.warn("Invalidate Package Start1 [0x{}]", Integer.toHexString(b));
                        reset();
                    }
                    break;
                case STATUS_PACKAGE_START_2:
                    final int ib = ByteUtils.byte2Int(b);
                    if (ib == DataPacket.PACKAGE_START_2) {
                        this.status = STATUS_PACKAGE_LENGTH;
                    } else {
                        log.warn("Invalidate Package Start2 [0x{}]", Integer.toHexString(ib));
                        reset();
                    }
                    break;
                case STATUS_PACKAGE_LENGTH:
                    this.dataLength = ByteUtils.byte2Int(b);
                    this.status = STATUS_PACKAGE_DATA;
                    break;
                case STATUS_PACKAGE_DATA:
                    this.buffer[this.dataIndex++] = b;
                    final int readDataLen = this.dataIndex;
                    if (readDataLen >= dataLength) {
                        onNewPackage(dataLength);
                        reset();
                    }
                    break;
                default:
                    log.error("Invalidate Status [{}]", status);
                    reset();
                    break;
            }
        }
    }

    private void onNewPackage(int dataLength) {
        final byte[] packet = new byte[dataLength];
        System.arraycopy(buffer, 0, packet, 0, dataLength);
        try {
            this.packageListener.onNewPackage(packet);
        } catch (Exception e) {
            log.error("Error on call new package callback", e);
        }
    }

    private void reset() {
        this.dataIndex = 0;
        this.dataLength = 0;
        this.status = STATUS_PACKAGE_START_1;
    }

    @Override
    public void setListener(PackageReadConnection.PackageListener listener) {
        this.packageListener = listener;
    }
}
