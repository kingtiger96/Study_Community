package com.lican.community.service.impl;

import com.lican.community.entity.UserEntity;
import com.lican.community.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

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
        u.setUserName("zhoujiang");
        u.setEmail("942433951@qq.com");
        u.setPassword("20171213zjcan");
        userService.register(u);
    }

    @Test
    void activation() {
        UserEntity u = userService.findUserById(1);
        System.out.println(userService.activation(u.getId(), u.getActivationCode()));
    }
}