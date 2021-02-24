package com.berrontech.dsensor.dataserver.web.intercept;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Create by 郭文梁 2019/5/20 0020 19:03
 * InterceptorConfig
 * 拦截器配置
 *
 * @author 郭文梁
 */
@Configuration
@Component
public class InterceptorConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AccessTokenInterceptor());
    }
}
