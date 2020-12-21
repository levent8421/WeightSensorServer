delete
from t_application_config
where name = 'extra.page_uri';

insert into t_application_config (name, value, create_time, update_time, deleted)
values ('extra.page_uri', 'http://56.58.0.1:8081/starbucks-camera/pages/tt.html', now(), now(), false);

# SKU update time
alter table t_slot
    add column sku_update_time datetime null comment 'SKU Update Time' after sku_shelf_life_open_days;


# add config application.db_version_name
update t_application_config
set value='0.3.1',
    update_time=now()
where name = 'application.db_version_name';

# update db_version
update t_application_config
set value       = 9,
    update_time = now()
where name = 'application.db_version';