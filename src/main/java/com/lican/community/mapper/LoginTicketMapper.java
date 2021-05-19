package com.lican.community.mapper;

import com.lican.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@Deprecated
public interface LoginTicketMapper {
    int insertLoginTicket(LoginTicket ticket);
    LoginTicket selectLoginTicket(String ticket);
    int updateStatus(String ticket, int status);
}
