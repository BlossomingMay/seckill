package com.huang.springboot.dao;

import com.huang.springboot.domain.FlashSaleGoods;
import com.huang.springboot.domain.Goods;
import com.huang.springboot.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*,fs.stock_count,fs.start_date,fs.end_date,fs.flash_sale_price from flash_sale_goods fs left join goods g on fs.goods_id = g.id")
    public List<GoodsVo> listGoodsVo ();

    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.flash_sale_price from flash_sale_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId")long goodsId);

    @Update("update flash_sale_goods set stock_count = stock_count -1 where goods_id = #{goodsId}")
    public int reduceStock(FlashSaleGoods g);
}
