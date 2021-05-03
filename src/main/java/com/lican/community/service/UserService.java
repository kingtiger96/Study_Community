package com.lican.community.service;

import com.lican.community.entity.UserEntity;

import java.util.Map;

public interface UserService {
    UserEntity findUserById(int id);
    Map<String, Object> register(UserEntity u);
    int activation(int userId, String code);
    Map<String, Object> login(String userName, String password, int expired);
}
