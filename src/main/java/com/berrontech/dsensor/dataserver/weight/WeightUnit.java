package com.berrontech.dsensor.dataserver.weight;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Create By Levent8421
 * Create Time: 2021/2/9 17:43
 * Class Name: WeightUnit
 * Author: Levent8421
 * Description:
 * Weight Unit
 *
 * @author Levent8421
 */
public class WeightUnit {
    /**
     * 单位 克
     */
    public static final int G = 0x01;
    /**
     * 单位： 千克
     */
    public static final int KG = 0x02;
    private static final BiMap<Integer, String> UNIT_CODE_NAME_TABLE;
    private static final BiMap<String, Integer> UNIT_NAME_CODE_TABLE;

    static {
        UNIT_CODE_NAME_TABLE = HashBiMap.create();
        UNIT_NAME_CODE_TABLE = UNIT_CODE_NAME_TABLE.inverse();
        UNIT_CODE_NAME_TABLE.put(G, "g");
        UNIT_CODE_NAME_TABLE.put(KG, "kg");
    }

    public static String asName(int code) {
        if (UNIT_CODE_NAME_TABLE.containsKey(code)) {
            return UNIT_CODE_NAME_TABLE.get(code);
        }
        throw new IllegalArgumentException("Unknown unit code:" + code);
    }

    public static int of(String name) {
        Preconditions.checkNotNull(name, "Name must be monNull");
        name = name.toLowerCase();
        if (UNIT_NAME_CODE_TABLE.containsKey(name)) {
            return UNIT_NAME_CODE_TABLE.get(name);
        }
        throw new IllegalArgumentException("Cant find unit code for name:" + name);
    }
}
