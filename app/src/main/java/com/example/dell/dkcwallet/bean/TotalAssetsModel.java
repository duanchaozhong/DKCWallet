package com.example.dell.dkcwallet.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author yiyang
 */
public class TotalAssetsModel implements Parcelable {

    public Map<String, AssetsModel> getInfo() {
        return info;
    }

    public void setInfo(Map<String, AssetsModel> info) {
        this.info = info;
    }

    /**
     * info : {"1":{"total":"5766.8336","freeze":"1253.0156","typeName":"蒂克币","currType":1,"currency":"4513.8180",
     * "convertRmb":"1730050.08","netConvertRmb":"1354145.40"},"2":{"total":"1095.0000","freeze":"100.0000",
     * "typeName":"比特币","currType":2,"currency":"995.0000","convertRmb":"1289592.45","netConvertRmb":"1171821.45"},
     * "userTotal":{"totalAssets":"3019642.53","netAssets":"2525966.85"}}
     */

    private Map<String, AssetsModel> info;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.info.size());
        for (Map.Entry<String, AssetsModel> entry : this.info.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }

    public TotalAssetsModel() {
    }

    protected TotalAssetsModel(Parcel in) {
        int infoSize = in.readInt();
        this.info = new HashMap<String, AssetsModel>(infoSize);
        for (int i = 0; i < infoSize; i++) {
            String key = in.readString();
            AssetsModel value = in.readParcelable(AssetsModel.class.getClassLoader());
            this.info.put(key, value);
        }
    }

    public static final Parcelable.Creator<TotalAssetsModel> CREATOR = new Parcelable.Creator<TotalAssetsModel>() {
        @Override
        public TotalAssetsModel createFromParcel(Parcel source) {
            return new TotalAssetsModel(source);
        }

        @Override
        public TotalAssetsModel[] newArray(int size) {
            return new TotalAssetsModel[size];
        }
    };
}
