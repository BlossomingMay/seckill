package com.huang.springboot.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    private static final String salt = "1a2b3c4d";

    public static String inputPwdToDBPwd(String input,String saltDB){
        String formPwd = inputPassToFromPass(input);
        String dbPwd = formPassToDBPass(formPwd,saltDB);
        return dbPwd;
    }

    public static String inputPassToFromPass(String inputPwd){
        String str = "" + salt.charAt(0)+salt.charAt(2)+inputPwd+salt.charAt(5)+salt.charAt(4);
        return md5(str);

    }
    //数据从HTML传过来经过了一次加盐加密，现在在进行一次加盐加密，盐本来应该随机生成并存储到用户数据库的
    public static String formPassToDBPass(String inputPwd,String salt){
        String str = "" + salt.charAt(0)+salt.charAt(2)+ inputPwd +salt.charAt(5)+salt.charAt(4);
        return md5(str);

    }

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    public static void main(String[] args){

        System.out.println(inputPwdToDBPwd("123456","1a2b3c4d"));
    }
}
