package com.lican.community.service;

import com.lican.community.entity.DiscussPostEntity;
import org.springframework.data.domain.Page;

public interface ElasticsearchService {
    void saveDiscussPost(DiscussPostEntity post);

    void deleteDiscussPost(int id);

    Page<DiscussPostEntity> searchDiscussPost(String keyword, int current, int limit);
}
