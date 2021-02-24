package com.berrontech.dsensor.dataserver.log.impl;

import com.berrontech.dsensor.dataserver.common.util.DateTimeUtils;
import com.berrontech.dsensor.dataserver.conf.LogConfiguration;
import com.berrontech.dsensor.dataserver.log.LogManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志定期清理
 *
 * @author 黄荷翔
 * 2021/2/23 15:40
 */
@Component
@Slf4j
public class LogManagerImpl implements LogManager {
    /**
     * 设置日期格式
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 设置正则对文件名称的校验
     */
    private static final Pattern LOG_FILE_NAME_PATTERN = Pattern.compile("^weight-slot-(\\d{4}-\\d{2}-\\d{2})\\.log$");
    /**
     * 文件目录路径
     */
    private final File logRootPath;
    /**
     * 对日期计算的封装结果
     */
    private final long expireMs;

    /**
     * 访问配置类
     *
     * @param logConfiguration 调用配置类
     */
    public LogManagerImpl(LogConfiguration logConfiguration) {
        //调用配置类中的属性值
        final String dir = logConfiguration.getLogFileDir();
        logRootPath = new File(dir);
        //拿到配置类中的属性值并调用本来方法进行格式转换
        expireMs = day2Ms(logConfiguration.getLogKeepDays());
    }

    /**
     * 接口方法（本类入口）
     */
    @Override
    public void cleanup() {
        //创建Map集合存放文件的日期时间和文件名称
        final Map<String, File> logFileTable = findFiles();
        //调用grepExpiredFiles方法把集合传进去返回list<File>集合
        final List<File> expiredFiles = grepExpiredFiles(logFileTable);
        //遍历expiredFiles集合，集合中存储的是需要删除的超时日志文件
        for (File file : expiredFiles) {
            try {
                //删除日志文件
                if (!file.delete()) {
                    log.error("Can not delete log file [{}]", file.getAbsolutePath());
                }
            } catch (Exception e) {
                log.error("Error on delete log file [{}]!", file.getAbsolutePath(), e);
            }
        }
    }

    /**
     * 根据拿到的文件名称和文件名称中的日期进行处理
     *
     * @param dateFileTable map集合
     * @return 需要删除的文件
     */
    private List<File> grepExpiredFiles(Map<String, File> dateFileTable) {
        //获取系统当前时间并进行getTime方法转换
        final long now = DateTimeUtils.now().getTime();
        //创建list<File>集合
        final List<File> files = new ArrayList<>();
        //遍历dateFileTable集合
        for (Map.Entry<String, File> entry : dateFileTable.entrySet()) {
            //通过key键获取到文件日期
            final String dateStr = entry.getKey();
            //指定一下文件日期的日期格式
            final Date date = DateTimeUtils.parse(dateStr, DATE_FORMAT);
            //判断文件是否为删除文件
            if (isExpired(date, now)) {
                //是删除文件则存入集合汇总并返回
                files.add(entry.getValue());
            }
        }
        return files;
    }

    /**
     * 计算文件日期与当前日期相差天数
     *
     * @param date 天数
     * @param now  当前时间
     * @return 返回计算结果与固定天数比较的结果
     */
    private boolean isExpired(Date date, long now) {
        //把日期转化为可计算的格式
        final long fileTime = date.getTime();
        return now - fileTime > expireMs;
    }

    /**
     * 把天数转化成可计算的格式
     *
     * @param day 传入的天数
     * @return 天数转换为可以计算的格式
     */
    private int day2Ms(int day) {
        return day * 24 * 60 * 60 * 1000;
    }

    /**
     * 获取到指定路径下的文件
     *
     * @return list集合
     */
    private Map<String, File> findFiles() {
        //判断指定文件夹是否存在或是否为文件夹
        if (!logRootPath.exists() || !logRootPath.isDirectory()) {
            return Collections.emptyMap();
        }
        //把指定路径下的日志文件存入File数组里
        final File[] files = logRootPath.listFiles();
        //判断files是否为空
        if (files == null) {
            return Collections.emptyMap();
        }
        //创建一个类型是String，File的Map集合
        final Map<String, File> res = new HashMap<>();
        //循环遍历存放日志文件目录的数组
        for (File file : files) {
            //获取到文件的名称
            final String filename = file.getName();
            final Matcher matcher = LOG_FILE_NAME_PATTERN.matcher(filename);
            if (!matcher.find()) {
                log.debug("Invalidate LogFilename [{}]", filename);
                continue;
            }
            final String dateStr = matcher.group(1);
            res.put(dateStr, file);
        }
        return res;
    }
}
