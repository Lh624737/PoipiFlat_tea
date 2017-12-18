package com.pospi.dto;

/**
 * Created by acer on 2017/9/4.
 */

public class GoodsPop {
    private String goodsid;
    private double popsj;
    private String iszp;
    private double popsl;

    public double getPopsl() {
        return popsl;
    }

    public void setPopsl(double popsl) {
        this.popsl = popsl;
    }

    public String getIszp() {
        return iszp;
    }

    public void setIszp(String iszp) {
        this.iszp = iszp;
    }

    public String getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(String goodsid) {
        this.goodsid = goodsid;
    }

    public double getPopsj() {
        return popsj;
    }

    public void setPopsj(double popsj) {
        this.popsj = popsj;
    }
}
