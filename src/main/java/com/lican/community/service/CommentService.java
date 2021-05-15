package com.lican.community.service;

import com.lican.community.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findCommentsByEntity(int entity_type, int entity_id, int offset, int limit);
    int findCommentCount(int entityType, int entityId);
    int addComment(Comment comment);
}
