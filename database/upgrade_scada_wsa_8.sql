alter table t_weight_sensor
    add column sensor_sn varchar(100) null comment 'Sensor SN' after device_sn ,
    add column elabel_sn varchar(100) null comment 'ELabel SN' after sensor_sn;


insert into t_application_config(name, value, create_time, update_time, deleted)
values ('weight.soft_filter_level', '3', now(), now(), false);

# add config application.db_version_name
update t_application_config
set value='0.2.1'
where name = 'application.db_version_name';

# update db_version
update t_application_config
set value       = 8,
    update_time = now()
where name = 'application.db_version';