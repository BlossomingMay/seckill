package com.huang.springboot.redis;



public class FlashSaleUserKey extends BasefPrefix{

    public static final int TOKEN_EXPIRE = 3600*24*2;

    public FlashSaleUserKey(int expireSeconds, String prefix) {
        super(expireSeconds,prefix);
    }
    public static FlashSaleUserKey token = new FlashSaleUserKey(TOKEN_EXPIRE,"tk");
    public static FlashSaleUserKey getByName = new FlashSaleUserKey(TOKEN_EXPIRE,"name");
    public static FlashSaleUserKey getById = new FlashSaleUserKey(0,"id");

}
