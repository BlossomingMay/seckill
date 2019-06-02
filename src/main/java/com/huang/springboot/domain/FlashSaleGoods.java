package com.huang.springboot.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class FlashSaleGoods {
    private Long id;
    private Long goodsId;
    private Integer stockCount;
    private Date startDate;
    private  Date endDate;

}
