alter table t_slot
    add column state int(2) not null default 0 comment '货道状态';

# 系统配置表
drop table if exists t_application_config;
create table t_application_config
(
    id          int(10)      not null auto_increment primary key comment 'ID',
    name        varchar(255) not null comment '配置名称',
    value       varchar(255) not null comment '配置值',
    create_time datetime     not null comment '记录创建时间',
    update_time datetime     not null comment '上次更新时间',
    deleted     bit(1)       not null comment '标记删除'
) engine = 'InnoDb'
  charset utf8;

select ac.id          as ac_id,
       ac.name        as ac_name,
       ac.value       as ac_value,
       ac.create_time as ac_create_time,
       ac.update_time as ac_update_time,
       ac.deleted     as ac_deleted
from t_application_config as ac
where ac.deleted = false;