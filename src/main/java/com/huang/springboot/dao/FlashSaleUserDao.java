package com.huang.springboot.dao;

import com.huang.springboot.domain.FlashSaleUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FlashSaleUserDao {

    @Select("select * from flash_sale_user where id = #{id}")
    public FlashSaleUser getById(@Param("id") long id);

    @Update("update flash_sale_user set password = #{password} where id = #{id}")
    void update(FlashSaleUser updatedUser);
}
