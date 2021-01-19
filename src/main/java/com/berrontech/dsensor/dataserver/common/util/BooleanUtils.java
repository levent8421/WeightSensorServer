package com.berrontech.dsensor.dataserver.common.util;

import java.util.Locale;
import java.util.Objects;

/**
 * Create By Levent8421
 * Create Time: 2021/1/19 下午3:57
 * Class Name: BooleanUtils
 * Author: Levent8421
 * Description:
 * BooleanUtils
 *
 * @author Levent8421
 */
public class BooleanUtils {
    public static final String TRUE_STR = "true";
    public static final String FALSE_STR = "false";
    private static final boolean DEFAULT = false;

    public static boolean asBoolean(String str) {
        if (str == null) {
            return DEFAULT;
        }
        str = str.toLowerCase();
        if (Objects.equals(TRUE_STR, str)) {
            return true;
        }
        if (Objects.equals(FALSE_STR, str)) {
            return false;
        }
        return DEFAULT;
    }
}
