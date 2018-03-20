package com.example.dell.dkcwallet.bean;

/**
 *
 * @author yiyang
 */
public class TokenModel {

    private String loginToken;
    private String refreshToken;

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
