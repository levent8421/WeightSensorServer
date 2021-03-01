alter table t_slot
    add column indivisible bit(1) not null default false comment 'Mark this slot can not be split' after sku_update_time;


alter table t_weight_sensor
    add column type int(3) not null default 1 comment 'Weight sensor type' after has_elabel;


insert into t_application_config (name, value, create_time, update_time, deleted)
values ('weight.protocol_version', '1', now(), now(), false);

# add config application.db_version_name
update t_application_config
set value       = '0.4.4',
    update_time = now()
where name = 'application.db_version_name';

# update db_version
update t_application_config
set value       = 14,
    update_time = now()
where name = 'application.db_version';
