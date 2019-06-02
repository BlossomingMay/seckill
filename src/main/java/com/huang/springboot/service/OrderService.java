package com.huang.springboot.service;

import com.huang.springboot.dao.OrderDao;
import com.huang.springboot.domain.FlashSaleOrder;
import com.huang.springboot.domain.FlashSaleUser;
import com.huang.springboot.domain.OrderInfo;
import com.huang.springboot.redis.OrderKey;
import com.huang.springboot.redis.RedisService;
import com.huang.springboot.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    public FlashSaleOrder getFlashSaleOrderByUserIdGoodsId(Long userId, long gouodsId) {
        //不从数据库检查是否已经有一个秒杀订单而是去缓存取
        //return orderDao.getFlashSaleOrderByUserIdGoodsId(id,goodsId);
        return redisService.get(OrderKey.getFlashSaleOrderByUidGid,userId+"_"+gouodsId,FlashSaleOrder.class);

    }

    @Transactional
    public OrderInfo createOrder(GoodsVo goods, FlashSaleUser flashSaleUser) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getGoodsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(flashSaleUser.getId());
        orderDao.insert(orderInfo);

        FlashSaleOrder order = new FlashSaleOrder();
        order.setGoodsId(goods.getId());
        order.setOrderId(orderInfo.getId());
        order.setUserId(flashSaleUser.getId());
        orderDao.insertFlashSaleOrder(order);

        redisService.set(OrderKey.getFlashSaleOrderByUidGid,flashSaleUser.getId()+"_"+goods.getId(),order);

        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return  orderDao.getOrderById(orderId);
    }
}
