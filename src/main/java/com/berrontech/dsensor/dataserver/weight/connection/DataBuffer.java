package com.berrontech.dsensor.dataserver.weight.connection;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Create By Levent8421
 * Create Time: 2020/6/24 14:44
 * Class Name: DataBuffer
 * Author: lastnika
 * Description:
 * Data Buffer
 *
 * @author Levent8421
 */
@SuppressWarnings("unused")
public class DataBuffer {
    private List<Byte> buffer = new ArrayList<>();
    private Lock lock = new ReentrantLock();
    private int workingCounter = 0;
    private int readInterval = 50;

    public boolean isWorking() {
        lock.lock();
        try {
            return (workingCounter > 0);
        } finally {
            lock.unlock();
        }
    }

    public void setWorking(boolean val) {
        lock.lock();
        try {
            if (val) {
                workingCounter++;
            } else {
                workingCounter--;
            }
        } finally {
            lock.unlock();
        }
    }

    public void resetWorkingCounter() {
        lock.lock();
        try {
            workingCounter = 0;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return buffer.size() == 0;
        } finally {
            lock.unlock();
        }
    }

    public int getLength() {
        lock.lock();
        try {
            return buffer.size();
        } finally {
            lock.unlock();
        }
    }

    public void push(byte[] newBuf) {
        lock.lock();
        try {
            for (byte b : newBuf) {
                buffer.add(b);
            }
        } finally {
            lock.unlock();
        }
    }

    public void push(byte[] newBuf, int offset, int count) {
        lock.lock();
        try {
            for (int pos = 0; pos < count && offset + pos < newBuf.length; pos++) {
                buffer.add(newBuf[pos + offset]);
            }
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        lock.lock();
        try {
            buffer.clear();
        } finally {
            lock.unlock();
        }
    }

    public void delete(int offset, int count) {
        lock.lock();
        try {
            if (offset >= buffer.size()) {
                return;
            }
            count = (offset + count <= buffer.size()) ? count : buffer.size() - offset;
            while (count-- > 0) {
                buffer.remove(offset);
            }
        } finally {
            lock.unlock();
        }
    }

    public byte[] pop(int count) {
        if (count <= 0) {
            return null;
        }

        int remain = getLength();
        count = count <= remain ? count : remain;
        byte[] buf;

        lock.lock();
        try {
            List<Byte> subList = buffer.subList(0, count);
            buf = new byte[count];
            for (int p = 0; p < buf.length; p++) {
                buf[p] = subList.get(p);
            }
            /*buf = Bytes.toArray(buffer.subList(0, count));*/
            subList.clear();
        } finally {
            lock.unlock();
        }

        return buf;
    }

    public int lookup(byte[] bts) {
        lock.lock();
        try {
            int end = buffer.size() - bts.length;
            boolean matched = false;
            for (int pos = 0; pos <= end; pos++) {
                for (int cmp = 0; cmp < bts.length; cmp++) {
                    matched = (buffer.get(pos + cmp) == bts[cmp]);
                    if (!matched) {
                        break;
                    }
                }
                if (matched) {
                    return pos;
                }
            }
            return -1;
        } finally {
            lock.unlock();
        }
    }

    public byte[] readLine(byte[] lineEnd) {
        lock.lock();
        try {
            int len = lookup(lineEnd);
            if (len > 0) {
                byte[] buf = pop(len);
                pop(lineEnd.length);
                return buf;
            } else if (len == 0) {
                pop(lineEnd.length);
                return new byte[]{};
            } else {
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

    public byte[] readLine(byte[] lineEnd, long timeout) {
        try {
            long end = System.currentTimeMillis() + timeout;
            while (System.currentTimeMillis() <= end) {
                byte[] line = readLine(lineEnd);
                if (line != null) {
                    return line;
                }
                Thread.sleep(readInterval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] readExisting() {
        lock.lock();
        try {
            return pop(buffer.size());
        } finally {
            lock.unlock();
        }
    }

    public byte[] readBytes(int count) {
        lock.lock();
        try {
            if (buffer.size() < count) {
                return null;
            }
            return pop(count);
        } finally {
            lock.unlock();
        }
    }

    public byte[] readBytes(int count, long timeout) {
        try {
            long end = System.currentTimeMillis() + timeout;
            while (System.currentTimeMillis() <= end) {
                byte[] buf = readBytes(count);
                if (buf != null) {
                    return buf;
                }
                Thread.sleep(readInterval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean waitByte(byte bt, long timeout) {
        try {
            long end = System.currentTimeMillis() + timeout;
            while (System.currentTimeMillis() <= end) {
                byte[] buf = readBytes(1);
                if (buf != null && buf[0] == bt) {
                    return true;
                }
                Thread.sleep(readInterval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
