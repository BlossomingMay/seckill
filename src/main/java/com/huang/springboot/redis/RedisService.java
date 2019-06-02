package com.huang.springboot.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;

    @Autowired
    RedisConfig redisConfig;

    //根据key获取value
    public <T> T get(KeyPrefix keyPrefix,String key,Class<T>clazz){
        Jedis jedis = null;
        try {
            String realKey = keyPrefix.getPrefix()+key;
            jedis = jedisPool.getResource();
            String str = jedis.get(realKey);
            T t = stringToBean(str,clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }
    //设置key,value
    public <T> Boolean set(KeyPrefix keyPrefix,String key,T value){
        Jedis jedis = null;
        try {
            //获取附加的前缀并结合起来作为主键
            String realKey = keyPrefix.getPrefix()+key;
            //获取是否有过期的时间
            int seconds = keyPrefix.expireSeconds();
            //从连接池获得连接
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            //小于零默认数据不过期
            if(seconds<=0){
                jedis.set(realKey,str);
            }else{
                jedis.setex(realKey,seconds,str);
            }
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    //根据key判断是否存在value
    public <T> Boolean exists(KeyPrefix keyPrefix,String key){
        Jedis jedis = null;
        try {
            String realKey = keyPrefix.getPrefix()+key;
            jedis = jedisPool.getResource();
            return jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    //把value减一，如果对象无法做减法，就认为是0然后-1
    public <T> Long decr(KeyPrefix keyPrefix,String key){
        Jedis jedis = null;
        try {
            String realKey = keyPrefix.getPrefix()+key;
            jedis = jedisPool.getResource();
            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }


    /**
     * 增加值
     * */
    public <T> Long incr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            //生成真正的key
            String realKey  = prefix.getPrefix() + key;
            return  jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    public <T> boolean delete(KeyPrefix keyPrefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix()+key;
            long ret = jedis.del(realKey);
            return ret>0;
        } finally {
            returnToPool(jedis);
        }
    }

    //把字符串转换为Bean
    private <T> T stringToBean(String str,Class<T> clazz){
        if(str == null|| str.length() <= 0 || clazz==null){
            return null;
        }
        if(clazz == int.class || clazz == Integer.class){
            return (T)Integer.valueOf(str);
        } else if(clazz == String.class){
            return (T)str;
        }else if(clazz == long.class || clazz == Long.class){
            return (T)Integer.valueOf(str);
        }else{
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }
    }
    //把bean转化为String
    private <T> String beanToString(T value){
        if(value == null){
            return null;
        }
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class){
            return ""+value;
        } else if(clazz == String.class){
            return (String)value;
        }else if(clazz == long.class || clazz == Long.class){
            return ""+value;
        }else{
            return JSON.toJSONString(value);
        }
    }
    //关闭jedis连接
    private void returnToPool(Jedis jedis){
        if(jedis!=null) {
            jedis.close();
        }
    }



}
