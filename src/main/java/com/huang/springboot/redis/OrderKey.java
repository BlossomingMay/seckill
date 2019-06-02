package com.huang.springboot.redis;

public class OrderKey extends  BasefPrefix{

    public OrderKey(int expireSeconds, String prefix) {

        super(expireSeconds,prefix);
    }

    public static OrderKey getFlashSaleOrderByUidGid = new OrderKey(0,"fs_order_uid_gid");

}
