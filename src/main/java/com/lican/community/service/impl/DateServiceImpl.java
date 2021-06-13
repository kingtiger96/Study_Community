package com.lican.community.service.impl;

import com.lican.community.service.DateService;
import com.lican.community.utils.RedisKeyUtil;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DateServiceImpl implements DateService {
    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    @Override
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }

    @Override
    public long calculateUV(Date startTime, Date endTIme) {
        if(startTime==null || endTIme==null){
            throw new IllegalArgumentException("参数不能为空");
        }

        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        while(!calendar.getTime().after(endTIme)){
            String key = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE,1);
        }
        String redisKey = RedisKeyUtil.getUVKey(df.format(startTime),df.format(endTIme));
        redisTemplate.opsForHyperLogLog().union(redisKey,keyList.toArray());

        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    @Override
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey,userId,true);
    }

    @Override
    public long calculateDAU(Date startTime, Date endTime) {
        if(startTime==null || endTime==null){
            throw new IllegalArgumentException("参数不能为空");
        }

        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        while(!calendar.getTime().after(endTime)){
            String key = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE,1);
        }

        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(df.format(startTime),df.format(endTime));
                redisConnection.bitOp(RedisStringCommands.BitOperation.OR,redisKey.getBytes(),keyList.toArray(new byte[0][0]));
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });

    }


}
