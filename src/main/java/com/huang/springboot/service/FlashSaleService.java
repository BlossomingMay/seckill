package com.huang.springboot.service;

import com.huang.springboot.domain.FlashSaleOrder;
import com.huang.springboot.domain.FlashSaleUser;
import com.huang.springboot.domain.Goods;
import com.huang.springboot.domain.OrderInfo;
import com.huang.springboot.redis.FlashSaleKey;
import com.huang.springboot.redis.FlashSaleUserKey;
import com.huang.springboot.redis.RedisService;
import com.huang.springboot.util.MD5Util;
import com.huang.springboot.util.UUIDUtil;
import com.huang.springboot.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class FlashSaleService {
    private static char[] ops = new char[]{'+', '-', '*'};

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;


    @Transactional
    public OrderInfo seckill(FlashSaleUser flashSaleUser, GoodsVo goods) {
        //减少库存,生成秒杀订单
        boolean success = goodsService.reduceStock(goods);
        if (success) {
            return orderService.createOrder(goods, flashSaleUser);
        } else {
            //减少库存失败，商品售空，在缓存中标记
            setGoodsOver(goods.getId());
            return null;
        }

    }

    public long getFlashSaleResult(Long userId, long goodsId) {
        FlashSaleOrder order = orderService.getFlashSaleOrderByUserIdGoodsId(userId, goodsId);
        if (order != null) {//秒杀成功
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private void setGoodsOver(long goodsId) {
        redisService.set(FlashSaleKey.isGoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(FlashSaleKey.isGoodsOver, "" + goodsId);
    }

    public String createFlashSalePath(FlashSaleUser flashSaleUser, long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uudi() + "123456");
        redisService.set(FlashSaleKey.getFlashSalePath, flashSaleUser.getId() + "_" + goodsId, str);
        return str;
    }

    public boolean checkPath(FlashSaleUser flashSaleUser, long goodsId, String path) {
        if (flashSaleUser == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(FlashSaleKey.getFlashSalePath, flashSaleUser.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }


    public BufferedImage createVerifyCode(FlashSaleUser user, long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(FlashSaleKey.getFlashSaleVerifyKey, user.getId() + "," + goodsId, rnd);
        //输出图片
        return image;
    }

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = "" + num1 + op1 + num2 + op2 + num3;
        return exp;

    }

    public boolean checkVerifyCode(FlashSaleUser flashSaleUser, long goodsId, int verifyCode) {
        if (flashSaleUser == null || goodsId <= 0) {
            return false;
        }
        Integer old = redisService.get(FlashSaleKey.getFlashSaleVerifyKey, flashSaleUser.getId() + "," + goodsId, int.class);
        if(old==null||old-verifyCode!=0){
            return false;
        }
        redisService.delete(FlashSaleKey.getFlashSaleVerifyKey, flashSaleUser.getId() + "," + goodsId);
        return true;
    }
}