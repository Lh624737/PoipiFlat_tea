package com.pospi.dto;

import java.io.Serializable;

/**
 * Created by Qiyan on 2016/5/11.
 */
public class GoodsDto implements Serializable {
    /**
     * Sid : 04e1da31a4784c938942f9195cc72e40
     * UId : f17851cbccb84ce0b55ac6f464d9e13f
     * category_sid : 81d7a171f123412e8aef109cdb76ba9a
     * colorCode : 0xFF646464
     * group_sid : null
     * image : null
     * name : test
     * price : 0.0
     * CostPrice : 0.0
     * unit : 个
     * mainPrinterSid : null
     * backPrinterSid : null
     * Code : null
     * createTime : 2015-08-31T15:08:02
     * orderBy : 9
     * isHide : false
     * IsDel : false
     * Code_bm :
     * specification : null
     * valuationType : 0
     * genre : 0
     * setFlag : false
     * setPids : null
     * setOldPrice : null
     * colorCodeShow : #646464
     */
    private double num;//添加商品的数量
    private double discount;
    private String Sid;
    private String UId;
    private String category_sid;
    private String colorCode;
    private String group_sid;
    private String image;
    private String name;
    private double price;
    private double CostPrice;
    private String unit;
    private String mainPrinterSid;
    private String backPrinterSid;
    private String Code;
    private String createTime;
    private int orderBy;
    private boolean isHide;
    private boolean IsDel;
    private String Code_bm;
    private String specification;//单位
    private int valuationType;
    private int genre;
    private boolean setFlag;
    private String setPids;
    private String setOldPrice;
    private String colorCodeShow;
    private String modified;
    private String saleType="";//销售类型
    private String poptype="";//促销类型
    private String popid="";//促销id
    private String iszp ="0";//是否赠品
    private String ispop = "0";
    private double oldPrice = 0;

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getIspop() {
        return ispop;
    }

    public void setIspop(String ispop) {
        this.ispop = ispop;
    }

    public String getPoptype() {
        return poptype;
    }

    public String getIszp() {
        return iszp;
    }

    public void setIszp(String iszp) {
        this.iszp = iszp;
    }

    public void setPoptype(String poptype) {
        this.poptype = poptype;
    }

    public String getPopid() {
        return popid;
    }

    public void setPopid(String popid) {
        this.popid = popid;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    //订单单个商品的sid
    private String goodsId;
    private double popPrice;

    public double getPopPrice() {
        return popPrice;
    }

    public void setPopPrice(double popPrice) {
        this.popPrice = popPrice;
    }

    private String goods_id;
    private String goods_num;
    private String goods_name;
    private String goods_spec;
    private String goods_jjfs;
    private String goods_lsj;
    private String goods_catid;

    public String getGoods_catid() {
        return goods_catid;
    }

    public void setGoods_catid(String goods_catid) {
        this.goods_catid = goods_catid;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_num() {
        return goods_num;
    }

    public void setGoods_num(String goods_num) {
        this.goods_num = goods_num;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getGoods_spec() {
        return goods_spec;
    }

    public void setGoods_spec(String goods_spec) {
        this.goods_spec = goods_spec;
    }

    public String getGoods_jjfs() {
        return goods_jjfs;
    }

    public void setGoods_jjfs(String goods_jjfs) {
        this.goods_jjfs = goods_jjfs;
    }

    public String getGoods_lsj() {
        return goods_lsj;
    }

    public void setGoods_lsj(String goods_lsj) {
        this.goods_lsj = goods_lsj;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public double getNum() {
        return num;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getSid() {
        return Sid;
    }

    public void setSid(String Sid) {
        this.Sid = Sid;
    }

    public String getUId() {
        return UId;
    }

    public void setUId(String UId) {
        this.UId = UId;
    }

    public String getCategory_sid() {
        return category_sid;
    }

    public void setCategory_sid(String category_sid) {
        this.category_sid = category_sid;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getGroup_sid() {
        return group_sid;
    }

    public void setGroup_sid(String group_sid) {
        this.group_sid = group_sid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCostPrice() {
        return CostPrice;
    }

    public void setCostPrice(double CostPrice) {
        this.CostPrice = CostPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMainPrinterSid() {
        return mainPrinterSid;
    }

    public void setMainPrinterSid(String mainPrinterSid) {
        this.mainPrinterSid = mainPrinterSid;
    }

    public String getBackPrinterSid() {
        return backPrinterSid;
    }

    public void setBackPrinterSid(String backPrinterSid) {
        this.backPrinterSid = backPrinterSid;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(int orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isIsHide() {
        return isHide;
    }

    public void setIsHide(boolean isHide) {
        this.isHide = isHide;
    }

    public boolean isIsDel() {
        return IsDel;
    }

    public void setIsDel(boolean IsDel) {
        this.IsDel = IsDel;
    }

    public String getCode_bm() {
        return Code_bm;
    }

    public void setCode_bm(String Code_bm) {
        this.Code_bm = Code_bm;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public int getValuationType() {
        return valuationType;
    }

    public void setValuationType(int valuationType) {
        this.valuationType = valuationType;
    }

    public int getGenre() {
        return genre;
    }

    public void setGenre(int genre) {
        this.genre = genre;
    }

    public boolean isSetFlag() {
        return setFlag;
    }

    public void setSetFlag(boolean setFlag) {
        this.setFlag = setFlag;
    }

    public String getSetPids() {
        return setPids;
    }

    public void setSetPids(String setPids) {
        this.setPids = setPids;
    }

    public String getSetOldPrice() {
        return setOldPrice;
    }

    public void setSetOldPrice(String setOldPrice) {
        this.setOldPrice = setOldPrice;
    }

    public String getColorCodeShow() {
        return colorCodeShow;
    }

    public void setColorCodeShow(String colorCodeShow) {
        this.colorCodeShow = colorCodeShow;
    }
}
