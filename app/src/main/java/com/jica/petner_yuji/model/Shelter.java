package com.jica.petner_yuji.model;

public class Shelter {
    private String careNm;
    private String orgNm;
    private String careAddr;
    private String careTel;
    private String divisionNm;

    public Shelter(){

    }

    public Shelter(String careNm, String orgNm, String careAddr, String careTel, String divisionNm) {
        this.careNm = careNm;
        this.orgNm = orgNm;
        this.careAddr = careAddr;
        this.careTel = careTel;
        this.divisionNm = divisionNm;
    }

    @Override
    public String toString() {
        return "Shelter{" +
                "careNm='" + careNm + '\'' +
                ", orgNm='" + orgNm + '\'' +
                ", careAddr='" + careAddr + '\'' +
                ", careTel='" + careTel + '\'' +
                ", divisionNm='" + divisionNm + '\'' +
                '}';
    }

    public String getCareNm() {
        return careNm;
    }

    public void setCareNm(String careNm) {
        this.careNm = careNm;
    }

    public String getOrgNm() {
        return orgNm;
    }

    public void setOrgNm(String orgNm) {
        this.orgNm = orgNm;
    }

    public String getCareAddr() {
        return careAddr;
    }

    public void setCareAddr(String careAddr) {
        this.careAddr = careAddr;
    }

    public String getCareTel() {
        return careTel;
    }

    public void setCareTel(String careTel) {
        this.careTel = careTel;
    }

    public String getDivisionNm() {
        return divisionNm;
    }

    public void setDivisionNm(String divisionNm) {
        this.divisionNm = divisionNm;
    }
}
