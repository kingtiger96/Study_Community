package com.lican.community.controller;

import com.lican.community.entity.*;
import com.lican.community.event.EventProducer;
import com.lican.community.service.DiscussPostService;
import com.lican.community.service.LikeService;
import com.lican.community.service.UserService;
import com.lican.community.service.CommentService;
import com.lican.community.utils.CommunityConstant;
import com.lican.community.utils.CommunityUtils;
import com.lican.community.utils.HostHolder;

import com.lican.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

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
        post.setType(0);
        post.setStatus(0);
        post.setCommentCount(0);
        post.setScore(50.0);


        discussPostService.addDiscussPost(post);
        System.out.println(post);
//        int postId = discussPostService.findMaxId();
//
//        System.out.println(post);

        //触发发帖事件 向es中存贴子
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);


        // 计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,post.getId());

        return CommunityUtils.getJSONString(0, "发送成功");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)

    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Page<DiscussPostEntity> page, Model model){
        DiscussPostEntity post = discussPostService.findDiscussPost(discussPostId);
        model.addAttribute("post",post);

        UserEntity user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        //点赞
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPostId);

        model.addAttribute("likeCount", likeCount);


        //点赞状态
        int likeStatus = hostHolder.getUser()==null ? 0 :likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeStatus", likeStatus);

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


            //点赞
            likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,comment.getId());

            commentVo.put("likeCount", likeCount);


            //点赞状态
            likeStatus = hostHolder.getUser()==null ? 0 :likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,comment.getId());
            commentVo.put("likeStatus", likeStatus);

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
                //评论点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                replyVo.put("likeCount", likeCount);

                likeStatus = hostHolder.getUser()==null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                //评论点赞状态
                replyVo.put("target", target);
            }
            commentVo.put("replys", replyVoList);
            commentVo.put("likeStatus",likeStatus);

            int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());

            commentVo.put("replyCount",replyCount);
            commentVoList.add(commentVo);
        }

        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }

    //置顶
    @RequestMapping(path = "/top", method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id){
        discussPostService.updateType(id,1);

        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);

        eventProducer.fireEvent(event);

        return CommunityUtils.getJSONString(0);
    }

    //加精
    @RequestMapping(path = "/wonderful", method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id){
        discussPostService.updateStatus(id,1);

        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);

        eventProducer.fireEvent(event);
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,id);

        return CommunityUtils.getJSONString(0);
    }

    //加精
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id){
        discussPostService.updateStatus(id,2);

        //触发删帖事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);

        eventProducer.fireEvent(event);

        return CommunityUtils.getJSONString(0);
    }
}
