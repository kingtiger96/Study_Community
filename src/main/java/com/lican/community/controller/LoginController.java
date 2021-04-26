package com.lican.community.controller;

import com.lican.community.entity.UserEntity;
import com.lican.community.service.UserService;
import com.lican.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

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
            model.addAttribute("usernameMsg", user.getUserName());
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
}
