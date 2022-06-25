package com.kjy.care.bean;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;


public class RecordList implements Serializable {


    public int id;
    public String t_uid;
    public String t_content;
    public String t_time;
    public RecordList(){}


    public RecordList(int id,String t_uid,String t_content,String t_time){
        this.id = id;
        this.t_uid = t_uid;
        this.t_content =t_content;
        this.t_time = t_time;
    }

}
