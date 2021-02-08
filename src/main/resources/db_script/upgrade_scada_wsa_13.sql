alter table t_slot
    add column tare_value decimal(10, 3) not null default 0 comment 'Tare value' after slot_no;


# add config application.db_version_name
update t_application_config
set value='0.4.3',
    update_time=now()
where name = 'application.db_version_name';

# update db_version
update t_application_config
set value       = 13,
    update_time = now()
where name = 'application.db_version';
