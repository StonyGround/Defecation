package com.kjy.care.util;


import android.database.Cursor;
import android.util.Log;

import com.kjy.care.bean.RecordList;

import java.util.ArrayList;
import java.util.List;

public class MyDataUtil {


    public static void  addRecordList(MySqlUtil mysql,String t_content,  String uid){
        if(mysql!=null){

            String sql="insert into RecordList(t_uid,t_content,t_time) values(?,?,?)";
            Object[] obj=new Object[]{uid,t_content, DateUtil.getStringDate()};
            mysql.exec(sql,obj);
        }
    }


    public static List<RecordList> listRecordList(MySqlUtil mysql, String time, String uid){

        List<RecordList> list =new ArrayList();

        if(mysql!=null){

                String sql="select id,t_uid,t_content,t_time from  RecordList where t_uid = ? and date(t_time) = date(?) order by t_time desc ";//
                String[] obj=new String[]{uid, time};

                Cursor cursor= mysql.query(sql,obj);

                if (cursor == null) {
                    return list;
                }


                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor
                            .getColumnIndex("id"));
                    String t_uid = cursor.getString(cursor
                            .getColumnIndex("t_uid"));

                    String t_content = cursor.getString(cursor
                            .getColumnIndex("t_content"));

                    String t_time = cursor.getString(cursor
                            .getColumnIndex("t_time"));


                    RecordList bean = new RecordList(id,t_uid,t_content,t_time);

                    list.add(bean);

                }

        }
        return list;
    }






    public static void insertDataList(MySqlUtil mysql, int type, String uid){
        if(mysql!=null){

            String sql="insert into DataList(t_uid,t_type,t_time) values(?,?,?)";
            Object[] obj=new Object[]{uid,type, DateUtil.getStringDate()};
            mysql.exec(sql,obj);
        }
    }

    public static int countDataList(MySqlUtil mysql, int type, String uid, String time){
        Log.e("大便","==>"+time);
        int n = 0;
        if(mysql!=null){
            String sql="select count(id) as total from  DataList where t_uid = ? and t_type = ? and date(t_time) = date(?)  and t_status=0";//
            String[] obj=new String[]{uid,type+"",time};

            Cursor cursor= mysql.query(sql,obj);

            if (cursor == null) {
                return 0;
            }


            if (cursor.moveToNext()) {

                n = cursor.getInt(cursor
                        .getColumnIndex("total"));
            }




        }

        return n;
    }








    public static void test(MySqlUtil mysql, int type, String uid, String time){


        if(mysql!=null){
            String sql="select t_uid,t_time  from  DataList where t_uid = ? and t_type = ?     ";//strftime('%m-%d','now','localtime') = strftime('%m-%d',表中时间字段)
            String[] obj=new String[]{uid,type+""};

            Cursor cursor= mysql.query(sql,obj);

            if (cursor == null) {
                return ;
            }


            while (cursor.moveToNext()) {
                String t_uid = cursor.getString(cursor
                        .getColumnIndex("t_uid"));

               String n = cursor.getString(cursor
                        .getColumnIndex("t_time"));

               Log.e("时间",t_uid+"===="+n);
            }




        }


    }







    //计数清零
    public static void updateClean(MySqlUtil mysql,String uid){
        if(mysql!=null){

            String sql="update  DataList  set   t_status = 1  where  t_uid=? and date(t_time) = date(?) ";
            Object[] obj=new Object[]{uid, DateUtil.getStringDate()};
            mysql.exec(sql,obj);
        }
    }



}