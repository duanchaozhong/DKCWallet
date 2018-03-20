package com.example.dell.dkcwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dell.dkcwallet.Constant;
import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.base.BaseAct;
import com.example.dell.dkcwallet.bean.LoginModel;
import com.example.dell.dkcwallet.helper.UserHelper;
import com.example.dell.dkcwallet.http.ApiManger;
import com.example.dell.dkcwallet.http.BaseObserver;
import com.example.dell.dkcwallet.http.HttpResult;
import com.example.dell.dkcwallet.http.RxUtils;
import com.example.dell.dkcwallet.mInterface.MailCode;
import com.example.dell.dkcwallet.util.ACache;
import com.example.dell.dkcwallet.util.EditTextUtitl;
import com.example.dell.dkcwallet.util.GetMailCodeUtil;
import com.example.dell.dkcwallet.util.SpUtils;
import com.example.dell.dkcwallet.util.TimeUtils;
import com.example.dell.dkcwallet.util.ToastUtils;
import com.google.gson.Gson;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by DELL on 2017/8/7.
 */

public class LoginActBak extends BaseAct {
    @InjectView(R.id.phone_input)
    EditText phoneInput;
    @InjectView(R.id.pwd_input)
    EditText pwdInput;
    @InjectView(R.id.code_button)
    Button codeButton;
    @InjectView(R.id.code_input)
    EditText codeInput;
    @InjectView(R.id.to_login_tv)
    TextView mToLoginTv;

    private GetMailCodeUtil getMailCodeUtil;
    private boolean isStart;

    @Override
    protected int setLayoutResouceId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        EditTextUtitl.setEditTextInhibitInputSpace(codeInput);
        EditTextUtitl.setEditTextInhibitInputSpace(phoneInput);

        if (UserHelper.canGesture()) {
            mToLoginTv.setVisibility(View.VISIBLE);
        }

//        phoneInput.setText("18334797833");
//        pwdInput.setText("A1234567");
//        codeInput.setText("123");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
//        VerUtils.check(mActivity, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    @OnClick({R.id.code_button, R.id.login_bt, R.id.to_login_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.to_login_tv:
                startActivity(new Intent(mActivity, CreateGestureAct.class).putExtra(Constant
                        .IS_LOGIN, true));
                finish();
                break;
            case R.id.code_button:
                String phoneNum = phoneInput.getText().toString().trim();
                if(TextUtils.isEmpty(phoneNum)){
                    ToastUtils.showToast(getApplicationContext(), String.format(getString(R.string.s_can_not_be_empty), getString(R.string
                            .phone_or_email)));
                    return;
                }
                ApiManger.getApiService().getCode(null, phoneNum)
                        .compose(RxUtils.<HttpResult<String>>applySchedulers())
                        .subscribe(new BaseObserver<String>(mActivity) {
                            @Override
                            protected void onSuccess(String s) {
                                if (!isStart) {
                                    isStart = true;
                                    getMailCodeUtil = new GetMailCodeUtil(60000, 1000);
                                    getMailCodeUtil.setMailCode(new MailCode() {
                                        @Override
                                        public void showOnTick(long l) {
                                            codeButton.setClickable(false);
                                            codeButton.setTextColor(getResources().getColor(R.color.white));
                                            codeButton.setText(l / 1000 + "s");
                                            codeButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.code_btton_gray));
                                        }

                                        @Override
                                        public void showOnFinish() {
                                            codeButton.setTextColor(getResources().getColor(R.color.white));
                                            codeButton.setText(R.string.re_get_code);
                                            //设置可点击  
                                            codeButton.setClickable(true);
                                            isStart = false;
                                            codeButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.code_btton));
                                        }
                                    });
                                    getMailCodeUtil.start();
                                }
                            }
                        });
                break;
            case R.id.login_bt:
//                startActivity(new Intent(LoginAct.this,MainAct.class));
//                finish();
//                if(true)
//                    return;
                final String phone = phoneInput.getText().toString().trim();
                String pwd = pwdInput.getText().toString().trim();
                String code = codeInput.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    ToastUtils.showToast(getApplicationContext(), String.format(getString(R.string
                            .s_can_not_be_empty), getString(R.string.phone_or_email)));
                    return;
                }
                if(TextUtils.isEmpty(pwd)){
                    ToastUtils.showToast(getApplicationContext(), String.format(getString(R.string
                            .s_can_not_be_empty), getString(R.string.account_pwd)));
                    return;
                }
                if(TextUtils.isEmpty(code)){
                    ToastUtils.showToast(getApplicationContext(), String.format(getString(R.string
                            .s_can_not_be_empty), getString(R.string.email_code)));
                    return;
                }
//                SpUtils.put(mContext, AddCookiesInterceptor.COOKIE, "");
                ApiManger.getApiService().login(ApiManger.getUserAgent(), TimeUtils.getTimeStamp(), code, phone, pwd)
                        .compose(RxUtils.<HttpResult<LoginModel>>applySchedulers())
                        .subscribe(new BaseObserver<LoginModel>(mActivity) {
                            @Override
                            protected void onSuccess(LoginModel model) {
                                String user = SpUtils.get(mContext, Constant.TOKEN_SP, Constant.USER, "");
                                if(!TextUtils.isEmpty(user)){
                                    if(!user.equals(phone)){
                                        SpUtils.clear(mContext, Constant.TOKEN_SP);
                                        ACache.get(new File(getFilesDir(), Constant.S_DIR_NAME)).put(Constant.GESTURE_PASSWORD, new byte[0]);
                                    }
                                }
                                SpUtils.put(mContext, Constant.TOKEN_SP, Constant.USER, phone);
                                SpUtils.put(mContext, Constant.LOGIN_INFO, new Gson().toJson(model));
                                startActivity(new Intent(LoginActBak.this,MainAct.class));
                                finish();
                            }
                        });

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isStart)
            getMailCodeUtil.cancel();
        isStart = false;
    }
}
