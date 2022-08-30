package com.jica.petner_yuji.model;

public class Animal {

    private String happenPlace; // 발견 장소
    private String kindCd; // 품종
    private String specialMark; // 특징
    private String careNm; // 보호소 이름
    private String careAddr; // 보호소 주소
    private String careTel; // 보호소 연락처
    private String popfile; // 이미지 링크
    private String desertionNo; // 유기ㅁ번호

    public Animal() {

    }

    public Animal(String happenPlace, String kindCd, String specialMark, String careNm, String careAddr, String careTel, String popfile) {
        this.happenPlace = happenPlace;
        this.kindCd = kindCd;
        this.specialMark = specialMark;
        this.careNm = careNm;
        this.careAddr = careAddr;
        this.careTel = careTel;
        this.popfile = popfile;
    }

    public String getDesertionNo() {
        return desertionNo;
    }

    public void setDesertionNo(String desertionNo) {
        this.desertionNo = desertionNo;
    }

    public String getCareAddr() {
        return careAddr;
    }

    public void setCareAddr(String careAddr) {
        this.careAddr = careAddr;
    }

    public String getHappenPlace() {
        return happenPlace;
    }

    public void setHappenPlace(String happenPlace) {
        this.happenPlace = happenPlace;
    }

    public String getKindCd() {
        return kindCd;
    }

    public void setKindCd(String kindCd) {
        this.kindCd = kindCd;
    }

    public String getSpecialMark() {
        return specialMark;
    }

    public void setSpecialMark(String specialMark) {
        this.specialMark = specialMark;
    }

    public String getCareNm() {
        return careNm;
    }

    public void setCareNm(String careNm) {
        this.careNm = careNm;
    }

    public String getCareTel() {
        return careTel;
    }

    public void setCareTel(String careTel) {
        this.careTel = careTel;
    }

    public String getPopfile() {
        return popfile;
    }

    public void setPopfile(String popfile) {
        this.popfile = popfile;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "desertionNo='" + desertionNo + '\'' +
                ", happenPlace='" + happenPlace + '\'' +
                ", kindCd='" + kindCd + '\'' +
                ", specialMark='" + specialMark + '\'' +
                ", careNm='" + careNm + '\'' +
                ", careAddr='" + careAddr + '\'' +
                ", careTel='" + careTel + '\'' +
                ", popfile='" + popfile + '\'' +
                ", desertionNo='" + desertionNo + '\'' +
                '}';
    }
}
