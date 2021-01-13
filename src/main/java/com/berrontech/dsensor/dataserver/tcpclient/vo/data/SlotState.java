package com.berrontech.dsensor.dataserver.tcpclient.vo.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Create By Levent8421
 * Create Time: 2021/1/8 15:27
 * Class Name: SlotState
 * Author: Levent8421
 * Description:
 * Slot state codes
 *
 * @author Levent8421
 */
public class SlotState {
    /**
     * 在线
     */
    public static final int STATE_ONLINE = 0x00;
    /**
     * 离线
     */
    public static final int STATE_OFFLINE = 0x01;
    /**
     * 停用
     */
    public static final int STATE_DISABLE = 0x02;
    /**
     * 超载
     */
    public static final int STATE_OVERLOAD = 0x03;
    /**
     * 欠载
     */
    public static final int STATE_UNDERLOAD = 0x04;
    /**
     * 被合并
     */
    public static final int STATE_MERGED = 0x05;
    public static final BiMap<Integer, String> STATE_STRING_TABLE;
    public static final BiMap<String, Integer> STRING_STATE_TABLE;

    static {
        STATE_STRING_TABLE = HashBiMap.create();
        STATE_STRING_TABLE.put(STATE_ONLINE, "online");
        STATE_STRING_TABLE.put(STATE_OFFLINE, "offline");
        STATE_STRING_TABLE.put(STATE_DISABLE, "disable");
        STATE_STRING_TABLE.put(STATE_OVERLOAD, "overload");
        STATE_STRING_TABLE.put(STATE_UNDERLOAD, "underload");
        STATE_STRING_TABLE.put(STATE_MERGED, "merged");
        STRING_STATE_TABLE = STATE_STRING_TABLE.inverse();
    }

    public static String asStateString(Integer state) {
        if (STATE_STRING_TABLE.containsKey(state)) {
            return STATE_STRING_TABLE.get(state);
        }
        return null;
    }

    public static Integer asStateCode(String state) {
        if (STRING_STATE_TABLE.containsKey(state)) {
            return STRING_STATE_TABLE.get(state);
        }
        return null;
    }
}
