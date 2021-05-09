package com.lican.community.utils;

import com.lican.community.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 持有用户信息，用于代替session对象
 */
@Component
public class HostHolder {
    private ThreadLocal<UserEntity> users = new ThreadLocal<>();

    public void setUser(UserEntity user){
        users.set(user);
    }

    public UserEntity getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
