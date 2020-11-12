package com.berrontech.dsensor.dataserver.weight.scan;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create By Levent8421
 * Create Time: 2020/11/12 11:25
 * Class Name: SimpleSensorScanListener
 * Author: Levent8421
 * Description:
 * 扫描进度监听
 *
 * @author Levent8421
 */
@Slf4j
@Data
public class SimpleSensorScanListener implements SensorScanListener {
    private static final double MAX_PROGRESS = 100.0;
    public static final int STATE_READY = 0x01;
    public static final int STATE_START = 0x02;
    public static final int STATE_FINISHED = 0x03;
    private static final SimpleSensorScanListener INSTANCE = new SimpleSensorScanListener();
    private final Map<Integer, SnPair> scanResult = new HashMap<>(128);
    private final List<String> errors = new ArrayList<>();
    private int state = STATE_READY;
    private int start = 0;
    private int end = 0;
    private int currentAddress = -1;
    private double progress = 0;

    /**
     * Get a empty listener
     *
     * @return listener
     */
    public static SensorScanListener resetAndGet() {
        INSTANCE.reset();
        return INSTANCE;
    }

    @Override
    public void onScanStart(DeviceConnection connection, int start, int end) {
        log.info("Scan Task start!");
        this.state = STATE_START;
        this.start = start;
        this.end = end;
        this.currentAddress = -1;
        this.progress = 0;
    }

    @Override
    public void onScanEnd() {
        log.info("Scan task finished!");
        this.state = STATE_FINISHED;
        this.progress = MAX_PROGRESS;
    }

    @Override
    public void onProgress(int address, String sn, String eLabelSn) {
        log.info("Scan result [{}]/ s/e = [{}/{}]", address, sn, eLabelSn);
        if (StringUtils.isBlank(sn)) {
            return;
        }
        final SnPair snPair = new SnPair();
        snPair.setSensorSn(sn);
        snPair.setELabelSn(eLabelSn);
        scanResult.put(address, snPair);
        this.updateProgress();
    }

    private void updateProgress() {
        final double num = this.currentAddress - this.start + 1;
        final int total = this.end - this.start + 1;
        this.progress = num / total * MAX_PROGRESS;
    }

    @Override
    public void onScanError(Throwable err) {
        final String errorInStr = String.format("[%s]:%s", err.getClass().getSimpleName(), err.getMessage());
        errors.add(errorInStr);
    }

    private void reset() {
        scanResult.clear();
        errors.clear();
        state = STATE_READY;
        start = 0;
        end = 0;
        currentAddress = -1;
        progress = 0;
    }

    @Data
    public static class SnPair {
        private String sensorSn;
        private String eLabelSn;
    }
}
