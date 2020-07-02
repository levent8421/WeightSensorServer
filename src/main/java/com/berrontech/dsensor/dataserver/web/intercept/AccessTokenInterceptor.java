package com.berrontech.dsensor.dataserver.web.intercept;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Create by 郭文梁 2019/5/24 0024 11:12
 * AccessTokenInterceptor
 * 令牌拦截器
 *
 * @author 郭文梁
 * @data 2019/5/24 0024
 */
@Slf4j
public class AccessTokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        val url = request.getRequestURI();
        val method = request.getMethod();
        val ip = request.getRemoteAddr();
        log.debug("Http Request: [{}]/[{}]/{}", ip, method, url);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //Do Nothing
    }
}
