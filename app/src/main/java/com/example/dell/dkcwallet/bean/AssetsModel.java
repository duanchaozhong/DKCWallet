package com.example.dell.dkcwallet.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * @author yiyang
 */
public class AssetsModel implements Parcelable{
    /**
     * total : 5766.8336
     * freeze : 1253.0156
     * typeName : 蒂克币
     * currType : 1
     * currency : 4513.8180
     * convertRmb : 1730050.08
     * netConvertRmb : 1354145.40
     * "transferAddr": "45c6cfa83db7fa68dd4adb8f17f63e4b"
     */

    private String total;
    private String freeze;
    private String typeName;
    private String currTypeAd;
    private int currType;
    private String currency;
    private String convertRmb;
    private String netConvertRmb;

    public String getTransferAddr() {
        return transferAddr;
    }

    public void setTransferAddr(String transferAddr) {
        this.transferAddr = transferAddr;
    }

    private String transferAddr;
    /**
     * totalAssets : 3019642.53
     * netAssets : 2525966.85
     */

    private String totalAssets;
    private String netAssets;

    public String getCurrTypeAd() {
        return currTypeAd;
    }

    public void setCurrTypeAd(String currTypeAd) {
        this.currTypeAd = currTypeAd;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getFreeze() {
        return freeze;
    }

    public void setFreeze(String freeze) {
        this.freeze = freeze;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getCurrType() {
        return currType;
    }

    public void setCurrType(int currType) {
        this.currType = currType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getConvertRmb() {
        return convertRmb;
    }

    public void setConvertRmb(String convertRmb) {
        this.convertRmb = convertRmb;
    }

    public String getNetConvertRmb() {
        return netConvertRmb;
    }

    public void setNetConvertRmb(String netConvertRmb) {
        this.netConvertRmb = netConvertRmb;
    }

    public String getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(String totalAssets) {
        this.totalAssets = totalAssets;
    }

    public String getNetAssets() {
        return netAssets;
    }

    public void setNetAssets(String netAssets) {
        this.netAssets = netAssets;
    }

    public AssetsModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.total);
        dest.writeString(this.freeze);
        dest.writeString(this.typeName);
        dest.writeString(this.currTypeAd);
        dest.writeInt(this.currType);
        dest.writeString(this.currency);
        dest.writeString(this.convertRmb);
        dest.writeString(this.netConvertRmb);
        dest.writeString(this.transferAddr);
        dest.writeString(this.totalAssets);
        dest.writeString(this.netAssets);
    }

    protected AssetsModel(Parcel in) {
        this.total = in.readString();
        this.freeze = in.readString();
        this.typeName = in.readString();
        this.currTypeAd = in.readString();
        this.currType = in.readInt();
        this.currency = in.readString();
        this.convertRmb = in.readString();
        this.netConvertRmb = in.readString();
        this.transferAddr = in.readString();
        this.totalAssets = in.readString();
        this.netAssets = in.readString();
    }

    public static final Creator<AssetsModel> CREATOR = new Creator<AssetsModel>() {
        @Override
        public AssetsModel createFromParcel(Parcel source) {
            return new AssetsModel(source);
        }

        @Override
        public AssetsModel[] newArray(int size) {
            return new AssetsModel[size];
        }
    };
}
