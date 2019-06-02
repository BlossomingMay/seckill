package com.huang.springboot.redis;


public class FlashSaleKey extends BasefPrefix {

    public FlashSaleKey(int expireTime,String prefix) {
        super(expireTime,prefix);
    }

    public static FlashSaleKey isGoodsOver = new FlashSaleKey(0,"gover");
    public static FlashSaleKey getFlashSalePath = new FlashSaleKey(60,"fspath");
    public static FlashSaleKey getFlashSaleVerifyKey = new FlashSaleKey(120,"verify_code");


}
