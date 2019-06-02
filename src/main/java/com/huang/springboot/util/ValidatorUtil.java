package com.huang.springboot.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    public static final Pattern mobile_pattren = Pattern.compile("1\\d{10}");
    public static boolean isMobile(String src){
        if(StringUtils.isEmpty(src)){
            return false;
        }
        Matcher m = mobile_pattren.matcher(src);
        return m.matches();
    }
    public  static void main(String[] args){
        System.out.println(isMobile("15912341234"));
        System.out.println(isMobile("2341"));

    }
}
