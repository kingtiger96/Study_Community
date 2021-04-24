package com.lican.community.mapper;

import com.lican.community.entity.DiscussPostEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DiscussPostMapperTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    void selectDiscussPosts() {
        System.out.println(discussPostMapper.selectDiscussPostRows(0));
    }

    @Test
    void selectDiscussPostRows() {
        List<DiscussPostEntity> list = discussPostMapper.selectDiscussPosts(0,1,10);
    }
}