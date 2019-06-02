package com.huang.springboot.controller;

import com.huang.springboot.access.AccessLimit;
import com.huang.springboot.domain.FlashSaleUser;
import com.huang.springboot.rabbimq.FlashSaleMessage;
import com.huang.springboot.rabbimq.MQSender;
import com.huang.springboot.redis.GoodSKey;
import com.huang.springboot.redis.RedisService;
import com.huang.springboot.result.CodeMsg;
import com.huang.springboot.result.Result;
import com.huang.springboot.service.FlashSaleService;
import com.huang.springboot.service.FlashSaleUserService;
import com.huang.springboot.service.GoodsService;
import com.huang.springboot.service.OrderService;
import com.huang.springboot.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class FlashSaleController implements InitializingBean {
    private Map<Long,Boolean> localIsOverMap = new HashMap<>();

    @Autowired
    FlashSaleUserService flashSaleUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    FlashSaleService flashSaleService;

    @Autowired
    MQSender sender;
    //系统初始化
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList==null){
            return;
        }
        for(GoodsVo goods : goodsList){
            redisService.set(GoodSKey.getFlashSaleGoodsStock,""+goods.getId(),goods.getStockCount());
            localIsOverMap.put(goods.getId(),false);
        }
    }

    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model,FlashSaleUser flashSaleUser,
                                        @RequestParam("goodsId")long goodsId){
        model.addAttribute("user",flashSaleUser);
        if(flashSaleUser==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = flashSaleService.getFlashSaleResult(flashSaleUser.getId(),goodsId);
        return Result.success(result);
    }

    @RequestMapping(value = "/{path}/do_flash_sale", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> do_flash_sale(FlashSaleUser flashSaleUser, Model model,
                       @RequestParam("goodsId") long goodsId,
                       @PathVariable("path") String path) {
        //首先判断用户是否登录
        model.addAttribute("flashSaleUser",flashSaleUser);
        if(flashSaleUser==null){
            return Result.error(CodeMsg.NOT_LOGIN_ERROR);
        }
        //验证path
        boolean isCorrectPath = flashSaleService.checkPath(flashSaleUser,goodsId,path);
        if(!isCorrectPath){
            return Result.error(CodeMsg.REQUEST_ERROR);
        }
        //利用本地内存判断是否库存不足，减少访问redis
        boolean isOver = localIsOverMap.get(goodsId);
        if(isOver){
            return Result.error(CodeMsg.STOCK_EMPTY);
        }
        //先在缓存尝试减少库存，如果缓存库存不足，就直接返回不进行后续操作
        long stock = redisService.decr(GoodSKey.getFlashSaleGoodsStock,""+goodsId);
        if(stock<=0){
            localIsOverMap.put(goodsId,true);
            return Result.error(CodeMsg.STOCK_EMPTY);
        }
        //如果缓存还有剩余，就发送请求秒杀的消息到消息队列，尝试进行秒杀
        FlashSaleMessage message = new FlashSaleMessage();
        message.setUser(flashSaleUser);
        message.setGoodsId(goodsId);
        sender.sendFlashSaleMessage(message);
        //返回0表示正在排队秒杀中
        return Result.success(0);

    }

    @AccessLimit(seconds = 5,maxCount = 10,needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getFlashSalePath(HttpServletRequest request,FlashSaleUser flashSaleUser, Model model,
                                           @RequestParam("goodsId") long goodsId,
                                           @RequestParam(value = "verifyCode") int verifyCode) {
            model.addAttribute("user",flashSaleUser);
            //检查验证码是否输入成功
            boolean checkVerifyCode = flashSaleService.checkVerifyCode(flashSaleUser,goodsId,verifyCode);
            if(!checkVerifyCode){
                return Result.error(CodeMsg.VERIFY_CODE_ERROR);
            }
            //生成访问路径
            String path = flashSaleService.createFlashSalePath(flashSaleUser,goodsId);
            System.out.println(path);
            return Result.success(path);
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> verify(HttpServletResponse response, FlashSaleUser flashSaleUser, Model model,
                                 @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user",flashSaleUser);
        if(flashSaleUser==null||goodsId<=0){
            return null;
        }
        try {
            BufferedImage image  = flashSaleService.createVerifyCode(flashSaleUser, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.GENERATE_VERIFY_CODE_ERROR);
        }
    }



//        //判断库存
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//        int stock = goods.getStockCount();
//        if(stock<=0){
//            model.addAttribute("errmsg", CodeMsg.STOCK_EMPTY);
//            return "flash_fail";
//        }
        //查看是否已经有订单，没有的话尝试添加订单

//        FlashSaleOrder order = orderService.getFlashSaleOrderByUserIdGoodsId(
//                flashSaleUser.getId(),goodsId );
//        if(order != null){
//            model.addAttribute("errmsg",CodeMsg.REPEAT_ERROR);
//
//        }
//
//        OrderInfo orderInfo = flashSaleService.seckill(flashSaleUser,goods);
//
//        model.addAttribute("orderInfo",orderInfo);
//        model.addAttribute("goods",goods);
//        return "order_detail";



}
