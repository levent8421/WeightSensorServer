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


# add config application.db_version_name
delete
from t_application_config
where name = 'application.db_version_name';

insert into t_application_config (name, value, create_time, update_time, deleted)
values ('application.db_version_name', '0.2.0', now(), now(), false);

# update db_version
update t_application_config
set value       = 7,
    update_time = now()
where name = 'application.db_version';