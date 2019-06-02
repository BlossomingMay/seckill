package com.huang.springboot.rabbimq;

import com.huang.springboot.domain.FlashSaleOrder;
import com.huang.springboot.domain.FlashSaleUser;
import com.huang.springboot.redis.RedisService;
import com.huang.springboot.service.FlashSaleService;
import com.huang.springboot.service.GoodsService;
import com.huang.springboot.service.OrderService;
import com.huang.springboot.util.TransferUtil;
import com.huang.springboot.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceirver {

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    FlashSaleService flashSaleService;
    private static Logger log = LoggerFactory.getLogger(MQReceirver.class);

    @RabbitListener(queues=MQConfig.FLASH_SALE_QUEUE)
    public void receive(String message){
        FlashSaleMessage fm =  TransferUtil.stringToBean(message,FlashSaleMessage.class);
        FlashSaleUser user = fm.getUser();
        long goodsId = fm.getGoodsId();
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock<=0){
            return;
        }
        //判断缓存中是否有用户订单，如果有证明该用户已经秒杀到了该物品，返回
        FlashSaleOrder order = orderService.getFlashSaleOrderByUserIdGoodsId(user.getId(),goodsId );
        if(order!=null){
            return;
        }
        flashSaleService.seckill(user,goods);
    }








//    @RabbitListener(queues=MQConfig.QUEUE)
//    public void receive(String message){
//        log.info("receive messages"+message);
//    }
//
//    @RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String message){
//        log.info("receive topic key1 messages"+message);
//    }
//
//    @RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String message){
//        log.info("receive topic key2 messages"+message);
//    }
//
//    @RabbitListener(queues=MQConfig.HEADER_QUEUE)
//    public void receiveHeaderQueue(byte[] message) {
//        log.info(" header  queue message:"+new String(message));
//    }
}
