package com.example.dell.dkcwallet.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

/**
 *
 * @author yiyang
 */
public class TransferDetailModel implements Parcelable {

    /**
     * toAddr : 45c6cfa83db7fa68dd4adb8f17f63e4b    转出地址
     * inAddr : c95aafa4caae5c9cdf4c35f99e4e8afb    准入地址
     * quantity : 1.0011                            转出金额
     * fee : 0.005                                  手续费
     * createTime : 1503474160146                   时间
     * status : 1                                   0：审核中 ，1完成，2，失败
     */

    private String toAddr;
    private String inAddr;
    private String currTypeAd;
    private BigDecimal quantity;
    private BigDecimal fee;
    private long createTime;
    private int status;

    public String getToAddr() {
        return toAddr;
    }

    public void setToAddr(String toAddr) {
        this.toAddr = toAddr;
    }

    public String getInAddr() {
        return inAddr;
    }

    public void setInAddr(String inAddr) {
        this.inAddr = inAddr;
    }

    public String getCurrTypeAd() {
        return currTypeAd;
    }

    public void setCurrTypeAd(String currTypeAd) {
        this.currTypeAd = currTypeAd;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.toAddr);
        dest.writeString(this.inAddr);
        dest.writeString(this.currTypeAd);
        dest.writeSerializable(this.quantity);
        dest.writeSerializable(this.fee);
        dest.writeLong(this.createTime);
        dest.writeInt(this.status);
    }

    public TransferDetailModel() {
    }

    protected TransferDetailModel(Parcel in) {
        this.toAddr = in.readString();
        this.inAddr = in.readString();
        this.currTypeAd = in.readString();
        this.quantity = (BigDecimal) in.readSerializable();
        this.fee = (BigDecimal) in.readSerializable();
        this.createTime = in.readLong();
        this.status = in.readInt();
    }

    public static final Creator<TransferDetailModel> CREATOR = new Creator<TransferDetailModel>() {
        @Override
        public TransferDetailModel createFromParcel(Parcel source) {
            return new TransferDetailModel(source);
        }

        @Override
        public TransferDetailModel[] newArray(int size) {
            return new TransferDetailModel[size];
        }
    };
}
