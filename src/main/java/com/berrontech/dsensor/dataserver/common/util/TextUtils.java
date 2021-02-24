package com.berrontech.dsensor.dataserver.common.util;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.common.fn.GetterFunction;
import lombok.val;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Create by 郭文梁 2019/4/20 0020 15:30
 * TextUtils
 * 文本相关工具类
 *
 * @author 郭文梁
 */
public class TextUtils {
    /**
     * 去除两边空格后字符串是否为空,
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isTrimedEmpty(String str) {
        if (str == null || str.length() <= 0) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            val c = str.charAt(i);
            if (c > ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * 去除两边空格后字符串是否不为空
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isTrimedNotEmpty(String str) {
        return !isTrimedEmpty(str);
    }

    /**
     * 字符串非空检查
     *
     * @param getter Getter
     * @param errMsg 异常信息
     */
    public static void notEmpty(GetterFunction<String> getter, String errMsg) {
        if (isTrimedEmpty(getter.apply())) {
            throw new BadRequestException(errMsg);
        }
    }

    /**
     * 将ISO8859-1的字符串转换为UTF-8字符串
     *
     * @param str 被转换字符串
     * @return 转换结果
     */
    public static String iso2Utf8(String str) {
        if (str == null) {
            return null;
        }
        return new String(str.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    /**
     * 将字符串转换为UTF-8字符串
     *
     * @param str 被转换字符串
     * @return 转换结果
     */
    public static String toUtf8(String str) {
        if (str == null) {
            return null;
        }
        return new String(str.getBytes(), StandardCharsets.UTF_8);
    }

    /**
     * 限制字符串的最大长度
     *
     * @param source    输入
     * @param maxLength 最大长度
     * @param suffix    超出最大长度后在截取后的字符串后面拼接该内容
     * @return 处理结果
     */
    public static String maxLength(String source, int maxLength, String suffix) {
        if (source.length() > maxLength) {
            val res = source.substring(0, maxLength);
            if (suffix != null) {
                return res + suffix;
            }
            return res;
        }
        return source;
    }

    /**
     * Encode Uri
     *
     * @param text text
     * @return encode result
     */
    public static String encodeUri(String text) {
        try {
            return URLEncoder.encode(text, ApplicationConstants.Context.DEFAULT_CHARSET_STR);
        } catch (UnsupportedEncodingException e) {
            throw new InternalServerErrorException("Error on encode url!" + text);
        }
    }
}
