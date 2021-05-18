package com.lican.community.controller;

import com.lican.community.annotation.LoginRequired;
import com.lican.community.entity.UserEntity;
import com.lican.community.service.FollowService;
import com.lican.community.service.LikeService;
import com.lican.community.service.UserService;
import com.lican.community.utils.CommunityConstant;
import com.lican.community.utils.CommunityUtils;
import com.lican.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;


@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private  static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.uploadPath}")
    private String uploadPath;

    @Value("${community.path.domain")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error","您没有选择图片!");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确!");
            return "/site/setting";
        }

        //生成随机文件名
        fileName = CommunityUtils.generateUUID() + suffix;

        //确定文件存放的路径
        File dest = new File(uploadPath+"/"+fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败，服务器发生异常！",e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！");
        }

        //更新当前用户头像的路径（web访问路径）
        UserEntity user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";

    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存储图片的位置
        fileName = uploadPath + "/" + fileName;

        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        response.setContentType("image/"+suffix);
        try {
            OutputStream os = response.getOutputStream();

            FileInputStream fis = new FileInputStream((fileName));
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer))!=-1){
                os.write(buffer);
            }
        } catch (IOException e) {
            logger.error(("读取图像失败:"+e.getMessage()));
        }
    }


    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        UserEntity user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }

        //用户
        model.addAttribute("user",user);

        long likeCount = likeService.findUserLikeCount(userId);

        System.out.println(likeCount);

        model.addAttribute("likeCount",likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);

        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);

        //是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser()!=null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }

}
