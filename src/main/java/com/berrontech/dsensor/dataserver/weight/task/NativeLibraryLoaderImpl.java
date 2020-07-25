package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.common.util.NativeUtils;
import com.berrontech.dsensor.dataserver.conf.SerialConfiguration;
import com.berrontech.dsensor.dataserver.weight.NativeLibraryLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/7/25 13:49
 * Class Name: NativeLibraryLoaderImpl
 * Author: Levent8421
 * Description:
 * 本地库加载器实现
 *
 * @author Levent8421
 */
@Slf4j
@Component
public class NativeLibraryLoaderImpl implements NativeLibraryLoader {
    private final SerialConfiguration serialConfiguration;

    public NativeLibraryLoaderImpl(SerialConfiguration serialConfiguration) {
        this.serialConfiguration = serialConfiguration;
    }

    @Override
    public void loadLib(String os, String arch, boolean skipOnError) {
        final String libPath = serialConfiguration.getLibPath();
        try {
            log.info("Loading Library [{}]", libPath);
            NativeUtils.loadLibrary(libPath);
            log.info("Load Library [{}] Success!", libPath);
        } catch (Throwable e) {
            final String error = String.format("Can now load [%s] on [%s/%s]", libPath, os, arch);
            if (skipOnError) {
                log.error("Load native lib error, {}", error);
            } else {
                log.error("Load native lib error, {}", error, e);
                throw new InternalServerErrorException(error, e);
            }
        }
    }
}
