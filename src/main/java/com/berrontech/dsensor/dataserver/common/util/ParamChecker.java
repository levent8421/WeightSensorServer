package com.berrontech.dsensor.dataserver.common.util;

import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

/**
 * Create by 郭文梁 2019/5/27 0027 15:11
 * ParamChecker
 * 参数检查相关工具
 *
 * @author 郭文梁
 * @data 2019/5/27 0027
 */
public class ParamChecker {
    private static final int[] ID_NUMBER_WEIGHT_TABLE = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static final char[] ID_NUMBER_TAIL_TABLE = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    private static final int ID_NUMBER_LENGTH = 18;
    private static final int NUMBER_CHAR_OFFSET = 0x30;

    /**
     * 检查参数是否为空 为空则抛出异常
     *
     * @param o              参数
     * @param exceptionClass 异常类
     * @param msg            附加信息
     */
    public static void notNull(Object o, Class<? extends RuntimeException> exceptionClass, String msg) {
        if (o == null) {
            throwException(exceptionClass, msg);
        }
    }

    /**
     * 检查参数是否为空 为空则抛出异常
     *
     * @param str            字符串
     * @param exceptionClass 异常类
     * @param msg            异常信息
     */
    public static void notEmpty(String str, Class<? extends RuntimeException> exceptionClass, String msg) {
        if (TextUtils.isTrimedEmpty(str)) {
            throwException(exceptionClass, msg);
        }
    }

    /**
     * Assert map  not empty
     *
     * @param map            map
     * @param exceptionClass exception class
     * @param msg            error msg
     */
    public static void notEmpty(Map<?, ?> map, Class<? extends RuntimeException> exceptionClass, String msg) {
        if (map == null || map.isEmpty()) {
            throwException(exceptionClass, msg);
        }
    }

    /**
     * 检查容器不为空
     *
     * @param collection     容器
     * @param exceptionClass 异常类
     * @param msg            异常信息
     */
    public static void notEmpty(Collection<?> collection, Class<? extends RuntimeException> exceptionClass, String msg) {
        notNull(collection, exceptionClass, msg);
        if (collection.size() <= 0) {
            throwException(exceptionClass, msg);
        }
    }

    /**
     * 检查Multipart file 不为空
     *
     * @param file           文件
     * @param exceptionClass 异常类
     * @param msg            异常信息
     */
    public static void notEmpty(MultipartFile file, Class<? extends RuntimeException> exceptionClass, String msg) {
        notNull(file, exceptionClass, msg);
        if (file.isEmpty()) {
            throwException(exceptionClass, msg);
        }
    }

    /**
     * 抛出异常
     *
     * @param exceptionClass 异常类
     * @param msg            异常信息
     */
    private static void throwException(Class<? extends RuntimeException> exceptionClass, String msg) {
        try {
            Constructor<? extends RuntimeException> constructor = exceptionClass.getConstructor(String.class);
            throw constructor.newInstance(msg);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查身份证号码
     *
     * @param idNumber       身份证号字符串
     * @param exceptionClass 异常类
     * @param errMsg         错误信息
     */
    public static void checkIdCard(String idNumber, Class<? extends RuntimeException> exceptionClass, String errMsg) {
        notEmpty(idNumber, exceptionClass, errMsg);
        idNumber = idNumber.trim();
        if (idNumber.length() != ID_NUMBER_LENGTH) {
            throwException(exceptionClass, errMsg);
        }
        char[] numbers = idNumber.toCharArray();
        int sum = 0;
        for (int i = 0; i < ID_NUMBER_LENGTH - 1; i++) {
            int num = (numbers[i] - NUMBER_CHAR_OFFSET) * ID_NUMBER_WEIGHT_TABLE[i];
            sum += num;
        }
        int tailIndex = sum % ID_NUMBER_TAIL_TABLE.length;
        char tailChar = ID_NUMBER_TAIL_TABLE[tailIndex];
        if (tailChar != numbers[ID_NUMBER_LENGTH - 1]) {
            throwException(exceptionClass, errMsg);
        }
    }

    /**
     * 必须是十进制整型正数
     *
     * @param str            str
     * @param exceptionClass ex
     * @param msg            err msg
     */
    public static void onlyDecIntNumber(String str, Class<? extends RuntimeException> exceptionClass, String msg) {
        notEmpty(str, exceptionClass, msg);
        for (char c : str.toCharArray()) {
            if (c > '9' || c < '0') {
                throwException(exceptionClass, msg);
            }
        }
    }
}
