package com.lican.community.controller;

import com.lican.community.entity.DiscussPostEntity;
import com.lican.community.entity.Page;
import com.lican.community.entity.UserEntity;
import com.lican.community.service.ElasticsearchService;
import com.lican.community.service.LikeService;
import com.lican.community.service.UserService;
import com.lican.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Controller
public class SearchController implements CommunityConstant {


    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {

        org.springframework.data.domain.Page<DiscussPostEntity> result =
                elasticsearchService.searchDiscussPost(keyword, page.getCurrent()-1, page.getLimit());

        //聚合数据
        List<Map<String, Object>> discussposts = new ArrayList<>();
        if (result != null) {
            for (DiscussPostEntity post : result) {
                Map<String, Object> map = new HashMap<>();

                //帖子
                map.put("post", post);

                //作者
                UserEntity user = userService.findUserById(post.getUserId());
                map.put("user", user);

                //点赞数量
                map.put("like", likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));

                discussposts.add(map);

            }
        }
        model.addAttribute("discussPosts", discussposts);
        model.addAttribute("keyword",keyword);

        //分页信息
        page.setPath("/search?keyword="+keyword);
        page.setRows(result == null ? 0 : (int) result.getTotalElements());

        return "site/search";
    }
}
