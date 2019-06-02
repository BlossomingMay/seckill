package com.huang.springboot.result;

import lombok.Getter;

@Getter
public class CodeMsg {
    private int code;
    private String msg;

    private CodeMsg(int code,String msg){
        this.code=code;
        this.msg=msg;
    }
    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0,"success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101,"参数校验异常 %s");
    public static CodeMsg REQUEST_ERROR = new CodeMsg(500102,"请求非法");

    //5002X代表登录异常
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210,"Session不存在或已经失效");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211,"登录密码不能为空");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212,"手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213,"手机号码格式错误");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214,"手机号码不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215,"输入的密码有误");

    //5004X代表订单异常
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400,"订单不存在");

    //5005X代表秒杀异常
    public static CodeMsg STOCK_EMPTY = new CodeMsg(500500,"商品售空");
    public static CodeMsg REPEAT_ERROR = new CodeMsg(500501,"不能重复下单");
    public static CodeMsg NOT_LOGIN_ERROR = new CodeMsg(500502,"用户未登录");
    public static CodeMsg VERIFY_CODE_ERROR = new CodeMsg(500503,"验证码错误");
    public static CodeMsg GENERATE_VERIFY_CODE_ERROR= new CodeMsg(500504,"服务器生成验证码繁忙");
    public static CodeMsg SUBMIT_TOO_MUCH= new CodeMsg(500505,"提交过于频繁");

    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String message = String.format(this.msg,args);
        return new CodeMsg(code,message);
    }
}
