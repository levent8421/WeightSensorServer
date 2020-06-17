CREATE SCHEMA `db_weight_slot` DEFAULT CHARACTER SET utf8;

create table if not exists `t_device_connection`
(
    id          int(10)      not null auto_increment primary key comment 'ID',
    type        int(2)       not null comment '连接类型',
    target      varchar(255) not null comment '连接目标',
    create_time datetime     not null comment '记录创建时间',
    update_time datetime     not null comment '记录更新时间',
    deleted     bit(1)       not null comment '标记删除'
)

select tdc.id          as tdc_id,
       tdc.type        as tdc_type,
       tdc.target      as tdc_target,
       tdc.create_time as tdc_create_time,
       tdc.update_time as tdc_update_time,
       tdc.deleted     as tdc_deleted
from t_device_connection as tdc
where tdc.deleted = false;