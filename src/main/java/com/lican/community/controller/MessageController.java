package com.lican.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.lican.community.entity.DiscussPostEntity;
import com.lican.community.entity.Message;
import com.lican.community.entity.Page;
import com.lican.community.entity.UserEntity;
import com.lican.community.service.MessageService;
import com.lican.community.service.UserService;
import com.lican.community.utils.CommunityConstant;
import com.lican.community.utils.CommunityUtils;
import com.lican.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //私信列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page<DiscussPostEntity> page){
        UserEntity user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if(conversationList!=null){
            for(Message message:conversationList){
                Map<String, Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target",userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

        //查询未读信息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);

        model.addAttribute("letterUnreadCount",letterUnreadCount);

        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(Model model, @PathVariable("conversationId") String conversationId, Page<DiscussPostEntity> page) {
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }

        model.addAttribute("letters", letters);

        model.addAttribute("target", getLetterTarget(conversationId));

        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessages(ids);
        }

        return "site/letter-detail";

    }

    private UserEntity getLetterTarget(String conversationId) {
        String[] split = conversationId.split("_");
        int target1 = Integer.parseInt(split[0]);
        int target2 = Integer.parseInt(split[1]);

        if (hostHolder.getUser().getId() == target1) {
            return userService.findUserById(target2);
        } else {
            return userService.findUserById(target1);
        }
    }

    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if (letterList!=null){
            for (Message message:letterList) {
                if(hostHolder.getUser().getId() == message.getToId() && message.getStatus()==0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content){
        System.out.println("send。。。。。。。。。。。。。。。。。。。");
        UserEntity target = userService.findUserByName(toName);
        if(target==null){
            return CommunityUtils.getJSONString(1,"用户名不存在");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());

        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }

        else{
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }

        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        return CommunityUtils.getJSONString(0);
    }

    @GetMapping("/notice/list")
    public String getNoticeList(Model model) {
        UserEntity user = hostHolder.getUser();

        //查询评论通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);

        if (message != null) {
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));

            int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("count", noticeCount);

            int letterUnreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("unreadCount", letterUnreadCount);
            model.addAttribute("commentNotice", messageVo);
        }

        //查询点赞通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);

        if (message != null) {
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));

            int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("count", noticeCount);

            int letterUnreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVo.put("unreadCount", letterUnreadCount);
            model.addAttribute("likeNotice", messageVo);
        }

        //查询关注通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        if (message != null) {
            
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            //无帖子id
            //messageVo.put("postId", data.get("postId"));

            int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count", noticeCount);

            int letterUnreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("unreadCount", letterUnreadCount);
            model.addAttribute("followNotice", messageVo);
        }


        //查询未读消息的数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    @GetMapping("/notice/detail/{topic}")
    public String getNoticesDetail(@PathVariable("topic") String topic, Page<DiscussPostEntity> page, Model model) {
        UserEntity user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();

        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                //通知
                map.put("notice", notice);
                //内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));
                //通知作者
                map.put("fromUser", userService.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices",noticeVoList);

        //设置已读
        //获取需要设置已读的id
        List<Integer> ids = getLetterIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessages(ids);

        }
        return "/site/notice-detail";
    }

}
