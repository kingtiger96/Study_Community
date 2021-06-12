package com.lican.community.mapper;

import com.lican.community.entity.UserEntity;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    void selectById() {
        System.out.println(userMapper.selectById(2));
    }

    @Test
    void selectByName() {
    }

    @Test
    void selectByEmail() {
    }

    @Test
    void insertUser() {
        //username, password, salt, email, type, status, activation_code, header_url, create_time
        UserEntity u = new UserEntity();
        u.setUsername("zhoujiang");
        u.setPassword("123456");
        u.setSalt("abc");
        u.setType(0);
        u.setStatus(0);
        u.setActivationCode("laohujiang");
        u.setHeaderUrl("http://www.offer.com/zhoujiang");
        userMapper.insertUser(u);
    }

    @Test
    void updateStatus() {
    }

    @Test
    void updateHeader() {
    }

    @Test
    void updatePassword() {
    }
}