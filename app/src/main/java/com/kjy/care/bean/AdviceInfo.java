package com.kjy.care.bean;

public class AdviceInfo {

    private String id;
    private String name;
    private String type;
    private String price;
    private String time;
    public AdviceInfo(){}

    public AdviceInfo(String id,String name,String type,String price,String time){

        this.id =id;
        this.name=name;
        this.type=type;
        this.price=price;
        this.time=time;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
