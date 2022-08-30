package com.jica.petner_yuji.model;

import java.util.HashMap;
import java.util.Map;

public class Post {
    private String userID;
    private String title;
    private String img;
    private String imgname;
    private String content;
    private String date;
    private String userName;

    private String num;

    public Post() {

    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Post(String userID, String title, String img, String imgname, String content, String date, String userName) {
        this.userID = userID;
        this.title = title;
        this.img = img;
        this.imgname = imgname;
        this.content = content;
        this.date = date;
        this.userName = userName;
    }

    public Post(String userID, String title, String img, String imgname, String content) {
        this.userID = userID;
        this.title = title;
        this.img = img;
        this.imgname = imgname;
        this.content = content;
    }

    public Post(String userID, String title, String img, String imgname, String content, String date) {
        this.userID = userID;
        this.title = title;
        this.img = img;
        this.imgname = imgname;
        this.content = content;
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImgname() {
        return imgname;
    }

    public void setImgname(String imgname) {
        this.imgname = imgname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "PostModel{" +
                "userID='" + userID + '\'' +
                ", title='" + title + '\'' +
                ", img='" + img + '\'' +
                ", imgname='" + imgname + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", userName='" + userName + '\'' +
                ", num='" + num + '\'' +
                '}' + "\n";
    }
}
