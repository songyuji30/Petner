package com.jica.petner_yuji.model;

public class MyPageGallery {
    private String img;

    public MyPageGallery(String img) {
        this.img = img;
    }

    public MyPageGallery() {

    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "List_img{" +
                "img='" + img + '\'' +
                '}';
    }
}
