package com.lican.community.service.impl;

import com.lican.community.entity.DiscussPostEntity;
import com.lican.community.mapper.DiscussPostMapper;
import com.lican.community.service.DiscussPostService;
import com.lican.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Override
    public List<DiscussPostEntity> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    @Override
    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPostEntity post) {
        if(post==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        post.setTitle(sensitiveFilter.filter(HtmlUtils.htmlEscape(post.getTitle())));
        post.setContent(sensitiveFilter.filter(HtmlUtils.htmlEscape(post.getContent())));
        return discussPostMapper.insertDiscussPost(post);
    }

    @Override
    public DiscussPostEntity findDiscussPost(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }
}
