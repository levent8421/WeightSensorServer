package com.berrontech.dsensor.dataserver.common.context;

import java.nio.charset.Charset;

/**
 * Create By Levent8421
 * Create Time: 2020/6/10 18:22
 * Class Name: ApplicationConstants
 * Author: Levent8421
 * Description:
 * Application Global Constants
 *
 * @author Levent8421
 */
public class ApplicationConstants {

    public static class Datetime {
        /**
         * 默认时间如期格式
         */
        public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        /**
         * 时间格式
         */
        public static final String TIME_FORMAT = "HH:mm:ss";
        /**
         * 时间格式 不带秒
         */
        public static final String TIME_FORMAT_WITHOUT_SECOND = "HH:mm";
        /**
         * 日期格式
         */
        public static final String DATE_FORMAT = "yyyy-MM-dd";
        /**
         * 日期格式化 不带年
         */
        public static final String DATE_FORMAT_WITHOUT_YEAR = "MM-dd";
    }

    /**
     * Message Constants
     */
    public static class Message {
        /**
         * Package Start STX
         */
        public static final byte PACKAGE_START = 0x02;
        /**
         * Package End ETX
         */
        public static final byte PACKAGE_END = 0x03;
        /**
         * 重发次数
         */
        public static final int MESSAGE_MAX_RETRY = 2;
        /**
         * timeout int ms
         */
        public static final int MESSAGE_TIMEOUT = 10 * 1000;
        /**
         * Timeout 检查周期
         */
        public static final int TIMEOUT_CHECK_INTERVAL = 1000;
        /**
         * 心跳周期
         */
        public static final int HEARTBEAT_INTERVAL = 2 * 60 * 1000;
        /**
         * 数据最大连续发送失败次数
         */
        public static final int MAXIMUM_CONSECUTIVE_SEND_FAILURES = 3;
    }

    public static class Context {
        /**
         * Charset Name String
         */
        public static final String DEFAULT_CHARSET_STR = "UTF-8";
        /**
         * Charset
         */
        public static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_STR);
        /**
         * Version
         */
        public static final String APP_VERSION = "0.1.5";
        /**
         * Application Name
         */
        public static final String APP_NAME = "balance";
    }

    /**
     * Actions Table
     */
    public static class Actions {
        /**
         * Heartbeat action
         */
        public static final String HEARTBEAT = "notify.heartbeat";
    }

    /**
     * 数据库相关常量
     */
    public static class Database {
        /**
         * JDBC主键生成器名称
         */
        public static final String GENERATOR_JDBC = "JDBC";
        /**
         * 降序排序
         */
        public static final String ORDER_DESC = "desc";
        /**
         * 升序排序
         */
        public static final String ORDER_ASC = "asc";
    }

    /**
     * 电子标签相关常量
     */
    public static class ELabel {
        /**
         * ELabel 高亮时间
         */
        public static final long ELABEL_HIGHLIGHT_DURATION = 5 * 1000;
    }
}
