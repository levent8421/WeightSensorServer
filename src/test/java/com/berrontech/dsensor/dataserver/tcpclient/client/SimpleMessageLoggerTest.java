package com.berrontech.dsensor.dataserver.tcpclient.client;

import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class SimpleMessageLoggerTest implements Runnable {
    private MessageLogger logger;

    @Test
    public void test() {
        logger = new SimpleMessageLogger();
        final List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final String name = String.format("T-%d", i);
            final Thread thread = new Thread(this, name);
            thread.start();
            threads.add(thread);
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            logger.pushMessage(new Message());
            final int time2Sleep = (int) (Math.random() * 100);
            try {
                Thread.sleep(time2Sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}