package com.huang.springboot.redis;

public class BasefPrefix implements KeyPrefix{
    private String prefix;
    private int expireSeconds;

    public BasefPrefix(int expireSeconds,String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public BasefPrefix(String prefix){
        this.expireSeconds = 0;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {
        //0默认不过期
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className+":"+prefix;
    }
}
