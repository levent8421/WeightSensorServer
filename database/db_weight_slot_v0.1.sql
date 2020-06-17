# 创建数据库
CREATE SCHEMA `db_weight_slot` DEFAULT CHARACTER SET utf8 collate utf8_general_ci;

# 删除设备连接表
drop table if exists t_device_connection;
# 创建t_device_connection 表
# 设备连接表
create table if not exists `t_device_connection`
(
    id          int(10)      not null auto_increment primary key comment 'ID',
    type        int(2)       not null comment '连接类型',
    target      varchar(255) not null comment '连接目标',
    create_time datetime     not null comment '记录创建时间',
    update_time datetime     not null comment '记录更新时间',
    deleted     bit(1)       not null comment '标记删除'
) engine = 'InnoDb'
  charset utf8;

# 尝试查询
select tdc.id          as tdc_id,
       tdc.type        as tdc_type,
       tdc.target      as tdc_target,
       tdc.create_time as tdc_create_time,
       tdc.update_time as tdc_update_time,
       tdc.deleted     as tdc_deleted
from t_device_connection as tdc
where tdc.deleted = false;


# 删除t_weight_sensor 表
drop table if exists t_weight_sensor;
# 创建 t_weight_sensor表
# 重力传感器表
create table if not exists t_weight_sensor
(
    id            int(10)     not null auto_increment primary key comment 'ID',
    connection_id int(10)     not null comment '连接ID',
    address       int(10)     not null comment '485物理地址',
    device_sn     varchar(50) not null comment '设备SN',
    state         int(2)      not null comment '设备状态',
    slot_id       int(10)     null comment '所属货位ID',
    create_time   datetime    not null comment '记录创建时间',
    update_time   datetime    not null comment '上次更新时间',
    deleted       bit(1)      not null comment '标记删除'
) engine = 'InnoDb'
  charset utf8;

# 尝试查询
select ws.id            as ws_id,
       ws.connection_id as ws_connection_id,
       ws.address       as ws_address,
       ws.device_sn     as ws_device_sn,
       ws.state         as ws_state,
       ws.slot_id       as ws_slot_id,
       ws.create_time   as ws_create_time,
       ws.update_time   as ws_update_time,
       ws.deleted       as ws_deleted
from t_weight_sensor as ws
where ws.deleted = false;

# 删除t_slot
drop table if exists t_slot;
# 创建t_slot
create table if not exists t_slot
(
    id            int(10)      not null auto_increment primary key comment 'ID',
    address       int(10)      not null comment '组合485地址',
    slot_no       varchar(100) not null comment '逻辑货道号',
    sku_no        varchar(255) null comment 'SKU 号',
    sku_name      varchar(255) null comment '物料名称',
    sku_apw       int(7)       null comment 'SKU单重',
    sku_tolerance int(7)       null comment '允差',
    has_elabel    bit(1)       not null comment '是否存在电子标签',
    create_time   datetime     not null comment '记录创建时间',
    update_time   datetime     not null comment '上次更新时间',
    deleted       bit(1)       not null comment '标记删除'
) engine = 'InnoDb'
  charset utf8;

# 尝试查询
select s.id            as s_id,
       s.address       as s_address,
       s.slot_no       as s_slot_no,
       s.sku_no        as s_sku_no,
       s.sku_name      as s_sku_name,
       s.sku_apw       as s_sku_apw,
       s.sku_tolerance as s_sku_tolerance,
       s.has_elabel    as s_has_elabel,
       s.create_time   as s_create_time,
       s.update_time   as s_update_time,
       s.deleted       as s_deleted
from t_slot as s
where s.deleted = false;