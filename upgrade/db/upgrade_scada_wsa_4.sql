# add config application.db_version_name
delete
from t_application_config
where name = 'application.db_version_name';

insert into t_application_config (name, value, create_time, update_time, deleted)
values ('application.db_version_name', '0.0.2', now(), now(), false);


# update db_version
update t_application_config
set value=4
where name = 'application.db_version';