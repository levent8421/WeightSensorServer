SET NAMES utf8;

# --
# -- Table structure for table `t_application_config`
# --
DROP TABLE IF EXISTS `t_application_config`;
CREATE TABLE `t_application_config`
(
    `id`          int(10)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name`        varchar(255) NOT NULL COMMENT 'configuration name',
    `value`       varchar(255) NOT NULL COMMENT 'configuration value',
    `create_time` datetime     NOT NULL COMMENT 'time of create',
    `update_time` datetime     NOT NULL COMMENT 'time of last update',
    `deleted`     bit(1)       NOT NULL COMMENT 'delete flag',
    PRIMARY KEY (`id`)
) ENGINE = innoDb
  DEFAULT CHARSET = utf8;


# --
# -- Default data for table `t_application_config`
# --
LOCK TABLES `t_application_config` WRITE;

INSERT INTO `t_application_config`
VALUES (1, 'application.db_version', '9', now(), now(), false),          # for this database version
       (2, 'application.db_version_name', '0.3.1', now(), now(), false), # db version name
       (3, 'application.ui.enable_tabBar', 'true', now(), now(), false),
       (4, 'weight.soft_filter_level', '0', now(), now(), false),        # enable the Dashboard UI TabBar
       (5, 'extra.page_uri', 'http://56.58.0.1:8081/starbucks-camera/pages/tt.html', now(), now(),
        false);

UNLOCK TABLES;

# --
# -- Table structure for table `t_device_connection`
# --

DROP TABLE IF EXISTS `t_device_connection`;

CREATE TABLE `t_device_connection`
(
    `id`          int(10)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `type`        int(2)       NOT NULL COMMENT 'connection type, 1:serial,2:tcp',
    `target`      varchar(255) NOT NULL COMMENT 'connection target',
    `create_time` datetime     NOT NULL COMMENT 'time of create',
    `update_time` datetime     NOT NULL COMMENT 'time of last update',
    `deleted`     bit(1)       NOT NULL COMMENT 'delete flag',
    PRIMARY KEY (`id`)
) ENGINE = InnoDb
  DEFAULT CHARSET = utf8;


# --
# -- Table structure for table `t_slot`
# --

DROP TABLE IF EXISTS `t_slot`;

CREATE TABLE `t_slot`
(
    `id`                       int(10)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `address`                  int(10)      NOT NULL COMMENT 'hardware address for 485',
    `slot_no`                  varchar(100) NOT NULL COMMENT 'slot name',
    `sku_no`                   varchar(255)          DEFAULT NULL COMMENT 'sku number',
    `sku_name`                 varchar(255)          DEFAULT NULL COMMENT 'sku name',
    `sku_apw`                  decimal(10, 3)        DEFAULT NULL COMMENT 'sku average single weight',
    `sku_tolerance`            decimal(10, 3)        DEFAULT NULL COMMENT 'sku max tolerance',
    `sku_shelf_life_open_days` int(11)               DEFAULT NULL COMMENT 'sku shelf_life_open_days',
    `has_elabel`               bit(1)       NOT NULL COMMENT 'enable eLabel flag',
    `state`                    int(2)       NOT NULL DEFAULT '0' COMMENT 'slot state',
    `sku_update_time`          datetime     NULL COMMENT 'SKU update time',
    `create_time`              datetime     NOT NULL COMMENT 'time of create',
    `update_time`              datetime     NOT NULL COMMENT 'time of last update',
    `deleted`                  bit(1)       NOT NULL COMMENT 'delete flag',
    PRIMARY KEY (`id`)
) ENGINE = InnoDb
  DEFAULT CHARSET = utf8;

# --
# -- Table structure for table `t_weight_sensor`
# --

DROP TABLE IF EXISTS `t_weight_sensor`;

CREATE TABLE `t_weight_sensor`
(
    `id`             int(10)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `connection_id`  int(10)      NOT NULL COMMENT 'connection ref',
    `address`        int(10)      NOT NULL COMMENT 'hardware address for 485',
    `device_sn`      varchar(50)  NOT NULL COMMENT 'device SerialNumber',
    sensor_sn        varchar(100) null comment 'Sensor SN',
    elabel_sn        varchar(100) null comment 'ELabel SN',
    `state`          int(2)       NOT NULL COMMENT 'state for this sensor',
    `zero_reference` double       NOT NULL DEFAULT '0' COMMENT 'weight zero offset',
    `config_str`     varchar(255)          DEFAULT NULL COMMENT 'config string for hardware',
    `slot_id`        int(10)               DEFAULT NULL COMMENT 'slot ref',
    `has_elabel`     bit(1)       NOT NULL DEFAULT b'0' COMMENT 'enable eLabel flag',
    `create_time`    datetime     NOT NULL COMMENT 'time of create',
    `update_time`    datetime     NOT NULL COMMENT 'time of last update',
    `deleted`        bit(1)       NOT NULL COMMENT 'delete flag',
    PRIMARY KEY (`id`)
) ENGINE = InnoDb
  DEFAULT CHARSET = utf8;

drop table if exists t_temperature_humidity_sensor;

create table t_temperature_humidity_sensor
(
    id              int(10)       not null auto_increment primary key comment 'Row ID',
    connection_id   int(10)       not null comment 'connection id',
    no              varchar(100)  null comment 'number',
    device_sn       varchar(100)  not null comment 'device sn',
    address         int(4)        not null comment 'device address',
    state           int(2)        not null comment 'device state',
    max_temperature decimal(6, 3) not null comment 'Max Temperature',
    min_temperature decimal(6, 3) not null comment 'Min Temperature',
    max_humidity    decimal(6, 3) not null comment 'Max humidity',
    min_humidity    decimal(6, 3) not null comment 'Min humidity',
    create_time     datetime      not null comment 'create time',
    update_time     datetime      not null comment 'update time',
    deleted         bit(1)        not null comment 'Delete flag'
) engine = 'Innodb'
  charset utf8;


drop table if exists t_temp_humidity_log;

create table t_temp_humidity_log
(
    id                int(10)       not null auto_increment primary key comment 'Row id',
    sensor_id         int(10)       not null comment 't_temperature)humidity_sensor.id',
    humidity          decimal(6, 3) not null comment 'Log humidity value',
    humidity_state    int(2)        not null comment 'Humidity state code',
    max_humidity      decimal(6, 3) not null comment 'Max humidity',
    min_humidity      decimal(6, 3) not null comment 'Min humidity',
    temperature       decimal(6, 3) not null comment 'Log temperature',
    temperature_state int(2)        not null comment 'Temperature state code',
    max_temperature   decimal(6, 3) not null comment 'Max temperature',
    min_temperature   decimal(6, 3) not null comment 'Min temperature',
    create_time       datetime      not null comment 'Row Create time',
    update_time       datetime      not null comment 'Row Update time',
    deleted           bit(1)        not null comment 'Delete flag'
) engine = 'Innodb'
  default charset utf8
  collate utf8_general_ci;


drop table if exists t_weight_data_record;

create table t_weight_data_record
(
    id                 int(10)        not null auto_increment primary key comment 'Row id',
    sensor_sn          varchar(100)   null comment 'sensor sn',
    sensor_address     int(6)         not null comment 'sensor address',
    sensor_state       int(3)         not null comment 'sensor state code',
    elabel_sn          varchar(100)   null comment 'eLabel sn',
    elabel_state       int(3)         not null comment 'eLabel state code',
    weight             decimal(10, 3) not null comment 'Weight data',
    zero_offset        double         not null comment 'Zero offset ',
    sensor_error_rate  double         not null comment 'sensor error rate',
    sensor_error_count int(10)        not null comment 'Sensor error count',
    elabel_error_rate  double         not null comment 'ELabel error rate',
    elabel_error_count int(10)        not null comment 'ELabel error count',
    sku_apw            decimal(10, 3) null comment 'SKU APW',
    sku_pcs            int(10)        null comment 'SKU PCS',
    create_time        datetime       not null comment 'Row create time',
    update_time        datetime       not null comment 'Row last update time',
    deleted            bit(1)         not null comment 'Deleted mark'
) engine = 'Innodb'
  default charset utf8
  collate utf8_general_ci;