package com.berrontech.dsensor.dataserver.common.util;

import com.berrontech.dsensor.dataserver.common.exception.CopyException;

import java.io.*;

/**
 * Create By Levent at 2020/8/13 22:06
 * DeepCopyUtils
 * 深拷贝工具类
 *
 * @author levent
 */
public class DeepCopyUtils {
    /**
     * Deep copy object
     *
     * @param source source object
     * @param <T>    object type
     * @return target object
     * @throws CopyException error
     */
    public static <T extends Serializable> T deepCopy(T source) throws CopyException {
        final byte[] bytes = object2Bytes(source);
        return readFromBytes(bytes);
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

    private static byte[] object2Bytes(Serializable source) throws CopyException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (final ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(source);
            out.flush();
        } catch (IOException e) {
            final String error = String.format("Error on write object [%s] at [%s]",
                    source.getClass().getName(), source.hashCode());
            throw new CopyException(error, e);
        }
        return bos.toByteArray();
    }
}
