package com.lican.community.controller;

import com.lican.community.entity.DiscussPostEntity;
import com.lican.community.entity.Page;
import com.lican.community.entity.UserEntity;
import com.lican.community.service.DiscussPostService;
import com.lican.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPostEntity> list = (discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit()));
        List<Map<String, Object>> discussPosts = new ArrayList<>();

        for(DiscussPostEntity post : list){
            Map<String, Object> map = new HashMap<>();
            map.put("post",post);
            UserEntity user = userService.findUserById(post.getUserId());
            map.put("user",user);
            discussPosts.add(map);
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

}
