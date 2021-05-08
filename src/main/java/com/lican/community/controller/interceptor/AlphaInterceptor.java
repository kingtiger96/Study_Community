package com.lican.community.controller.interceptor;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class AlphaInterceptor implements HandlerInterceptor {
    private static final Logger loggger = LoggerFactory.getLogger(AlphaInterceptor.class);

    //在Controller前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception{
        loggger.debug(("preHandle: "+handle.toString()));
        return true;
    }

    //在Controller后执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handle, ModelAndView modelAndView
    ) throws Exception{
        loggger.debug("postHandle: "+handle.toString());
    }

    //TemplateEngine之后执行
    @Override
    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handle, Exception ex) throws Exception{
        loggger.debug("afterCompletion: "+handle.toString());
    }
}
