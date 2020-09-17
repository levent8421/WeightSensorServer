package com.berrontech.dsensor.dataserver.common.util;

import com.berrontech.dsensor.dataserver.common.exception.CopyException;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * Create By Levent at 2020/8/13 22:06
 * DeepCopyUtils
 * 深拷贝工具类
 *
 * @author levent
 */
@Slf4j
public class DeepCopyUtils {
    public static <T extends Serializable> T deepCopy(T obj) throws CopyException {
        final long start = System.currentTimeMillis();
        final T copy;
        try (final ObjectCopier copier = new ObjectCopier()) {
            copy = copier.copy(obj);
        } catch (IOException e) {
            throw new InternalServerErrorException("Error on create objectCopier", e);
        }
        final long useTime = System.currentTimeMillis() - start;
        final String logStr = String.format("Copy object [%s@%s] useTime=[%s]ms!", obj.hashCode(), obj.getClass().getSimpleName(), useTime);
        log.debug(logStr);
        return copy;
    }
}

class ObjectCopier implements Closeable {
    private final ByteArrayOutputStream out;
    private final ObjectOutputStream oos;

    ObjectCopier() {
        out = new ByteArrayOutputStream();
        oos = buildObjectOutputStream();
    }

    private ObjectOutputStream buildObjectOutputStream() {
        try {
            return new ObjectOutputStream(out);
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public <T extends Serializable> T copy(T obj) throws CopyException {
        try {
            oos.reset();
            oos.writeObject(obj);
            oos.flush();
            final byte[] bytes = out.toByteArray();
            return readFromBytes(bytes);
        } catch (IOException e) {
            throw new CopyException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Serializable> T readFromBytes(byte[] buffer) throws CopyException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
        final T target;
        try (final ObjectInputStream in = new ObjectInputStream(bis)) {
            target = (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new CopyException("Error on read object !", e);
        }
        return target;
    }

    @Override
    public void close() throws IOException {
        if (this.oos != null) {
            oos.close();
        }
    }
}
