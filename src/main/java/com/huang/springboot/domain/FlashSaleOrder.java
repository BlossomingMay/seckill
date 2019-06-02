package com.huang.springboot.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlashSaleOrder {
    private Long id;
    private Long userId;
    private Long orderId;
    private Long goodsId;

}
