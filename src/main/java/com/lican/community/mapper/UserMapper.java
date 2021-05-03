package com.lican.community.mapper;

import com.lican.community.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    UserEntity selectById(int id);

    UserEntity selectByName(String userName);

    UserEntity selectByEmail(String email);

    int insertUser(UserEntity user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
