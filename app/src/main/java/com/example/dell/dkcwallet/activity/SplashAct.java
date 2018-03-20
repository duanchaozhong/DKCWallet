package com.example.dell.dkcwallet.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.dell.dkcwallet.Constant;
import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.base.ActMgrs;
import com.example.dell.dkcwallet.base.BaseAct;
import com.example.dell.dkcwallet.helper.UserHelper;
import com.example.dell.dkcwallet.http.interceptor.AddCookiesInterceptor;
import com.example.dell.dkcwallet.util.SpUtils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class SplashAct extends BaseAct {

    private long sleepTime = 2000;

    @Override
    public boolean setWhiteBackground() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        changeLan();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        Observable.timer(sleepTime, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
//                .filter(new Predicate<Long>() {
//                    @Override
//                    public boolean test(@NonNull Long aLong) throws Exception {
//                        IntentFilter intentFilter = new IntentFilter(
//                                Intent.ACTION_BATTERY_CHANGED);
//                        Intent batteryStatusIntent = registerReceiver(null, intentFilter);
//
//                        int voltage = batteryStatusIntent.getIntExtra("voltage", 99999);
//                        int temperature = batteryStatusIntent.getIntExtra("temperature", 99999);
//                        if (((voltage == 0) && (temperature == 0))
//                                || ((voltage == 10000) && (temperature == 0))) {
//                            //这是通过电池的伏数和温度来判断是真机还是模拟器
//                            ToastUtils.showToast(getApplicationContext(), "暂不支持在模拟器上运行！", Toast.LENGTH_LONG);
//                            return false;
//                        }
//
//                        return !(AntiEmulatorUtils.CheckEmulatorBuild(mContext)||AntiEmulatorUtils.checkPipes()
// ||AntiEmulatorUtils.checkQEmuDriverFile()||AntiEmulatorUtils.CheckDeviceIDS(mContext)||AntiEmulatorUtils
// .CheckImsiIDS(mContext)||AntiEmulatorUtils.CheckPhoneNumber(mContext));
//                    }
//                })
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addRxDestroy(d);
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        if (UserHelper.canGesture()) {
                            startActivity(new Intent(mActivity, CreateGestureAct.class).putExtra(Constant
                                    .IS_LOGIN, true));
                        } else if (TextUtils.isEmpty(SpUtils.get(mContext, AddCookiesInterceptor.COOKIE, ""))
                                || TextUtils.isEmpty(SpUtils.get(mContext, Constant.LOGIN_INFO, ""))) {
                            startActivity(new Intent(SplashAct.this, LoginAct.class));
                        } else {
                            startActivity(new Intent(mActivity, MainAct.class));
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        ActMgrs.getInstance().popActivity(mActivity);
                    }
                });
    }

    private void changeLan() {
        int value = SpUtils.get(this, Constant.LANGUAGE, Constant.LANGUAGE, -1);
        if (value == -1) {
            return;
        }
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        if (value == 0) {//默认
            config.locale = Locale.getDefault();
        } else if (value == 2) {//英文
            config.locale = Locale.ENGLISH;
        } else if (value == 1) {//中文
            config.locale = Locale.CHINESE;
        }
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(config, dm);
    }

    @Override
    protected int setLayoutResouceId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }
}
