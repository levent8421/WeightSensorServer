package com.berrontech.dsensor.dataserver.service.general.impl;

import com.berrontech.dsensor.dataserver.common.entity.WeightDataRecord;
import com.berrontech.dsensor.dataserver.repository.mapper.WeightDataRecordMapper;
import com.berrontech.dsensor.dataserver.service.basic.impl.AbstractServiceImpl;
import com.berrontech.dsensor.dataserver.service.general.WeightDataRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Create By Levent8421
 * Create Time: 2020/12/8 17:55
 * Class Name: WeightDataRecordServiceImpl
 * Author: Levent8421
 * Description:
 * Weight data record service implementation
 *
 * @author Levent8421
 */
@Service
@Slf4j
public class WeightDataRecordServiceImpl extends AbstractServiceImpl<WeightDataRecord> implements WeightDataRecordService {
    private static final int RECORD_KEEP_DAYS = 90;
    private final WeightDataRecordMapper weightDataRecordMapper;

    public WeightDataRecordServiceImpl(WeightDataRecordMapper weightDataRecordMapper) {
        super(weightDataRecordMapper);
        this.weightDataRecordMapper = weightDataRecordMapper;
    }

    @Override
    public void cleanup() {
        final int rows = weightDataRecordMapper.deleteOnCreateTimeBefore(RECORD_KEEP_DAYS);
        log.info("Cleanup [{}] records!", rows);
    }
}
