package com.berrontech.dsensor.dataserver.common.io;

import com.berrontech.dsensor.dataserver.common.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Create By Levent8421
 * Create Time: 2020/7/28 11:27
 * Class Name: FileWatcher
 * Author: Levent8421
 * Description:
 * 文件监听器
 *
 * @author Levent8421
 */
@Slf4j
public class FileWatcher implements Runnable {
    private final static int CHECK_INTERVAL = 100;
    private boolean shutdown = false;
    private boolean running = false;
    private final ExecutorService threadPool;
    private TailListener tailListener;
    private long lastModify = -1;
    private long readOffset = 0;
    private FileInputStream fileInputStream;
    private final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

    public interface TailListener {

    }

    private final File file;

    public FileWatcher(File file) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IOException("File can not be found");
        }
        this.fileInputStream = new FileInputStream(file);
        this.file = file;
        this.threadPool = ThreadUtils.createSingleThreadPool("FileWatcher");
    }

    private void checkState() {
        if (shutdown) {
            throw new IllegalStateException("This watcher is shutdown now!");
        }
    }

    private void checkRunning() {
        if (running) {
            throw new IllegalStateException("This watcher is running now!");
        }
    }

    public void tail(TailListener listener) {
        checkState();
        checkRunning();
        if (listener == null) {
            throw new IllegalArgumentException("Null tail listener!");
        }
        this.tailListener = listener;
        this.readOffset = file.length();
        startTailThread();
    }

    private void startTailThread() {
        threadPool.submit(this);
    }

    @Override
    public void run() {
        while (running) {
            final long modifyTime = file.lastModified();
            if (lastModify < modifyTime) {
                lastModify = modifyTime;
//                tryReadTail();
            }
            trySleep();
        }
    }

    private void trySleep() {
        try {
            Thread.sleep(CHECK_INTERVAL);
        } catch (InterruptedException e) {
            log.warn("FileWatcher try sleep fail! [{}:{}]", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void tryReadTail() throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        if (readOffset < file.length()) {
            fileInputStream.reset();
            final long skiped = fileInputStream.skip(readOffset);
            if (skiped != readOffset) {
                log.warn("Actual skip=[{}], except skip=[{}]", skiped, readOffset);
            }
            byteBuffer.reset();
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = fileInputStream.read(buffer)) > 0) {

            }
        }
        readOffset = file.length();
    }

    public void shutdown() {
        if (running) {
            running = false;
        }
        if (!shutdown) {
            shutdown = true;
            threadPool.shutdownNow();
        }
    }
}
