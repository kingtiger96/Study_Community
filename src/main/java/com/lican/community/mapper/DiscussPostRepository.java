package com.lican.community.mapper;

import com.lican.community.entity.DiscussPostEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPostEntity, Integer> {
}
