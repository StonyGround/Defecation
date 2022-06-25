package com.kjy.care.bean;

import android.bluetooth.BluetoothDevice;


public class DeviceInfo {

    private BluetoothDevice device;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    private String id;
    private String name;

    private String mac;
    private int type;

    public DeviceInfo(){}

    public DeviceInfo(String id, String name, int type){

        this.id =id;
        this.name=name;
        this.type=type;

    }



    public DeviceInfo(String id, String name, int type,String mac){

        this.id =id;
        this.name=name;
        this.type=type;
       this.mac = mac;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    private int rssi = 999;

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
