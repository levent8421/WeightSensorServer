package com.berrontech.dsensor.dataserver.web.controller.api;

import com.berrontech.dsensor.dataserver.web.controller.AbstractController;
import com.berrontech.dsensor.dataserver.web.vo.GeneralResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 * Create By Levent8421
 * Create Time: 2020/12/17 19:15
 * Class Name: FileUtilsController
 * Author: Levent8421
 * Description:
 * File Utils Controller
 *
 * @author Levent8421
 */
@RestController
@RequestMapping("/api/file")
@Slf4j
public class FileUtilsController extends AbstractController {
    @PostMapping("/_info")
    public GeneralResult<?> fileInfo(@RequestParam("filename") String filename) throws IOException {
        final File file = new File(filename);
        final String f1 = file.getCanonicalFile().getAbsolutePath();
        final String f2 = file.getAbsolutePath();
        log.debug("[{}]/[{}]", f1, f2);
        return GeneralResult.ok(f1);
    }
}
