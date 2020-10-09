package com.berrontech.dsensor.dataserver.weight.firmware;

import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.common.io.IOUtils;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/9/11 13:53
 * Class Name: FirmwareLoader
 * Author: Levent8421
 * Description:
 * 固件加载器
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class FirmwareLoader {
    private static final String PASSWORD = "monolithiot";
    private static final String RESOURCE_NAME = "classpath:firmware/firmware.zip";
    private boolean ready;
    private final FirmwareResource firmwareResource = new FirmwareResource();

    public FirmwareLoader loadResource() {
        if (ready) {
            return this;
        }
        synchronized (firmwareResource) {
            if (ready) {
                return this;
            }
            try {
                final File resourceFile = ResourceUtils.getFile(RESOURCE_NAME);
                this.doLoadResource(resourceFile);
            } catch (FileNotFoundException e) {
                throw new InternalServerErrorException("Error ron load Firmware file!", e);
            }
            ready = true;
        }
        return this;
    }

    private void doLoadResource(File resourceFile) {
        try {
            final ZipFile zipFile = new ZipFile(resourceFile);
            if (!zipFile.isValidZipFile()) {
                throw new InternalServerErrorException("Invalidate firmware zip file!");
            }
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(PASSWORD.toCharArray());
            }
            final List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
            if (fileHeaderList.size() != 1) {
                throw new InternalServerErrorException("Invalidate file count " + fileHeaderList.size() + " in zip file!");
            }
            final FileHeader fileHeader = fileHeaderList.get(0);
            readZipFile(zipFile, fileHeader);
        } catch (IOException e) {
            throw new InternalServerErrorException("Error on load firmware file!", e);
        }
    }

    private void readZipFile(ZipFile zipFile, FileHeader fileHeader) {
        final String fileName = fileHeader.getFileName();
        this.firmwareResource.setFileName(fileName);
        try {
            try (final InputStream inputStream = zipFile.getInputStream(fileHeader)) {
                final byte[] content = IOUtils.readAsBytes(inputStream);
                this.firmwareResource.setContent(content);
            }
        } catch (IOException e) {
            throw new InternalServerErrorException("Error on read zip file!", e);
        }
    }

    public FirmwareResource getFirmwareResource() {
        return firmwareResource;
    }
}
