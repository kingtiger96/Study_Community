package com.lican.community.service.impl;

import com.lican.community.entity.LoginTicket;
import com.lican.community.entity.UserEntity;
import com.lican.community.mapper.LoginTicketMapper;
import com.lican.community.mapper.UserMapper;
import com.lican.community.service.UserService;
import com.lican.community.utils.CommunityConstant;
import com.lican.community.utils.CommunityUtils;
import com.lican.community.utils.MailClient;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService, CommunityConstant {
    //private Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;


    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public UserEntity findUserById(int id) {
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(UserEntity u){
        //检查参数是否合法
        Map<String, Object> map = new HashMap<>();
        if(u==null){
            map.put("msg","参数不能为空!");
            //throw new IllegalArgumentException("参数不能为空!");
            return map;
        }

        if(StringUtils.isAllBlank(u.getUserName(),u.getPassword(),u.getEmail())){
            ////logger.info("参数不能为空，用户名，密码，邮箱不能为空为空，userName  ::: {}, password ::: {}, email ::: {}", u.getUserName(), u.getPassword(), u.getEmail());
            map.put("msg","用户名，密码，邮箱不能为空!");
            return map;
        }

        //验证参数是否已经存在
        //验证用户名
        UserEntity user1 = userMapper.selectByName(u.getUserName());
        System.out.println(user1);
        if(user1!=null) {
            //logger.info("userName ::: {} 已经存在!", u.getUserName());
            map.put("msg",String.format("userName ::: %s 已经存在!", u.getUserName()));
            return map;
        }

        //验证邮箱
        UserEntity user2 = userMapper.selectByEmail(u.getEmail());
        if(user2!=null) {
            //logger.info("email ::: {} 已经存在!", u.getEmail());
            map.put("msg",String.format("email ::: %s 已经存在!", u.getEmail()));
            return map;
        }

        //普通用户
        u.setSalt(CommunityUtils.generateUUID().substring(0,5));
        u.setPassword(CommunityUtils.md5(u.getPassword()+u.getSalt()));
        u.setType(0);
        u.setStatus(0);
        u.setActivationCode(CommunityUtils.generateUUID());
        u.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        userMapper.insertUser(u);

        String url = domain + contextPath + "/activation/" + u.getId() + "/" + u.getActivationCode();
        Context context = new Context();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.senMail(u.getEmail(),"账户激活",content);

        return map;
    }

    public int activation(int userId, String code){
        UserEntity u = userMapper.selectById(userId);
        if(u.getStatus() == 1){
            return ACTIVATE_REPEAT;
        }else if(u.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            return ACTIVATE_SUCCESS;
        }else{
            return ACTIVATE_FAILED;
        }
    }

    @Override
    public Map<String, Object> login(String userName, String password, int expired) {
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isAllBlank(userName)){
            map.put("usernameMsg","用户名不能为空！");
            return map;
        }
        if(StringUtils.isAllBlank(password)){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }

        //验证账号
        UserEntity user = userMapper.selectByName(userName);
        if(user==null){
            map.put("usernameMsg","该用户不存在！");
            return map;
        }
        //验证状态
        if(user.getStatus()==0){
            map.put("usernameMsg","该用户未激活！");
            return map;
        }
        //验证密码
        password = CommunityUtils.md5(password+user.getSalt());
        if(!password.equals(user.getPassword())){
            map.put("passwordMsg","密码错误！");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtils.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expired * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    @Override
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket,1);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectLoginTicket(ticket);
    }

    @Override
    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId,headerUrl);
    }
}
