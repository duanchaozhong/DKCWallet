package com.example.dell.dkcwallet.bean;

/**
 *
 * @author yiyang
 */
public class QueryTradeRecordBean {
    public String flowType;
    public String beginAmount;
    public String endAmount;
    public String beginTime;
    public String endTime;

    public QueryTradeRecordBean(String flowType, String beginAmount, String endAmount, String beginTime, String
            endTime) {
        this.flowType = flowType;
        this.beginAmount = beginAmount;
        this.endAmount = endAmount;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }
}
