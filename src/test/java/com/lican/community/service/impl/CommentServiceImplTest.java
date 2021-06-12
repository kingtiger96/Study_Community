package com.lican.community.service.impl;

import com.lican.community.entity.Comment;
import com.lican.community.service.CommentService;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;


@SpringBootTest
class CommentServiceImplTest {
    @Autowired
    private CommentService commentService;

    @Test
    void addComment() {
        Comment commont = new Comment();
        commont.setUserId(4);
        commont.setEntityType(1);
        commont.setEntityId(2);
        commont.setTargetId(0);
        commont.setContent("嫖娼来吗，500一次");
        commont.setStatus(0);
        commont.setCreateTime(new Date());

        System.out.println(commentService.addComment(commont));
    }
}