package com.kjy.care.util;

import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by 420041900 on 2017/3/17.
 */

public class LinkedTreeMapUtil {


    /**
     * 将 http 的 body  转 Map
     * @param value
     * @return
     */
     public static JSONObject LinkedTreeMapToJSONObject(Object value){

         JSONObject json=null;
         try{
             //存放类

               json=new JSONObject();




             LinkedTreeMap linked=(LinkedTreeMap)value;
             for (Iterator iter = linked.keySet().iterator(); iter.hasNext();) {
                 String key = (String) iter.next();
                 json.put(key, linked.get(key));
             }



         }catch (Exception e){

             LogH.e("E",e.getMessage());

         }

         return json;
     }


    /**
     * 判断Code 是否1
     * @param map
     * @return
     */
     public static boolean CodeOk(JSONObject map) throws  Exception{
         if (map != null&&map.has ("code")) {

             if(map.getString("code").equals("0")){
                 return true;
             }else {
                 return false;
             }
         }else{

             return false;
         }


     }








}
