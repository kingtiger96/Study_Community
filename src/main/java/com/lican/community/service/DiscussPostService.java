package com.lican.community.service;

import com.lican.community.entity.DiscussPostEntity;

import java.util.List;

public interface DiscussPostService {
    List<DiscussPostEntity> findDiscussPosts(int userId, int offset, int limit);

    int findDiscussPostRows(int userId);
}
