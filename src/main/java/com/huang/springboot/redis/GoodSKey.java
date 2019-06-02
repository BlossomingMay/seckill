package com.huang.springboot.redis;

import com.huang.springboot.domain.Goods;

public class GoodSKey extends BasefPrefix {
    public static GoodSKey getGoodsList = new GoodSKey(60,"gl");
    public static GoodSKey getGoodsDetail = new GoodSKey(60,"gd");
    public static GoodSKey getFlashSaleGoodsStock = new GoodSKey(0,"gs");

    public GoodSKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public GoodSKey(String prefix) {
        super(prefix);
    }
}
