package com.huang.springboot.redis;

public class AccessKey extends BasefPrefix{
    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public AccessKey(int expireSeconds) {
        super(expireSeconds, "access");
    }

    public static AccessKey getAccessKey(int expireSeconds){
        return new AccessKey(expireSeconds);
    }

}
