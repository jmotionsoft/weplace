package com.jmotionsoft.towntalk.util;

import java.util.regex.Pattern;

public class Validate {

    public static boolean isEmail(String value, boolean isEmpty){
        Pattern pattern = Pattern.compile("(\\w+\\.)*\\w+@(\\w+\\.)+[A-Za-z]+");
        return validator(pattern, value, isEmpty);
    }

    public static boolean isPassword(String value, boolean isEmpty){
        Pattern pattern = Pattern.compile("^.{8,}$");
        return validator(pattern, value, isEmpty);
    }

    public static boolean isNotEmpty(String value){
        if(value == null) return false;

        value = value.trim();
        if(value.equals("")) return false;
        else return true;
    }

    private static boolean validator(Pattern pattern, String value, boolean isEmpty){
        if(value == null) return false;

        value = value.trim();
        if(isEmpty && value.equals("")) return true;

        if(pattern.matcher(value).find()) return true;
        else return false;
    }
}
