package com.lican.community.controller.interceptor;

import com.lican.community.entity.LoginTicket;
import com.lican.community.entity.UserEntity;
import com.lican.community.service.UserService;
import com.lican.community.utils.CookieUtil;
import com.lican.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        //从Cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");

        if(ticket!=null){
            //查看凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查凭证是否有效

            if(loginTicket!=null && loginTicket.getStatus()==0 && loginTicket.getExpired().after(new Date())){
                //根据凭证查询用户
                UserEntity user = userService.findUserById(loginTicket.getUserId());

                //在本次请求中持有用户
                //浏览器和服务器多对一问题，为了考虑多线程下的安全性
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws  Exception{
        UserEntity user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
