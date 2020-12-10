alter table t_slot
    modify `sku_apw` decimal(10, 3) DEFAULT NULL COMMENT 'sku average single weight' ,
    modify `sku_tolerance` decimal(10, 3) DEFAULT NULL COMMENT 'sku max tolerance';

drop table if exists t_weight_data_record;

create table t_weight_data_record
(
    id                 int(10)        not null auto_increment primary key comment 'Row id',
    sensor_sn          varchar(100)   null comment 'sensor sn',
    sensor_address     int(6)         not null comment 'sensor address',
    sensor_state       int(3)         not null comment 'sensor state code',
    elabel_sn          varchar(100)   null comment 'eLabel sn',
    elabel_state       int(3)         not null comment 'eLabel state code',
    weight             decimal(10, 3) not null comment 'Weight data',
    zero_offset        double         not null comment 'Zero offset ',
    sensor_error_rate  double         not null comment 'sensor error rate',
    sensor_error_count int(10)        not null comment 'Sensor error count',
    elabel_error_rate  double         not null comment 'ELabel error rate',
    elabel_error_count int(10)        not null comment 'ELabel error count',
    sku_apw            decimal(10, 3) null comment 'SKU APW',
    sku_pcs            int(10)        null comment 'SKU PCS',
    create_time        datetime       not null comment 'Row create time',
    update_time        datetime       not null comment 'Row last update time',
    deleted            bit(1)         not null comment 'Deleted mark'
) engine = 'Innodb'
  default charset utf8
  collate utf8_general_ci;

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