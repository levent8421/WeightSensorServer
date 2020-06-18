package com.berrontech.dsensor.dataserver.common.io;

import com.berrontech.dsensor.dataserver.common.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 14:34
 * Class Name: AbstractAsyncReadConnection
 * Author: Levent8421
 * Description:
 * 异步读取的IO连接
 *
 * @author Levent8421
 */
@Slf4j
public abstract class AbstractAsyncReadConnection implements AsyncReadConnection, Runnable {
    /**
     * 运行标志
     */
    private boolean running = false;
    private final ExecutorService singleThreadPool = ThreadUtils.createSingleThreadPool("AsyncIORead");
    private AsyncReadConnection.ReadListener readListener;
    private InputStream inputStream;
    private final byte[] readBuffer = new byte[getReadBufferSize()];

    @Override
    public void startRead() throws IOException {
        this.inputStream = getInputStream();
        if (this.inputStream == null) {
            throw new IOException(String.format("Error On Get InputStream! class=[%s]", getClass().getName()));
        }
        running = true;
        singleThreadPool.execute(this);
    }

    @Override
    public void stopRead() {
        running = false;
        singleThreadPool.shutdownNow();
    }

    @Override
    public void setListener(ReadListener listener) {
        this.readListener = listener;
    }

    @Override
    public void run() {
        while (running) {
            int availableSize;
            if (isInputReadBlocking()) {
                try {
                    availableSize = inputStream.read(readBuffer);
                } catch (IOException e) {
                    callError(e);
                    continue;
                }
            } else {
                try {
                    availableSize = inputStream.available();
                    if (availableSize > 0) {
                        availableSize = inputStream.read(readBuffer);
                    } else {
                        trySleep();
                    }
                } catch (IOException e) {
                    callError(e);
                    continue;
                }
            }
            if (availableSize > 0) {
                onData(availableSize);
            }
        }
    }

    private void trySleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            log.warn("Error On Call Sleep", e);
        }
    }

    private void onData(int len) {
        final byte[] data = new byte[len];
        System.arraycopy(readBuffer, 0, data, 0, len);
        try {
            readListener.onReadData(data, 0, len);
        } catch (Exception e) {
            log.warn("Error On ReadData Callback", e);
        }
    }

    private void callError(Throwable error) {
        if (this.readListener == null) {
            return;
        }
        try {
            this.readListener.onReadError(error);
        } catch (Exception e) {
            log.warn("Error On ReadError Callback!", e);
        }
    }

    /**
     * 当前连接读取时是否会阻塞
     *
     * @return blocking flag
     */
    protected abstract boolean isInputReadBlocking();

    /**
     * 指定读取缓冲区大小
     *
     * @return buffer size
     */
    protected abstract int getReadBufferSize();
}
