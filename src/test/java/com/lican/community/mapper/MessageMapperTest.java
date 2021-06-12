package com.lican.community.mapper;

import com.lican.community.entity.Message;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class MessageMapperTest {
    @Autowired
    private MessageMapper messageMapper;

    @Test
    void selectConversations() {
        System.out.println(messageMapper.selectConversations(111, 0, 4));
    }

    @Test
    void selectConversationCount() {
        System.out.println(messageMapper.selectConversationCount(111));
    }

    @Test
    void selectLetters() {
        System.out.println(messageMapper.selectLetters("111_112", 0, 2));
    }

    @Test
    void selectLetterCount() {
        System.out.println(messageMapper.selectLetterCount("111_112"));
    }

    @Test
    void selectLetterUnreadCount() {
        System.out.println(messageMapper.selectLetterUnreadCount(111,""));
    }
}