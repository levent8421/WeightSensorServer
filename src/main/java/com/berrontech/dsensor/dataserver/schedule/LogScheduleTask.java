package com.berrontech.dsensor.dataserver.schedule;

import com.berrontech.dsensor.dataserver.log.LogManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * 日志清理定时任务
 *
 * @author 黄荷翔
 */
@Component
@Slf4j
public class LogScheduleTask {
    private final LogManager logManager;

    public LogScheduleTask(LogManager logManager) {
        this.logManager = logManager;
    }

    /**
     * 定时任务
     * 测试时可使用如下注解
     * <code>@Scheduled(fixedRate = 3000)</code>
     */
    @Scheduled(cron = "0  11 * * ?")
    public void cleanupLog() {
        log.info("LogFile Cleanup task start!");
        logManager.cleanup();
        log.info("LogFile Cleanup task complete!");
    }
}
