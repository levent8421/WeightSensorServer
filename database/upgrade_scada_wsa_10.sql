alter table t_slot
    modify `sku_apw` decimal(10, 3) DEFAULT NULL COMMENT 'sku average single weight' ,
    modify `sku_tolerance` decimal(10, 3) DEFAULT NULL COMMENT 'sku max tolerance';

# add config application.db_version_name
update t_application_config
set value='0.4.0',
    update_time=now()
where name = 'application.db_version_name';

# update db_version
update t_application_config
set value       = 10,
    update_time = now()
where name = 'application.db_version';