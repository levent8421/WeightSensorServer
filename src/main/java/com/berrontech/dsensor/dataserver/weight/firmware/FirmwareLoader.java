package com.berrontech.dsensor.dataserver.weight.firmware;

import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.common.io.IOUtils;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileOutputStream;
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
@Slf4j
public class FirmwareLoader {
    private final String resourceName;
    private final String zipPassword;
    private final String tmpFileName;
    private boolean ready;
    private final FirmwareResource firmwareResource = new FirmwareResource();

    public FirmwareLoader(String resourceName, String zipPassword, String tmpFileName) {
        this.resourceName = resourceName;
        this.zipPassword = zipPassword;
        this.tmpFileName = tmpFileName;
    }

    public FirmwareLoader loadResource() {
        if (ready) {
            return this;
        }
        synchronized (firmwareResource) {
            if (ready) {
                return this;
            }
            try {
                final ClassPathResource resource = new ClassPathResource(resourceName);
                this.doLoadResource(resource.getInputStream());
            } catch (IOException e) {
                throw new InternalServerErrorException("Error on get resource stream!", e);
            }
            ready = true;
        }
        return this;
    }

    private File copyFirmware2File(InputStream source) throws IOException {
        final File file = new File("./" + tmpFileName);
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("Error on delete tmp file!");
            }
        }
        try (final FileOutputStream target = new FileOutputStream(file)) {
            int len;
            final byte[] buffer = new byte[1024];
            while ((len = source.read(buffer)) > 0) {
                target.write(buffer, 0, len);
            }
        }
        return file;
    }

    private void doLoadResource(InputStream resourceStream) {
        try {
            final File resourceFile = copyFirmware2File(resourceStream);
            final ZipFile zipFile = new ZipFile(resourceFile);
            if (!zipFile.isValidZipFile()) {
                throw new InternalServerErrorException("Invalidate firmware zip file!");
            }
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(zipPassword.toCharArray());
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
