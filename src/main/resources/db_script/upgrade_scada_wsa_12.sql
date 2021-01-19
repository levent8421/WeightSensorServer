insert into t_application_config (name, value, create_time, update_time, deleted)
values ('application.display.auto_unit', 'true', now(), now(), false);

# add config application.db_version_name
update t_application_config
set value='0.4.2',
    update_time=now()
where name = 'application.db_version_name';

# update db_version
update t_application_config
set value       = 12,
    update_time = now()
where name = 'application.db_version';
