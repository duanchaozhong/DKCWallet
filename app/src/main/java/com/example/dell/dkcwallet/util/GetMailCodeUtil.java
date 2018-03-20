package com.example.dell.dkcwallet.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Button;

import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.mInterface.MailCode;

/**
 * Created by DELL on 2017/8/8.
 */

public class GetMailCodeUtil extends CountDownTimer {
    private MailCode mailCode;
    public GetMailCodeUtil(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }
    public void setMailCode(MailCode mailcode){
        mailCode = mailcode;
    }
    @Override
    public void onTick(long l) {
        //防止计时过程中重复点击  
        mailCode.showOnTick(l);
//        myButton.setClickable(false);
//        myButton.setTextColor(mContext.getResources().getColor(R.color.red));
//        myButton.setText(l / 1000 + "s");
    }

    @Override
    public void onFinish() {
        //重新给Button设置文字  colorPrimaryDark
//        myButton.setTextColor(mContext.getResources().getColor(R.color.white));
//        myButton.setText("重新获取验证码");
//        //设置可点击  
//        myButton.setClickable(true);
        mailCode.showOnFinish();
    }

}
