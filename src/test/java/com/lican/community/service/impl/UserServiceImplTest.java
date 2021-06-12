package com.lican.community.service.impl;

import com.lican.community.entity.UserEntity;
import com.lican.community.service.UserService;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserService userService;
    @Test
    void findUserById() {
    }

    @Test
    void register() {
        UserEntity u = new UserEntity();
        u.setUsername("kingtiger");
        u.setEmail("lican01@cqu.edu.com");
        u.setPassword("19961213lc@@");

        userService.register(u);
    }

    @Test
    void activation() {
        UserEntity u = userService.findUserById(5);
        System.out.println(userService.activation(u.getId(), u.getActivationCode()));
    }
}