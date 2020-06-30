alter table t_slot
    add column sku_shelf_life_open_days int(11) null comment '开封后保存天数' after sku_tolerance;

alter table t_weight_sensor
    add column has_elabel bit(1) not null default false comment '是否有电子标签' after slot_id;