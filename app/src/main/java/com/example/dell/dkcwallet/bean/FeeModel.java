package com.example.dell.dkcwallet.bean;

/**
 *
 * @author yiyang
 */
public class FeeModel {


    /**
     "chargeLessAmount": "20.0000",  比率收费时最低手续费
     "model": "2",                   手续费模式， 1为固定收费，2为比率收费（该模式下，通过比率计算的结果与最低手续费比较，取其中最大值）
     "chargeAmount": "12.0000",      固定模式手续费金额
     "feeRatio": "0.1000",           比率收费转账比率，直接与转账金额相乘
     "onOff": "1"                    转账开关， 1为开启，0为关闭。关闭时提示转账功能关闭。             后台调转账接口提示转账失败。
     */

    private String chargeLessAmount;
    private String model;
    private String chargeAmount;
    private String feeRatio;
    private String onOff;

    public String getChargeLessAmount() {
        return chargeLessAmount;
    }

    public void setChargeLessAmount(String chargeLessAmount) {
        this.chargeLessAmount = chargeLessAmount;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(String chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public String getFeeRatio() {
        return feeRatio;
    }

    public void setFeeRatio(String feeRatio) {
        this.feeRatio = feeRatio;
    }

    public String getOnOff() {
        return onOff;
    }

    public void setOnOff(String onOff) {
        this.onOff = onOff;
    }
}
