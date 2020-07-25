package com.berrontech.dsensor.dataserver.weight;

/**
 * Create By Levent8421
 * Create Time: 2020/7/25 13:46
 * Class Name: NativeLibraryLoader
 * Author: Levent8421
 * Description:
 * Native Lib Loader
 *
 * @author Levent8421
 */
public interface NativeLibraryLoader {
    /**
     * Load Library
     *
     * @param os          os name
     * @param arch        arch name
     * @param skipOnError ignore error
     */
    void loadLib(String os, String arch, boolean skipOnError);
}
