package com.huang.springboot.redis;

public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();
}
