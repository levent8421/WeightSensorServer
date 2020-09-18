# add config application.db_version_name
delete
from t_application_config
where name = 'application.db_version_name';

insert into t_application_config (name, value, create_time, update_time, deleted)
values ('application.db_version_name', '0.1.1', now(), now(), false);

alter table t_temperature_humidity_sensor
    add column max_temperature decimal(5, 5) not null comment 'Max Temperature',
    add column min_temperature decimal(5, 5) not null comment 'Min Temperature',
    add column max_humidity decimal(5, 5) not null comment 'Max humidity',
    add column min_humidity decimal(5, 5) not null comment 'Min humidity';

# update db_version
update t_application_config
set value       = 6,
    update_time = now()
where name = 'application.db_version';