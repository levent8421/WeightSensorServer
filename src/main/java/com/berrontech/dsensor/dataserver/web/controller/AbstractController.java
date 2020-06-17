package com.berrontech.dsensor.dataserver.web.controller;

import lombok.val;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.constraints.NotNull;

/**
 * Create by 郭文梁 2019/5/18 0018 12:38
 * AbstractController
 * 控制器基类
 *
 * @author 郭文梁
 * @data 2019/5/18 0018
 */
public abstract class AbstractController {
    private static final String ERROR_VIEW_NAME = "sys-error";
    /**
     * 默认页码
     */
    private static final int DEFAULT_PAGE = 1;
    /**
     * 默认每页大小
     */
    private static final int DEFAULT_ROWS = 10;

    /**
     * 当传入的page=null时返回默认页码，否则返回page
     *
     * @param page 页码
     * @return 页码
     */
    @NotNull
    protected int defaultPage(Integer page) {
        return (page == null || page < 1) ? DEFAULT_PAGE : page;
    }

    /**
     * 当传入的rows==null时返回默认每页大小，否则反回rows
     *
     * @param rows 每页大小
     * @return 每页大小
     */
    @NotNull
    protected int defaultRows(Integer rows) {
        return (rows == null || rows < 1) ? DEFAULT_ROWS : rows;
    }

    /**
     * 错误界面视图
     *
     * @param title 变体
     * @param msg   消息
     * @return MV
     */
    protected ModelAndView error(String title, String msg) {
        val mv = new ModelAndView(ERROR_VIEW_NAME);
        mv.addObject("title", title);
        mv.addObject("msg", msg);
        return mv;
    }
}
