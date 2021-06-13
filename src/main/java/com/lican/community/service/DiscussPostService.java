package com.lican.community.service;

import com.lican.community.entity.DiscussPostEntity;

import java.util.List;

public interface DiscussPostService {
    List<DiscussPostEntity> findDiscussPosts(int userId, int offset, int limit,int orderMode);

    int findDiscussPostRows(int userId);

    int addDiscussPost(DiscussPostEntity post);

    DiscussPostEntity findDiscussPost(int id);

    int updateCommentCount(int id, int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int findMaxId();

    int updateScore(int id, double score);

}
