package com.lican.community.controller;

import com.lican.community.entity.UserEntity;
import com.lican.community.service.FollowService;
import com.lican.community.utils.CommunityUtils;
import com.lican.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController {
    @Autowired
    private FollowService fellowService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId){
        UserEntity user = hostHolder.getUser();

        fellowService.follow(user.getId(),entityType,entityId);

        return CommunityUtils.getJSONString(0,"已关注！");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        UserEntity user = hostHolder.getUser();

        fellowService.unFollow(user.getId(),entityType,entityId);

        return CommunityUtils.getJSONString(0,"已取消关注！");
    }
}
