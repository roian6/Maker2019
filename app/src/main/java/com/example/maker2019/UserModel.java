package com.example.maker2019;

public class UserModel {

    //유저 정보를 담을 양식 클래스

    //유저 정보 저장에 필요한 변수 선언
    private String email, userkey;

    public UserModel(){}

    public UserModel(String email, String userkey){ //클래스 양식 지정
        this.email = email;
        this.userkey = userkey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }






}
