package com.berrontech.dsensor.dataserver.service.general;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/7/27 15:54
 * Class Name: LogService
 * Author: Levent8421
 * Description:
 * 日志也足组件 行为定义
 *
 * @author Levent8421
 */
public interface LogService {
    /**
     * 获取全部日志文件
     *
     * @return file list
     */
    List<File> getAllLogFile();

    /**
     * 获取指定日期的日志文件
     *
     * @param date date
     * @return file
     */
    File getLogFileByDate(Date date);

    /**
     * 删除过期的日志文件
     *
     * @return 删除的文件列表
     */
    List<File> deleteExpireLogFiles();
}
