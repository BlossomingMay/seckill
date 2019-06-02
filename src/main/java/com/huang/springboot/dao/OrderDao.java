package com.huang.springboot.dao;

import com.huang.springboot.domain.FlashSaleOrder;
import com.huang.springboot.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {
    @Select("select * from flash_sale_order where user_id = #{userId} and goods_id = #{goodsId}")
     FlashSaleOrder getFlashSaleOrderByUserIdGoodsId(@Param("userId") long userId,@Param("goodsId") long goodsId);

    @Insert("insert into order_info(user_id,goods_id, goods_name, goods_count, goods_price, order_channel,status,create_date)" +
            "values(#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate})")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    long insert(OrderInfo orderInfo);

    @Insert("insert into flash_sale_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    void insertFlashSaleOrder(FlashSaleOrder order);

    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);
}
