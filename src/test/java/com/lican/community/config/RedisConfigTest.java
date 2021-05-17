package com.lican.community.config;

import com.lican.community.CommunityApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisConfigTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey = "name";

        redisTemplate.opsForValue().set(redisKey,"汪汪汪");
        System.out.println(redisTemplate.opsForValue().get(redisKey));
    }

    @Test
    public void testHash(){
        String redisKey = "dog";

        redisTemplate.opsForHash().put(redisKey,"barking","汪汪汪");
        System.out.println(redisTemplate.opsForHash().get(redisKey,"barking"));
        System.out.println(redisTemplate.opsForHash().size(redisKey));
    }

    @Test
    public void testList(){
        String redisKey = "ids";

        redisTemplate.opsForList().leftPush(redisKey,1);
        redisTemplate.opsForList().leftPush(redisKey,2);
        redisTemplate.opsForList().leftPush(redisKey,3);
        System.out.println(redisTemplate.opsForList().index(redisKey,1));
        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,-1));
    }

    @Test
    public void testSets(){
        String redisKey = "heros";

        redisTemplate.opsForSet().add(redisKey,"刘备");
        redisTemplate.opsForSet().add(redisKey,"曹操");
        redisTemplate.opsForSet().add(redisKey,"z胡歌两");
        System.out.println(redisTemplate.opsForSet().members(redisKey));
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
    }


    @Test
    public void testZsets(){
        String redisKey = "heros:score";

        redisTemplate.opsForZSet().add(redisKey,"刘备",100);
        redisTemplate.opsForZSet().add(redisKey,"曹操",98);
        redisTemplate.opsForZSet().add(redisKey,"z胡歌两",80);
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"刘备"));
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,100));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,100));
    }


    @Test
    public void testKeys(){
        String redisKey = "heros:score";

        redisTemplate.delete(redisKey);
        System.out.println(redisTemplate.hasKey(redisKey));

        redisTemplate.expire("ids",10,TimeUnit.SECONDS);
    }


    //多次访问一个key
    @Test
    public void testBoundOperations(){
        String redisKey = "name";

        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());

    }


    //多次访问一个key
    @Test
    public void testTransactional(){


        Object execute = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "tx";
                operations.multi();
                operations.opsForSet().add(redisKey, "lican");
                operations.opsForSet().add(redisKey, "love");
                operations.opsForSet().add(redisKey, "zhoujiang");
                System.out.println(operations.opsForSet().members(redisKey));
                return operations.exec();
            }

        });
        System.out.println(execute);

    }



}