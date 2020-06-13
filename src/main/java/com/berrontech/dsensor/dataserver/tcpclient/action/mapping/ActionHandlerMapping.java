package com.berrontech.dsensor.dataserver.tcpclient.action.mapping;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 15:14
 * Class Name: ActionHandlerMapping
 * Author: Levent8421
 * Description:
 * Action Handler Mapping Annotation
 *
 * @author Levent8421
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ActionHandlerMapping {
    /**
     * Action
     *
     * @return action
     */
    String value() default "";
}
