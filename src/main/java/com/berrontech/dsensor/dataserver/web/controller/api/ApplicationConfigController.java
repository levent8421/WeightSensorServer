package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.common.entity.ApplicationConfig;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.common.exception.ResourceNotFoundException;
import com.berrontech.dsensor.dataserver.service.general.ApplicationConfigService;
import com.berrontech.dsensor.dataserver.web.controller.AbstractEntityController;
import com.berrontech.dsensor.dataserver.web.vo.ConfigSettingParam;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import org.springframework.web.bind.annotation.*;

import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notEmpty;
import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notNull;

/**
 * Create By Levent8421
 * Create Time: 2020/7/2 15:56
 * Class Name: ApplicationConfigController
 * Author: Levent8421
 * Description:
 * 系统配置相关数据访问控制器
 *
 * @author Levent8421
 */
@RestController
@RequestMapping("/api/config")
public class ApplicationConfigController extends AbstractEntityController<ApplicationConfig> {
    private final ApplicationConfigService applicationConfigService;

    public ApplicationConfigController(ApplicationConfigService applicationConfigService) {
        super(applicationConfigService);
        this.applicationConfigService = applicationConfigService;
    }

    /**
     * 获取配置参数
     * @param name config name
     * @return GR
     */
    @GetMapping("/{name}")
    public GeneralResult<ApplicationConfig> getConfig(@PathVariable("name") String name) {
        final ApplicationConfig config = applicationConfigService.getConfig(name);
        checkNotNull(config);
        return GeneralResult.ok(config);
    }

    /**
     * 设置配置参数
     *
     * @param name  config name
     * @param param config params
     * @return GR
     */
    @PostMapping("/{name}")
    public GeneralResult<Void> setConfig(@PathVariable("name") String name,
                                         @RequestBody ConfigSettingParam param) {
        ApplicationConfig config = applicationConfigService.getConfig(name);
        if (config == null) {
            if (param.getCreateIfNotExists() != null && param.getCreateIfNotExists()) {
                config = new ApplicationConfig();
                config.setName(name);
                config.setValue(param.getValue());
                config = applicationConfigService.save(config);
            }
        }
        checkNotNull(config);
        notNull(param, BadRequestException.class, "No Params!");
        config.setValue(param.getValue());
        applicationConfigService.updateById(config);
        return GeneralResult.ok();
    }

    /**
     * Throw Exception on object is null
     *
     * @param o object
     */
    private void checkNotNull(Object o) {
        if (o == null) {
            throw new ResourceNotFoundException("Can not found config !");
        }
    }
}
