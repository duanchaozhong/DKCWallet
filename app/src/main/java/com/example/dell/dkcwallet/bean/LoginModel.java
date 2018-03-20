package com.example.dell.dkcwallet.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * @author yiyang
 */
public class LoginModel implements Parcelable {

    /**
     * infos : {"createTime":1501324257896,"delStatus":0,"downlineSellOrderCount":-1,
     * "downlineSingleOrderSellCount":-1,"email":"1553219572@qq.com","id":5,"issign":1,"level":1,"loginStatus":1,
     * "mobile":"18334797832","mobilePrefix":"+86","payStatus":1,"stars":0,"updateTime":1501324257896,
     * "username":"18334797832"}
     */

    private InfosBean infos;

    public InfosBean getInfos() {
        return infos;
    }

    public void setInfos(InfosBean infos) {
        this.infos = infos;
    }

    public static class InfosBean implements Parcelable {
        /**
         * createTime : 1501324257896
         * delStatus : 0
         * downlineSellOrderCount : -1
         * downlineSingleOrderSellCount : -1
         * email : 1553219572@qq.com
         * id : 5
         * issign : 1
         * level : 1
         * loginStatus : 1
         * mobile : 18334797832
         * mobilePrefix : +86
         * payStatus : 1
         * stars : 0
         * updateTime : 1501324257896
         * username : 18334797832
         */

        private long createTime;
        private int delStatus;
        private int downlineSellOrderCount;
        private int downlineSingleOrderSellCount;
        private String email;
        private int id;
        private int issign;
        private int level;
        private int loginStatus;
        private String mobile;
        private String mobilePrefix;
        private int payStatus;
        private int stars;
        private long updateTime;
        private String username;

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getDelStatus() {
            return delStatus;
        }

        public void setDelStatus(int delStatus) {
            this.delStatus = delStatus;
        }

        public int getDownlineSellOrderCount() {
            return downlineSellOrderCount;
        }

        public void setDownlineSellOrderCount(int downlineSellOrderCount) {
            this.downlineSellOrderCount = downlineSellOrderCount;
        }

        public int getDownlineSingleOrderSellCount() {
            return downlineSingleOrderSellCount;
        }

        public void setDownlineSingleOrderSellCount(int downlineSingleOrderSellCount) {
            this.downlineSingleOrderSellCount = downlineSingleOrderSellCount;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIssign() {
            return issign;
        }

        public void setIssign(int issign) {
            this.issign = issign;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getLoginStatus() {
            return loginStatus;
        }

        public void setLoginStatus(int loginStatus) {
            this.loginStatus = loginStatus;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getMobilePrefix() {
            return mobilePrefix;
        }

        public void setMobilePrefix(String mobilePrefix) {
            this.mobilePrefix = mobilePrefix;
        }

        public int getPayStatus() {
            return payStatus;
        }

        public void setPayStatus(int payStatus) {
            this.payStatus = payStatus;
        }

        public int getStars() {
            return stars;
        }

        public void setStars(int stars) {
            this.stars = stars;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.createTime);
            dest.writeInt(this.delStatus);
            dest.writeInt(this.downlineSellOrderCount);
            dest.writeInt(this.downlineSingleOrderSellCount);
            dest.writeString(this.email);
            dest.writeInt(this.id);
            dest.writeInt(this.issign);
            dest.writeInt(this.level);
            dest.writeInt(this.loginStatus);
            dest.writeString(this.mobile);
            dest.writeString(this.mobilePrefix);
            dest.writeInt(this.payStatus);
            dest.writeInt(this.stars);
            dest.writeLong(this.updateTime);
            dest.writeString(this.username);
        }

        public InfosBean() {
        }

        protected InfosBean(Parcel in) {
            this.createTime = in.readLong();
            this.delStatus = in.readInt();
            this.downlineSellOrderCount = in.readInt();
            this.downlineSingleOrderSellCount = in.readInt();
            this.email = in.readString();
            this.id = in.readInt();
            this.issign = in.readInt();
            this.level = in.readInt();
            this.loginStatus = in.readInt();
            this.mobile = in.readString();
            this.mobilePrefix = in.readString();
            this.payStatus = in.readInt();
            this.stars = in.readInt();
            this.updateTime = in.readLong();
            this.username = in.readString();
        }

        public static final Creator<InfosBean> CREATOR = new Creator<InfosBean>() {
            @Override
            public InfosBean createFromParcel(Parcel source) {
                return new InfosBean(source);
            }

            @Override
            public InfosBean[] newArray(int size) {
                return new InfosBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.infos, flags);
    }

    public LoginModel() {
    }

    protected LoginModel(Parcel in) {
        this.infos = in.readParcelable(InfosBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<LoginModel> CREATOR = new Parcelable.Creator<LoginModel>() {
        @Override
        public LoginModel createFromParcel(Parcel source) {
            return new LoginModel(source);
        }

        @Override
        public LoginModel[] newArray(int size) {
            return new LoginModel[size];
        }
    };
}
