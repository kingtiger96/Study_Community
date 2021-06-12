package com.lican.community.controller.interceptor;

import com.lican.community.entity.UserEntity;
import com.lican.community.service.DateService;
import com.lican.community.utils.HostHolder;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DateInterceptor implements HandlerInterceptor {
    @Autowired
    private DateService dateService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计UV
        String  ip = request.getRemoteHost();
        dateService.recordUV(ip);

        //统计DAU
        UserEntity user = hostHolder.getUser();
        if(user!=null){
            dateService.recordDAU(user.getId());
        }



        return true;
    }
}
