package com.example.dell.dkcwallet.bean;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author yiyang
 */
public class TradeRecordModel {

    private List<InfoBean> info;

    public List<InfoBean> getInfo() {
        return info;
    }

    public void setInfo(List<InfoBean> info) {
        this.info = info;
    }

    public static class InfoBean {
        /**
         * accountBalance : 4513.818
         * amount : 0.5
         * createTime : 1503025418930
         * flowType : 1
         * id : 250
         * recordType : 5
         * remark : 转账完成，转账手续费扣除
         * status : 1
         * tradeOrderNum : 1620170818000000050011033889
         * transferType : 1
         * updateTime : 1503025418930
         * updateUser : 18334797832
         * updateUserPhone : 18334797832
         * userId : 5
         * walletId : 10
         * transferAddr : c95aafa4caae5c9cdf4c35f99e4e8afb
         */

        private String accountBalance;
        private BigDecimal amount;
        private long createTime;
        private int flowType;
        private long id;
        private int recordType;
        private String remark;
        private int status;
        private String tradeOrderNum;
        private int transferType;
        private long updateTime;
        private String updateUser;
        private String updateUserPhone;
        private long userId;
        private int walletId;
        private String transferAddr;

        public String getAccountBalance() {
            return accountBalance;
        }

        public void setAccountBalance(String accountBalance) {
            this.accountBalance = accountBalance;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getFlowType() {
            return flowType;
        }

        public void setFlowType(int flowType) {
            this.flowType = flowType;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getRecordType() {
            return recordType;
        }

        public void setRecordType(int recordType) {
            this.recordType = recordType;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTradeOrderNum() {
            return tradeOrderNum;
        }

        public void setTradeOrderNum(String tradeOrderNum) {
            this.tradeOrderNum = tradeOrderNum;
        }

        public int getTransferType() {
            return transferType;
        }

        public void setTransferType(int transferType) {
            this.transferType = transferType;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public String getUpdateUser() {
            return updateUser;
        }

        public void setUpdateUser(String updateUser) {
            this.updateUser = updateUser;
        }

        public String getUpdateUserPhone() {
            return updateUserPhone;
        }

        public void setUpdateUserPhone(String updateUserPhone) {
            this.updateUserPhone = updateUserPhone;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public int getWalletId() {
            return walletId;
        }

        public void setWalletId(int walletId) {
            this.walletId = walletId;
        }

        public String getTransferAddr() {
            return transferAddr;
        }

        public void setTransferAddr(String transferAddr) {
            this.transferAddr = transferAddr;
        }
    }
}
