package com.lican.community.controller;

import com.lican.community.entity.DiscussPostEntity;
import com.lican.community.entity.UserEntity;
import com.lican.community.service.DiscussPostService;
import com.lican.community.service.UserService;
import com.lican.community.utils.CommunityUtils;
import com.lican.community.utils.HostHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        UserEntity user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtils.getJSONString(403, "你还没有登录");
        }

        DiscussPostEntity post = new DiscussPostEntity();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        post.setUpdateTime(new Date());

        discussPostService.addDiscussPost(post);

        return CommunityUtils.getJSONString(0, "发送成功");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    @ResponseBody
    public String addDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model){
        DiscussPostEntity post = discussPostService.findDiscussPost(discussPostId);
        model.addAttribute("post",post);

        UserEntity user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        return post.toString()+user.toString();
    }
}
