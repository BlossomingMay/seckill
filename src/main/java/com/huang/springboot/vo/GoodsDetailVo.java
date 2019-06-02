package com.huang.springboot.vo;

import com.huang.springboot.domain.FlashSaleUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsDetailVo {
    private FlashSaleUser flashSaleUser;
    private int flashSaleStatus;
    private int remainSeconds ;
    GoodsVo goods;
}
