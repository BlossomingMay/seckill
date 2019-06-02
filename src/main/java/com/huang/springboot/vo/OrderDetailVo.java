package com.huang.springboot.vo;

import com.huang.springboot.domain.OrderInfo;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderDetailVo {
	private GoodsVo goods;
	private OrderInfo order;
}