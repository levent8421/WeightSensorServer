alter table t_device_connection
    add column usb_device_id varchar(255) null comment 'USB Device ID' after target;

# add config application.db_version_name
update t_application_config
set value='0.4.1',
    update_time=now()
where name = 'application.db_version_name';

# update db_version
update t_application_config
set value       = 11,
    update_time = now()
where name = 'application.db_version';