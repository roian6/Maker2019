package com.example.maker2019;

public class MacroModel {

    //명함 정보를 담을 양식 클래스

    //명함 정보 저장에 필요한 변수 선언
    private String userkey, name, phone, num;

    public MacroModel() {
    }

    public MacroModel(String userkey, String name, String phone, String num) { //클래스 양식 지정
        this.userkey = userkey;
        this.name = name;
        this.phone = phone;
        this.num = num;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
