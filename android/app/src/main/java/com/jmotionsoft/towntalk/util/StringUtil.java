package com.jmotionsoft.towntalk.util;

/**
 * Created by sin31 on 2016-12-09.
 */

public class StringUtil {
    public static String checkNull(String value, String changeString){
        if(value == null || value.trim().toLowerCase().equals("null")){
            return changeString;
        }else{
            return value;
        }
    }
}
