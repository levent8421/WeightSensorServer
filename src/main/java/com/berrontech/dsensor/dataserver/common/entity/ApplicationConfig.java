package com.berrontech.dsensor.dataserver.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * Create By Levent8421
 * Create Time: 2020/6/22 18:31
 * Class Name: ApplicationConfig
 * Author: Levent8421
 * Description:
 * 系统配置实体类
 *
 * @author Levent8421
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_application_config")
public class ApplicationConfig extends AbstractEntity {
    /**
     * 数据库版本
     */
    public static final String DB_VERSION = "application.db_version";
    /**
     * 配置名称
     */
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * 配置值
     */
    @Column(name = "value")
    private String value;
}
