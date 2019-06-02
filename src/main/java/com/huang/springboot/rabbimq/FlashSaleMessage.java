package com.huang.springboot.rabbimq;

import com.huang.springboot.domain.FlashSaleUser;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FlashSaleMessage {
    private long goodsId;
    private FlashSaleUser user;
}
