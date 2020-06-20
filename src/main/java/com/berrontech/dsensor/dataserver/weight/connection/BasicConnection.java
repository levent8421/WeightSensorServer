package com.berrontech.dsensor.dataserver.weight.connection;


import java.lang.ref.WeakReference;
import java.nio.charset.Charset;

public abstract class BasicConnection {
    protected String tag;
    protected boolean paused;
    protected boolean connected;
    protected DataBuffer bufRecv = new DataBuffer();
    protected DataBuffer bufSend = new DataBuffer();
    protected OnReceivedListener onReceivedListener;

    public String getTag() {
        return tag;
    }

    public BasicConnection setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    public synchronized BasicConnection setPaused(boolean paused) {
        this.paused = paused;
        return this;
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized BasicConnection setConnected(boolean connected) {
        this.connected = connected;
        return this;
    }


    public BasicConnection() {
    }

    public DataBuffer getRecvBuffer() {
        return bufRecv;
    }

    public DataBuffer getSendBuffer() {
        return bufSend;
    }

    public OnReceivedListener getOnReceivedListener() {
        return onReceivedListener;
    }

    public BasicConnection setOnReceivedListener(OnReceivedListener onReceivedListener) {
        this.onReceivedListener = onReceivedListener;
        return this;
    }

    public void notifyReceived() {
        if (getOnReceivedListener() != null) {
            getOnReceivedListener().onReceived(this);
        }
    }

    public abstract void open() throws Exception;

    public abstract void close();


    byte[] lineEnd = new byte[]{'\r'};
    String encoder = "UTF-8";

    public BasicConnection setLineEnd(byte[] lineEnd) {
        this.lineEnd = lineEnd;
        return this;
    }

    public BasicConnection setLineEnd(String lineEnd) {
        this.lineEnd = lineEnd.getBytes(Charset.forName(encoder));
        return this;
    }

    public BasicConnection setEncoder(String encoder) {
        this.encoder = encoder;
        return this;
    }

    public void discardRecvBuffer() {
        bufRecv.clear();
        bufRecv.resetWorkingCounter();
    }

    public void discardSendBuffer() {
        bufSend.clear();
        bufSend.resetWorkingCounter();
    }

    public int receivedBytesCount() {
        return bufRecv.getLength();
    }

    public boolean waitByte(byte bt, long timeout) {
        return bufRecv.waitByte(bt, timeout);
    }

    public byte readByte(long timeout) {
        byte[] buf = readBytes(1, timeout);
        return buf[0];
    }

    public byte[] readBytes(int count, long timeout) {
        return bufRecv.readBytes(count, timeout);
    }

    public String readLine() {
        byte[] line = bufRecv.readLine(lineEnd);
        if (line != null) {
            return new String(line, Charset.forName(encoder));
        }
        return null;
    }

    public String readLine(long timeout) {
        byte[] line = bufRecv.readLine(lineEnd, timeout);
        if (line != null) {
            return new String(line, Charset.forName(encoder));
        }
        return null;
    }

    public String readMeaningfulLine(String[] ansFamily, long timeout) {
        try {
            long end = System.currentTimeMillis() + timeout;
            while (System.currentTimeMillis() <= end) {
                String line = this.readLine();
                if (line != null) {
                    for (String s : ansFamily) {
                        if (line.contains(s)) {
                            return line;
                        }
                    }
                }
                Thread.sleep(100);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }


    public void writeLine(String line) throws Exception {
        line += new String(lineEnd, Charset.forName(encoder));
        write(line);
    }

    public void write(String str) throws Exception {
        byte bytes[] = str.getBytes(Charset.forName(encoder));
        writeBuf(bytes, 0, bytes.length);
    }

    public void write(byte[] buf) throws Exception {
        writeBuf(buf, 0, buf.length);
    }

    public abstract void writeBuf(byte[] buf, int offset, int count) throws Exception;


    public interface OnReceivedListener {
        void onReceived(BasicConnection connection);
    }

}
