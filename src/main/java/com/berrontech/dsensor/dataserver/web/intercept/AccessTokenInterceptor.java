package com.berrontech.dsensor.dataserver.web.intercept;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by 郭文梁 2019/5/24 0024 11:12
 * AccessTokenInterceptor
 * 令牌拦截器
 *
 * @author 郭文梁
 */
@Slf4j
public class AccessTokenInterceptor implements HandlerInterceptor {
    private final Map<String, Boolean> ignoreLogPathTable;

    public AccessTokenInterceptor() {
        ignoreLogPathTable = new HashMap<>(16);
        initIgnorePathTable();
    }

    private void initIgnorePathTable() {
        // 忽略Dashboard 轮询刷新日志
        ignoreLogPathTable.put("/api/dashboard/slot-data", Boolean.TRUE);
        ignoreLogPathTable.put("/api/dashboard/_data", Boolean.TRUE);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        val url = request.getRequestURI();
        val method = request.getMethod();
        val ip = request.getRemoteAddr();
        if (!ignoreLogPathTable.containsKey(url)) {
            log.debug("HTTP [{}] from [{}]: {}", method, ip, url);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //Do Nothing
    }
}
