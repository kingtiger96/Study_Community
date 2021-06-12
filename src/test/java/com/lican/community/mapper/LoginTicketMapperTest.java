package com.lican.community.mapper;

import com.lican.community.entity.LoginTicket;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;


@SpringBootTest
class LoginTicketMapperTest {
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    void insertLoginTicket() {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(101);
        ticket.setTicket("abc");
        ticket.setStatus(0);
        ticket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insertLoginTicket(ticket);
    }

    @Test
    void selectLoginTicket() {
        System.out.println(loginTicketMapper.selectLoginTicket("abc"));
    }

    @Test
    void updateStatus() {
        loginTicketMapper.updateStatus("abc",1);
    }
}