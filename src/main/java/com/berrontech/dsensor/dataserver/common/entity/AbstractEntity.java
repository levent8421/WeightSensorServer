package com.berrontech.dsensor.dataserver.common.entity;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.common.util.DateTimeUtils;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Create by 郭文梁 2019/5/16 0016 16:40
 * AbstractEntity
 * 基础实体类
 *
 * @author 郭文梁
 * @data 2019/5/16 0016
 */
@Data
public abstract class AbstractEntity implements Serializable {
    /**
     * Id
     */
    @Id
    @Column(name = "id", length = 10, nullable = false)
    @GeneratedValue(generator = ApplicationConstants.Database.GENERATOR_JDBC)
    private Integer id;
    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private Date createTime;
    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    private Date updateTime;
    /**
     * 删除标记（逻辑删除）
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    /**
     * 初始化BaseEntity中的属性
     */
    public void init() {
        updateTime = createTime = DateTimeUtils.now();
        deleted = false;
    }

    /**
     * 刷新更新时间
     */
    public void touch() {
        updateTime = DateTimeUtils.now();
    }
}
