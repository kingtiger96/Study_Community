package com.lican.community.service;

public interface LikeService {
    //userId为点赞的人 需要entityUserId被点赞的人
    void like(int userId, int entityType, int entityId, int entityUserId);
    long findEntityLikeCount(int entityType, int entityId);
    int findEntityLikeStatus(int userId, int entityType, int entityId);
    //查询某个用户获得的赞
    int findUserLikeCount(int userId);

}
