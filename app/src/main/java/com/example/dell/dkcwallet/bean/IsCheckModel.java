package com.example.dell.dkcwallet.bean;

import java.util.List;

/**
 *
 * @author yiyang
 */
public class IsCheckModel {

    /**
     * currTypeAd : DKC
     * currType : 1
     * typeName : 蒂克币
     * transferAddr : 45c6cfa83db7fa68dd4adb8f17f63e4b
     * currency : 3908.7844
     */

    private String currTypeAd;
    private int currType;
    private String typeName;
    private String transferAddr;
    private String currency;
    /**
     * currency : 50163
     * map : [{"type":"1","value":"互助转账"}]
     */

    private List<MapBean> map;

    public String getCurrTypeAd() {
        return currTypeAd;
    }

    public void setCurrTypeAd(String currTypeAd) {
        this.currTypeAd = currTypeAd;
    }

    public int getCurrType() {
        return currType;
    }

    public void setCurrType(int currType) {
        this.currType = currType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTransferAddr() {
        return transferAddr;
    }

    public void setTransferAddr(String transferAddr) {
        this.transferAddr = transferAddr;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<MapBean> getMap() {
        return map;
    }

    public void setMap(List<MapBean> map) {
        this.map = map;
    }

}
