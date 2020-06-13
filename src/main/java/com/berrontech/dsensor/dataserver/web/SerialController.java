package com.berrontech.dsensor.dataserver.web;

import gnu.io.CommPortIdentifier;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * Create By Levent8421
 * Create Time: 2020/6/10 15:03
 * Class Name: SerialController
 * Author: Levent8421
 * Description:
 * Serial IO Controller
 *
 * @author Levent8421
 */
@RestController
@RequestMapping("/serial")
public class SerialController extends BaseController {
    @GetMapping("/scan")
    public Object scan() {
        val ports = CommPortIdentifier.getPortIdentifiers();
        val portList = new ArrayList<Object>();
        while (ports.hasMoreElements()) {
            portList.add(ports.nextElement());
        }
        return portList;
    }
}
