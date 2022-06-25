package com.kjy.care.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库连接
 */
public class MySqlUtil extends SQLiteOpenHelper {

    private static final String DB_NAME = "defecation.db"; // 数据库
    private static final int version = 1; // 版本

    private SQLiteDatabase mysql=null;


    public MySqlUtil(Context context) {


        super(context, DB_NAME, null, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        /******************************组表*********************/

        String DataList = "CREATE TABLE if not exists  DataList (";
        DataList += " id integer primary key autoincrement,";//UNIQUE
        DataList += " t_uid  varchar(32) NOT NULL,";//用户id
        DataList += " t_type int(32) NOT NULL  ,"; // 1 小便 2 大便
        DataList += " t_status int(1) DEFAULT '0',"; // 0 有效 1 无效
        DataList += " t_time datetime DEFAULT (datetime('now','localtime'))";
        DataList += " ) ";
        db.execSQL(DataList);
        Log.e("MYSQL","TABLE DataList CREATE SUCCESS");


        String RecordList = "CREATE TABLE if not exists  RecordList (";
        RecordList += " id integer primary key autoincrement,";//UNIQUE
        RecordList += " t_uid  varchar(32) NOT NULL,";//用户id
        RecordList += " t_content varchar(250) NOT NULL  ,"; // 内容
        RecordList += " t_time datetime DEFAULT (datetime('now','localtime'))";
        RecordList += " ) ";
        db.execSQL(RecordList);
        Log.e("MYSQL","TABLE RecordList CREATE SUCCESS");


        mysql=db;
    }
    public Cursor query(String sql, String[] values){

        if(mysql==null){mysql=this.getWritableDatabase();}
        Cursor cursor =  mysql.rawQuery(sql,values);


        return cursor;
    }


    public void exec(String sql, Object[] values){
        try {
            if (mysql == null) {
                mysql = this.getWritableDatabase();
            }
            mysql.execSQL(sql, values);
        }catch (Exception e){

            Log.e("sql","sql:"+sql+"exec:"+e.getMessage());
            throw  e;

        }
    }




    public void close(){

        if(mysql!=null) {
            mysql.close();
            mysql=null;
        }
    }






    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        if (newVersion == oldVersion) {
            return;
        }


        db.beginTransaction();
        try {
            if (newVersion > oldVersion) {

                //当新版本高于老版本时，在这里更新表 。如果第3次需要修改数据库时，记得删除上次的修改语句。
              //  db.execSQL("ALTER TABLE Attention ADD userCode TEXT");
                 onCreate(db);
                Log.e("数据库","创建数据库,newVersion:"+newVersion+",===,oldVersion:"+oldVersion);
            }


            if(oldVersion<=2){
                //
               //db.execSQL("ALTER TABLE DeviceData ADD column  runCount integer");
              //  db.execSQL("ALTER TABLE DeviceData ADD column  runEnd integer");
                Log.e("数据库","修改数据库,newVersion:"+newVersion+",===,oldVersion:"+oldVersion);

            }






            db.setTransactionSuccessful();
        } catch (Throwable ex) {

        } finally {
            db.endTransaction();
        }


    }



}