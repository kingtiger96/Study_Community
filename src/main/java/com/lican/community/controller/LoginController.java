package com.lican.community.controller;

import com.google.code.kaptcha.Producer;
import com.lican.community.config.KaptchaConfig;
import com.lican.community.entity.UserEntity;
import com.lican.community.service.UserService;
import com.lican.community.utils.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer producer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, UserEntity user){
        Map<String, Object> map = userService.register(user);
        if(ObjectUtils.isEmpty(map)){
            model.addAttribute("msg","注册成功，我们已经给您发送了一封邮件，请尽快激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg", user.getUsername());
            model.addAttribute("passwordMsg",user.getPassword());
            model.addAttribute("emailMsg",user.getEmail());
            return "/site/register";
        }

    }

    @RequestMapping(path = "activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model modle, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int result = userService.activation(userId,code);
        if(result== ACTIVATE_SUCCESS){
            modle.addAttribute("msg","该账号已经激活成功，请登录！");
            modle.addAttribute("target","/login");
        }else if(result == ACTIVATE_SUCCESS){
            modle.addAttribute("msg","该账号已经激活过了，无效操作！");
            modle.addAttribute("target","/index");
        }else{
            modle.addAttribute("msg","激活失败，请提供正确的激活码");
            modle.addAttribute("target","/login");
        }
        return "/site/login";
    }

    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);

        session.setAttribute("kaptcha",text);
        response.setContentType("image/png");

        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("验证码获取失败！");
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean isRemembered, Model model, HttpServletResponse response, HttpSession session){
        String kaptcha = (String) session.getAttribute("kaptcha");
        System.out.println(kaptcha);
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !(kaptcha).equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/login";
        }

        int expiredSeconds = isRemembered ? CommunityConstant.DEFAULT_EXPIRED_SECONDS : CommunityConstant.REMEMBER_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username,password,expiredSeconds);
        
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }
}
