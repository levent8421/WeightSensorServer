package com.berrontech.dsensor.dataserver.repository.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/7/18 11:29
 * Class Name: DatabaseMetaDataMapper
 * Author: Levent8421
 * Description:
 * 数据库元数据Mapper
 *
 * @author Levent8421
 */
@Repository
public interface DatabaseMetaDataMapper {
    /**
     * 查询所有表名
     *
     * @return tables names
     */
    List<String> showTables();
}
