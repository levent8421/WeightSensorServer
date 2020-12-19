package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.util.NativeUtils;
import com.berrontech.dsensor.dataserver.common.util.OSUtils;
import com.berrontech.dsensor.dataserver.conf.SerialConfiguration;
import com.berrontech.dsensor.dataserver.weight.NativeLibraryLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.TreeSet;

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
    private static final String LIB_NAME_TEMPLATE = "%s_%s_%s%s";
    private final SerialConfiguration serialConfiguration;
    private final Set<String> loadedLibs = new TreeSet<>();

    public NativeLibraryLoaderImpl(SerialConfiguration serialConfiguration) {
        this.serialConfiguration = serialConfiguration;
    }

    @Override
    public synchronized void loadLib(String os, String arch, boolean skipOnError) throws IOException {
        final String libBaseName = serialConfiguration.getLibName();
        final String libExtName = OSUtils.getLibExtName();
        final String libFileName = String.format(LIB_NAME_TEMPLATE, libBaseName, os, arch, libExtName);
        log.info("Loading lib [{}] for [{}-{}]", libFileName, os, arch);
        if (loadedLibs.contains(libFileName)) {
            log.warn("Lib [{}] already loaded!", libFileName);
            return;
        }
        loadedLibs.add(libFileName);
        loadLibraryFromClasspath(libFileName);
    }


    /**
     * Loads library from classpath
     * <p>
     * The file from classpath is copied into system temporary directory and then loaded. The temporary file is
     * deleted after exiting. Method uses String as filename because the pathname is
     * "abstract", not system-dependent.
     *
     * @param path The file path in classpath as an absolute path, e.g. /package/File.ext (could be inside jar)
     * @throws IOException              If temporary file creation or read/write operation fails
     * @throws IllegalArgumentException If source file (param path) does not exist
     * @throws IllegalArgumentException If the path is not absolute or if the filename is shorter than three characters (restriction
     *                                  of {@see File#createTempFile(java.lang.String, java.lang.String)}).
     */
    private void loadLibraryFromClasspath(String path) throws IOException {
        final Path inputPath = Paths.get(path);

        String fileNameFull = inputPath.getFileName().toString();
        int dotIndex = fileNameFull.indexOf('.');
        if (dotIndex < 0 || dotIndex >= fileNameFull.length() - 1) {
            throw new IllegalArgumentException("The path has to end with a file name and extension, but found: " + fileNameFull);
        }

        String fileName = fileNameFull.substring(0, dotIndex);
        String extension = fileNameFull.substring(dotIndex);
        Path target = Files.createTempFile(fileName, extension);
        File targetFile = target.toFile();
        targetFile.deleteOnExit();
        ClassPathResource resource = new ClassPathResource(inputPath.toString());
        try (InputStream source = resource.getInputStream()) {
            if (source == null) {
                throw new FileNotFoundException("File " + inputPath + " was not found in classpath.");
            }
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
        // Finally, load the library
        NativeUtils.loadLibrary(target.toAbsolutePath().toString());
    }
}
