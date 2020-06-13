package com.berrontech.dsensor.dataserver.common.io;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 15:05
 * Class Name: AbstractPackageReadConnection
 * Author: Levent8421
 * Description:
 * 分包数据读取基类
 *
 * @author Levent8421
 */
public abstract class AbstractPackageReadConnection extends AbstractAsyncReadConnection implements
        PackageReadConnection, AsyncReadConnection.ReadListener, PackageReadConnection.PackageListener {
    private PackageReadConnection.PackageSplitter packageSplitter;

    public AbstractPackageReadConnection() {
        setListener(this);
        this.packageSplitter = getSplitter();
        this.packageSplitter.setListener(this);
    }

    @Override
    public void onReadData(byte[] data, int startPos, int len) {
        this.packageSplitter.appendData(data, startPos, len);
    }

    /**
     * 指定分包器
     *
     * @return package splitter
     */
    protected abstract PackageReadConnection.PackageSplitter getSplitter();
}
