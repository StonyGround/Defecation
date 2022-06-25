package com.kjy.care.util;



import java.lang.reflect.Field;


import org.json.JSONObject;


public class SituJsonObject extends JSONObject {

    public SituJsonObject(String jsonString) throws Exception{

        super(jsonString);

    }


    /**

     * 将一个json字符串转换成一个java类的实例

     * @param cls

     * @return

     * @throws Exception

     */

    public Object getBean(Class cls) throws Exception

    {

//生成待输出的实例

        Object obj = cls.newInstance();

//得到所有的成员变量

        Field[] fs = cls.getDeclaredFields();

//对每个成员变量，从json字符串中取值，并且放到实例中

        for (int i = 0; i < fs.length; i++) {

            String name = fs[i].getName();

            fs[i].setAccessible(true);//防止私有成员不能访问

//从json中获取这个属性所对应的值

            Object value = this.get(name);

            if(value==null)continue;

//把当前的成员变量值放到实例中

            fs[i].set(obj, value);

        }

        return obj;

    }

}
