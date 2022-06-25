package com.kjy.care.util;

public class StringUtil {
   public static boolean  isEmpty(String value){

       if(value == null || value.trim().length()==0){

           return true;
       }


       return false;


   }
}
