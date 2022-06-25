package com.kjy.care.bean;

public class User {

    private String id="";
    private String brithday="1990-01-01";
    private String height="165";
    private String sex="男";

    public  User(String id,String brithday,String height,String sex){
        this.id = id;
        this.brithday=brithday;
        this.height=height;
        this.sex=sex;



    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrithday() {
        return brithday;
    }

    public void setBrithday(String brithday) {
        this.brithday = brithday;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
