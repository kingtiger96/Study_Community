package com.lican.community.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtils {
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    public static String md5(String key){
        if(StringUtils.isAllBlank(key)){
            return null;
        }

        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJSONString(int code, String msg, Map<String, Object> map){
        JSONObject object = new JSONObject();
        object.put("code",code);
        object.put("msg",msg);
        if(map!=null){
            for(String key:map.keySet()){
                object.put(key,map.get(key));
            }
        }
        return object.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }

    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name","lican");
        map.put("age",25);
        System.out.println(getJSONString(200, "success", map));
    }
}
