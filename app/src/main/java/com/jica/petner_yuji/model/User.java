package com.jica.petner_yuji.model;


public class User {

    // 사용자 기본정보
    private String userName; // 사용자 이름(닉네임)
    private String userID; // 사용자 아이디
    private String userPass; // 사용자 비밀번호

    public User(String userName, String userID, String userPass) {
        this.userName = userName;
        this.userID = userID;
        this.userPass = userPass;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
}