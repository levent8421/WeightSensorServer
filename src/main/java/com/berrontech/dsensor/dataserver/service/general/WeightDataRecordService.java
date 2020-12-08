package com.berrontech.dsensor.dataserver.service.general;

import com.berrontech.dsensor.dataserver.common.entity.WeightDataRecord;
import com.berrontech.dsensor.dataserver.service.basic.AbstractService;

/**
 * Create By Levent8421
 * Create Time: 2020/12/8 17:54
 * Class Name: WeightDataRecordService
 * Author: Levent8421
 * Description:
 * Weight Data Record Service definition
 *
 * @author Levent8421
 */
public interface WeightDataRecordService extends AbstractService<WeightDataRecord> {
    /**
     * clean up
     */
    void cleanup();
}
