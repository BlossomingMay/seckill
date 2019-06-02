package com.huang.springboot.controller;

import com.huang.springboot.domain.FlashSaleUser;
import com.huang.springboot.domain.OrderInfo;
import com.huang.springboot.redis.RedisService;
import com.huang.springboot.result.CodeMsg;
import com.huang.springboot.result.Result;
import com.huang.springboot.service.FlashSaleService;
import com.huang.springboot.service.GoodsService;
import com.huang.springboot.service.OrderService;
import com.huang.springboot.vo.GoodsVo;
import com.huang.springboot.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    FlashSaleService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;


    @RequestMapping("/order_detail")
    @ResponseBody
    public Result<OrderDetailVo> getOrderDetail(Model model, FlashSaleUser user,
                                      @RequestParam("orderId") long orderId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }

}
