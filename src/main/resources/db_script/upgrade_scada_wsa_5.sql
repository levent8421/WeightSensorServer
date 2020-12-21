# add config application.db_version_name
delete
from t_application_config
where name = 'application.db_version_name';

insert into t_application_config (name, value, create_time, update_time, deleted)
values ('application.db_version_name', '0.1.0', now(), now(), false);

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

# update db_version
update t_application_config
set value=5,
    update_time=now()
where name = 'application.db_version';