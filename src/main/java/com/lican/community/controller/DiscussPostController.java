package com.lican.community.controller;

import com.lican.community.entity.Comment;
import com.lican.community.entity.DiscussPostEntity;
import com.lican.community.entity.Page;
import com.lican.community.entity.UserEntity;
import com.lican.community.service.DiscussPostService;
import com.lican.community.service.UserService;
import com.lican.community.service.CommentService;
import com.lican.community.utils.CommunityConstant;
import com.lican.community.utils.CommunityUtils;
import com.lican.community.utils.HostHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

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

    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Page page, Model model){
        DiscussPostEntity post = discussPostService.findDiscussPost(discussPostId);
        model.addAttribute("post",post);

        UserEntity user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        page.setLimit((5));
        page.setPath("/discuss/detail"+discussPostId);
        page.setRows(post.getCommentCount());
        //给帖子的评论
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        for(Comment comment : commentList) {
            Map<String, Object> commentVo = new HashMap<>();
            //评论
            commentVo.put("comment", comment);
            //作者
            commentVo.put("user", userService.findUserById(comment.getUserId()));


            //回复
            List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
            List<Map<String,Object>> replyVoList = new ArrayList<>();
            for(Comment reply : replyList) {
                Map<String, Object> replyVo = new HashMap<>();
                //回复
                replyVo.put("reply", reply);
                //作者
                replyVo.put("user", userService.findUserById(reply.getUserId()));
                //回复的目标
                UserEntity target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getUserId());
                replyVo.put("target", target);
            }
            commentVo.put("replys",replyVoList);

            int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());

            commentVo.put("replyCount",replyCount);
            commentVoList.add(commentVo);
        }

        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }



}
