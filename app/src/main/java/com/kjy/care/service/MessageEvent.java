package com.kjy.care.service;

public enum MessageEvent {

    WifiConnect(1,"wifi已连接"),
    WifiDisConnect(2,"wifi已断开"),
    CheckWifi(99,"wifi状态"),
    VersionUpdate(3,"升级"),
    Location(4,"定位"),
    Hour(5,"时"),
    Minite(6,"分"),
    Second(7,"秒"),
    Blue_on(8,"蓝牙开"),
    Blue_off(9,"蓝牙关"),
    Send_data(98,"发送串口数据"),
    Com_data(99,"串口数据"),
    Com_open_ok(100,"串口打开成功"),
    Com_open_error(101,"串口打开失败");
    MessageEvent(int type, String name){
      this.type = type;
      this.name= name;
  }

    MessageEvent(int type, String name, Object data){
        this.type = type;
        this.name= name;
        this.data = data;
    }


    private int type;
    private String name;

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
