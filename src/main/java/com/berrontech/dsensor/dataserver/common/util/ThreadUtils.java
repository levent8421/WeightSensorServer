package com.berrontech.dsensor.dataserver.common.util;

import java.util.concurrent.*;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 14:36
 * Class Name: ThreadUtils
 * Author: Levent8421
 * Description:
 * 线程工具类
 *
 * @author Levent8421
 */
public class ThreadUtils {
    /**
     * 创建单线程 线程池
     *
     * @param threadName 线程名称
     * @return 线程池（单线程）
     */
    public static ExecutorService createSingleThreadPool(String threadName) {
        return createThreadPoolExecutorService(1, 1, threadName);
    }

    /**
     * 创建线程池
     *
     * @param poolCoreSize 核心线程数量
     * @param poolMaxSize  最大线程数量
     * @param threadName   线程名称
     * @return 线程池
     */
    public static ExecutorService createThreadPoolExecutorService(int poolCoreSize, int poolMaxSize, String threadName) {
        return new ThreadPoolExecutor(poolCoreSize, poolMaxSize, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(), new NamedThreadFactory(threadName));
    }
}

/**
 * Named Thread Factory
 */
class NamedThreadFactory implements ThreadFactory {
    private int threadNum = 1;
    private String threadNamePrefix;

    NamedThreadFactory(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, getNextThreadName());
    }

    private String getNextThreadName() {
        return String.format("%s-%s", threadNamePrefix, threadNum++);
    }
}
