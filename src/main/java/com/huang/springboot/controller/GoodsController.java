package com.huang.springboot.controller;

import com.huang.springboot.domain.FlashSaleUser;
import com.huang.springboot.redis.GoodSKey;
import com.huang.springboot.redis.RedisService;
import com.huang.springboot.result.Result;
import com.huang.springboot.service.FlashSaleUserService;
import com.huang.springboot.service.GoodsService;
import com.huang.springboot.vo.GoodsDetailVo;
import com.huang.springboot.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
    @RequestMapping("/goods")
    public class GoodsController {

        @Autowired
        FlashSaleUserService flashSaleUserService;

        @Autowired
        RedisService redisService;

        @Autowired
        GoodsService goodsService;

        @Autowired
        ThymeleafViewResolver thymeleafViewResolver;

        @Autowired
        ApplicationContext applicationContext;

    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String list(FlashSaleUser flashSaleUser, Model model, HttpServletResponse response, HttpServletRequest request) {
        model.addAttribute("flashSaleUser",flashSaleUser);
        //判断缓存中是否已经有页面
        String html = redisService.get(GoodSKey.getGoodsList,"",String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList",goodsList);
        //自己渲染页面存入缓存，而不是经过Spring
        // return "goods_list";

        //如果没有，就开始构建静态页面
        IWebContext ctx = new WebContext(request,response,request.getServletContext(),request.getLocale()
                ,model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodSKey.getGoodsList,"",html);
        }
        return html;
        }

    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(FlashSaleUser flashSaleUser, Model model,
                                        @PathVariable("goodsId") long goodsId,
                                        HttpServletResponse response, HttpServletRequest request) {

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt =  goods.getStartDate().getTime();
        long endAt =  goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int flashSaleStatus = 0;
        int remainSeconds = 0;
        //判断秒杀是否开始
        if(now < startAt){
            flashSaleStatus = 0;
            remainSeconds = (int)(startAt - now)/1000;
        }else if(now >endAt){
            flashSaleStatus = 2;
            remainSeconds = -1;
        }else{
            flashSaleStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoods(goods);
        goodsDetailVo.setFlashSaleUser(flashSaleUser);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setFlashSaleStatus(flashSaleStatus);
        System.out.println(goodsDetailVo.getRemainSeconds());

        return Result.success(goodsDetailVo);
    }
}
