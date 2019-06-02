package com.huang.springboot.service;

import com.huang.springboot.dao.FlashSaleUserDao;
import com.huang.springboot.domain.FlashSaleUser;
import com.huang.springboot.exception.GlobalException;
import com.huang.springboot.redis.FlashSaleUserKey;
import com.huang.springboot.redis.RedisService;
import com.huang.springboot.result.CodeMsg;
import com.huang.springboot.util.MD5Util;
import com.huang.springboot.util.UUIDUtil;
import com.huang.springboot.vo.LoginVo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class  FlashSaleUserService{

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    FlashSaleUserDao flashSaleUserDao;

    @Autowired
    RedisService redisService;

    public FlashSaleUser getById(long id){
        //先去缓存尝试取值
        FlashSaleUser flashSaleUser = redisService.get(FlashSaleUserKey.getById,""+id,FlashSaleUser.class);
        if(flashSaleUser!=null){
            return flashSaleUser;
        }
        //缓存取不到在去数据库拿
        flashSaleUser =  flashSaleUserDao.getById(id);
        if(flashSaleUser!=null){
            redisService.set(FlashSaleUserKey.getById, ""+id, flashSaleUser);
        }
        return flashSaleUser;
    }

    public boolean updatePassword(String token, long id, String newPwd){
        //尝试获取user
        FlashSaleUser flashSaleUser = getById(id);
        if(flashSaleUser==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //生产一个新的对象，更新数据库的内容
        FlashSaleUser updatedUser = new FlashSaleUser();
        updatedUser.setId(id);
        updatedUser.setPassword(MD5Util.formPassToDBPass(newPwd,flashSaleUser.getSalt()));
        flashSaleUserDao.update(updatedUser);
        //更新缓存内容
        redisService.delete(FlashSaleUserKey.getById,""+id);
        flashSaleUser.setPassword(updatedUser.getPassword());
        redisService.set(FlashSaleUserKey.token,token,flashSaleUser);
        return true;

    }


    public boolean login(HttpServletResponse httpServletResponse,LoginVo loginVo) {
        if(loginVo==null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        //获得用户传输进来的账号密码
        String mobile = loginVo.getMobile();
        String formPwd = loginVo.getPassword();
        FlashSaleUser flashSaleUser = getById(Long.parseLong(mobile));
        //判断手机号是否存在
        if(flashSaleUser==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //从数据库取出密码
        String DBpwd = flashSaleUser.getPassword();
        //从数据库取出二次加密的盐
        String saltDB = flashSaleUser.getSalt();
        //判断经过一次加盐加密的用户输入密码再次加盐加密之后是否等于数据库密码
        String calPwd=MD5Util.formPassToDBPass(formPwd,saltDB);
        if(!calPwd.equals(DBpwd)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成并添加cookie
        String token = UUIDUtil.uudi();
        addCookie(httpServletResponse,flashSaleUser,token);
        return true;
    }

    public FlashSaleUser getByToken(HttpServletResponse httpServletResponse, String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        FlashSaleUser flashSaleUser = redisService.get(FlashSaleUserKey.token,token,FlashSaleUser.class);
        if(flashSaleUser != null){
            //生成并添加cookie
            addCookie(httpServletResponse,flashSaleUser,token);
        }

       return flashSaleUser;
    }

    public void addCookie(HttpServletResponse httpServletResponse,FlashSaleUser flashSaleUser,String token){
        //生成cookie
        redisService.set(FlashSaleUserKey.token,token,flashSaleUser);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(FlashSaleUserKey.token.expireSeconds());
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);

    }
}
