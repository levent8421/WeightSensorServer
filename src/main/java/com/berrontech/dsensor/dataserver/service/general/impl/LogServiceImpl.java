package com.berrontech.dsensor.dataserver.service.general.impl;

import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.common.util.DateTimeUtils;
import com.berrontech.dsensor.dataserver.conf.LogConfiguration;
import com.berrontech.dsensor.dataserver.service.general.LogService;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/7/27 15:54
 * Class Name: LogServiceImpl
 * Author: Levent8421
 * Description:
 * 日志业务组件实现
 *
 * @author Levent8421
 */
@Component
public class LogServiceImpl implements LogService {
    private static final String LOG_FILE_TEMPLATE = "weight-slot-%s.log";
    private final LogConfiguration logConfiguration;
    private File logDirFile;

    public LogServiceImpl(LogConfiguration logConfiguration) {
        this.logConfiguration = logConfiguration;
        try {
            this.initLogDir();
        } catch (IOException e) {
            throw new InternalServerErrorException("Error on init log dir!", e);
        }
    }

    private void initLogDir() throws IOException {
        this.logDirFile = new File(logConfiguration.getLogFileDir());
        if (!logDirFile.exists()) {
            tryMkdir(logDirFile);
        }
        if (!logDirFile.isDirectory()) {
            throw new IOException(String.format("Path [%s] is not a dir!", logDirFile.getAbsoluteFile()));
        }
    }

    private void tryMkdir(File dir) throws IOException {
        final boolean make = dir.mkdirs();
        if (!make) {
            throw new IOException("Can not create log dir:" + dir.getAbsolutePath());
        }
    }

    @Override
    public List<File> getAllLogFile() {
        final File[] files = logDirFile.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(files);
    }

    @Override
    public File getLogFileByDate(Date date) {
        final String dateStr = DateTimeUtils.format(date, "yyyy-MM-dd");
        final String filename = String.format(LOG_FILE_TEMPLATE, dateStr);
        final File logFile = new File(logDirFile, filename);
        if (!logFile.exists()) {
            return null;
        }
        return logFile;
    }
}
