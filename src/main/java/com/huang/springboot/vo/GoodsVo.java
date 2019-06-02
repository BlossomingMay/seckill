package com.huang.springboot.vo;

import com.huang.springboot.domain.Goods;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GoodsVo extends Goods {

    private Double flashSalePrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
