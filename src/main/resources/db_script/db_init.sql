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
VALUES (1, 'application.db_version', '4', now(), now(), false),          # for this database version
       (2, 'application.db_version_name', '0.1.0', now(), now(), false), # db version name
       (3, 'application.ui.enable_tabBar', 'true', now(), now(), false); # enable the Dashboard UI TabBar

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
    `sku_apw`                  int(7)                DEFAULT NULL COMMENT 'sku average single weight',
    `sku_tolerance`            int(7)                DEFAULT NULL COMMENT 'sku max tolerance',
    `sku_shelf_life_open_days` int(11)               DEFAULT NULL COMMENT 'sku shelf_life_open_days',
    `has_elabel`               bit(1)       NOT NULL COMMENT 'enable eLabel flag',
    `state`                    int(2)       NOT NULL DEFAULT '0' COMMENT 'slot state',
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
    `id`             int(10)     NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `connection_id`  int(10)     NOT NULL COMMENT 'connection ref',
    `address`        int(10)     NOT NULL COMMENT 'hardware address for 485',
    `device_sn`      varchar(50) NOT NULL COMMENT 'device SerialNumber',
    `state`          int(2)      NOT NULL COMMENT 'state for this sensor',
    `zero_reference` double      NOT NULL DEFAULT '0' COMMENT 'weight zero offset',
    `config_str`     varchar(255)         DEFAULT NULL COMMENT 'config string for hardware',
    `slot_id`        int(10)              DEFAULT NULL COMMENT 'slot ref',
    `has_elabel`     bit(1)      NOT NULL DEFAULT b'0' COMMENT 'enable eLabel flag',
    `create_time`    datetime    NOT NULL COMMENT 'time of create',
    `update_time`    datetime    NOT NULL COMMENT 'time of last update',
    `deleted`        bit(1)      NOT NULL COMMENT 'delete flag',
    PRIMARY KEY (`id`)
) ENGINE = InnoDb
  DEFAULT CHARSET = utf8;

drop table if exists t_temperature_humidity_sensor;

create table t_temperature_humidity_sensor
(
    id            int(10)      not null auto_increment primary key comment 'Row ID',
    connection_id int(10)      not null comment 'connection id',
    no            varchar(100) null comment 'number',
    device_sn     varchar(100) not null comment 'device sn',
    address       int(4)       not null comment 'device address',
    state         int(2)       not null comment 'device state',
    create_time   datetime     not null comment 'create time',
    update_time   datetime     not null comment 'update time',
    deleted       bit(1)       not null comment 'Delete flag'
) engine = 'Innodb'
  charset utf8;


