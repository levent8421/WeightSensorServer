package com.berrontech.dsensor.dataserver;

import org.junit.jupiter.api.Test;

/**
 * Create By Levent8421
 * Create Time: 2020/11/21 18:44
 * Class Name: InstanceTest
 * Author: Levent8421
 * Description:
 * 测试Instance of 语法
 *
 * @author Levent8421
 */
public class InstanceTest {
    @Test
    public void testInstanceOf() {
        final String[] strArr = {"1", "2"};
        System.out.println(typeOf(strArr));
    }

    private String typeOf(Object o) {
        if (o instanceof Object[]) {
            return "Array";
        }
        if (o instanceof String) {
            return "String";
        }
        return "Unknown";
    }
}
