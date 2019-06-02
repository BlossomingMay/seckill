package com.huang.springboot.access;

import com.alibaba.fastjson.JSON;
import com.huang.springboot.domain.FlashSaleUser;
import com.huang.springboot.redis.AccessKey;
import com.huang.springboot.redis.RedisService;
import com.huang.springboot.result.CodeMsg;
import com.huang.springboot.service.FlashSaleUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;


@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    FlashSaleUserService flashSaleUserService;
    @Autowired
    RedisService redisService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            //尝试获取用户
            FlashSaleUser flashSaleUser = getUser(request, response);
            //把用户存在ThreadLocal
            UserContext.setUser(flashSaleUser);

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if(needLogin){
                if(flashSaleUser==null){
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
            }
            key = flashSaleUser.getId()+"_"+key;
            AccessKey accessKey = AccessKey.getAccessKey(seconds);
            Integer accessCount = redisService.get(accessKey,key,Integer.class);
            if(accessCount==null){
                redisService.set(accessKey,key,0);
            }else if(accessCount<3){
                redisService.incr(accessKey,key);
            }else{
                render(response, CodeMsg.SUBMIT_TOO_MUCH);
                return false;
            }
        }
        return true;

    }

    private void render(HttpServletResponse response, CodeMsg codeMsg) throws Exception{
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(codeMsg);
        out.write(str.getBytes("UTF-8"));
    }

    private FlashSaleUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(FlashSaleUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, FlashSaleUserService.COOKIE_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return flashSaleUserService.getByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request, String cookiName) {
        Cookie[]  cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(cookiName)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}