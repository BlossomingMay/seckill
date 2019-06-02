package com.huang.springboot.service;

import com.huang.springboot.dao.GoodsDao;
import com.huang.springboot.domain.FlashSaleGoods;
import com.huang.springboot.domain.Goods;
import com.huang.springboot.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }


    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        FlashSaleGoods g = new FlashSaleGoods();
        g.setGoodsId(goods.getId());
        int ret =  goodsDao.reduceStock(g);
        return ret>0;
    }
}
