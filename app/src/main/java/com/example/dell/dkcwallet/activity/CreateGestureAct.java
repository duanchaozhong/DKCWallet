package com.example.dell.dkcwallet.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import com.example.dell.dkcwallet.Constant;
import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.base.BaseAct;
import com.example.dell.dkcwallet.bean.LoginModel;
import com.example.dell.dkcwallet.bean.TokenModel;
import com.example.dell.dkcwallet.dialog.CommonDialog;
import com.example.dell.dkcwallet.http.ApiManger;
import com.example.dell.dkcwallet.http.BaseObserver;
import com.example.dell.dkcwallet.http.HttpResult;
import com.example.dell.dkcwallet.http.ResponseCode;
import com.example.dell.dkcwallet.http.RxUtils;
import com.example.dell.dkcwallet.http.interceptor.AddCookiesInterceptor;
import com.example.dell.dkcwallet.util.ACache;
import com.example.dell.dkcwallet.util.DeviceUtils;
import com.example.dell.dkcwallet.util.SpUtils;
import com.example.dell.dkcwallet.util.ToastUtil;
import com.example.dell.dkcwallet.view.CircleImageView;
import com.google.gson.Gson;
import com.star.lockpattern.util.LockPatternUtil;
import com.star.lockpattern.widget.LockPatternIndicator;
import com.star.lockpattern.widget.LockPatternView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;

/**
 * Created by DELL on 2017/8/11.
 */

public class CreateGestureAct extends BaseAct {
    @InjectView(R.id.head_img)
    CircleImageView headImg;
    @InjectView(R.id.small_lock)
    LockPatternIndicator smallLock;
    @InjectView(R.id.large_lock)
    LockPatternView largeLock;
    @InjectView(R.id.to_login_tv)
    TextView mToLoginTv;
    @InjectView(R.id.hint_tv)
    TextView mHintTv;

    private List<LockPatternView.Cell> mChosenPattern = null;
    private ACache aCache;
    private static final long DELAYTIME = 600L;
    private static final String TAG = "CreateGestureActivity";
    private boolean mIsLogin;
    private byte[] gesturePassword;

    @Override
    protected int setLayoutResouceId() {
        return R.layout.activity_create_gesture;
    }

    @Override
    protected void initView() {
        //Splash界面也要该
        aCache = ACache.get(new File(getFilesDir(), Constant.S_DIR_NAME));

        mIsLogin = getIntent().getBooleanExtra(Constant.IS_LOGIN, false);
        if (mIsLogin) {
            gesturePassword = aCache.getAsBinary(Constant.GESTURE_PASSWORD);
            smallLock.setVisibility(View.INVISIBLE);
            mToLoginTv.setVisibility(View.VISIBLE);
            mHintTv.setVisibility(View.VISIBLE);
        }else {
            gesturePassword = aCache.getAsBinary(Constant.GESTURE_PASSWORD);
            if(gesturePassword !=null && gesturePassword.length>0){
                setVerify();
            }else {
                setSet();
            }
        }

        largeLock.setOnPatternListener(patternListener);
    }

    private boolean isCheck;
    private void setVerify(){
        isCheck = true;
        smallLock.setVisibility(View.INVISIBLE);
        mHintTv.setText(R.string.gesture_default);
    }
    private void setSet(){
        isCheck = false;
        smallLock.setVisibility(View.VISIBLE);
        mHintTv.setText(R.string.create_gesture_default);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    /**
     * 手势监听
     */
    private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            largeLock.removePostClearPatternRunnable();
            //updateStatus(Status.DEFAULT, null);
            largeLock.setPattern(LockPatternView.DisplayMode.DEFAULT);
        }

        @Override
        public void onPatternComplete(List<LockPatternView.Cell> pattern) {
            //Log.e(TAG, "--onPatternDetected--");
            if (!mIsLogin) {
                if(isCheck){//验证手势密码
                    if (LockPatternUtil.checkPattern(pattern, gesturePassword)) {
                        setSet();
                        updateStatus(Status.DEFAULT);
                    } else {
                        updateStatus(Status.ERROR);
                    }
                }else if (mChosenPattern == null && pattern.size() >= 6) {
                    mChosenPattern = new ArrayList<LockPatternView.Cell>(pattern);
                    updateStatus(Status.CORRECT, pattern);
                } else if (mChosenPattern == null && pattern.size() < 6) {
                    updateStatus(Status.LESSERROR, pattern);
                } else if (mChosenPattern != null) {
                    if (mChosenPattern.equals(pattern)) {
                        updateStatus(Status.CONFIRMCORRECT, pattern);
                    } else {
                        updateStatus(Status.CONFIRMERROR, pattern);
                    }
                }
            } else {
                if (pattern != null) {
                    if (LockPatternUtil.checkPattern(pattern, gesturePassword)) {
                        updateStatus(Status.OK);
                    } else {
                        updateStatus(Status.ERROR);
                    }
                }
            }
        }
    };

    /**
     * 更新状态
     * @param status
     */
    private void updateStatus(Status status) {
        mHintTv.setText(status.strId);
        mHintTv.setTextColor(getResources().getColor(status.colorId));
        switch (status) {
            case DEFAULT:
                largeLock.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case ERROR:
                largeLock.setPattern(LockPatternView.DisplayMode.ERROR);
                largeLock.postClearPatternRunnable(DELAYTIME);
                break;
            case OK:
                largeLock.setPattern(LockPatternView.DisplayMode.DEFAULT);
                login();
                break;
        }
    }

    private void login() {
        SpUtils.put(mContext, AddCookiesInterceptor.COOKIE, "");
        ApiManger.getApiService().loginToken(ApiManger.getUserAgent(), SpUtils.get(mContext, Constant.TOKEN_SP, Constant.LOGIN_TOKEN, ""), DeviceUtils.getUniqueId(mContext))
                .flatMap(new Function<HttpResult<LoginModel>, ObservableSource<HttpResult<LoginModel>>>() {
                    @Override
                    public ObservableSource<HttpResult<LoginModel>> apply(@NonNull HttpResult<LoginModel>
                                                                                  loginModelHttpResult) throws
                            Exception {
                        String ret = loginModelHttpResult.getRet();
                        if (ResponseCode.SUCCESS.is(ret)) {
                            SpUtils.put(mContext, Constant.LOGIN_INFO, new Gson().toJson(loginModelHttpResult.getData
                                    ()));
                            startActivity(new Intent(mActivity, MainAct.class));
                            finish();
                            return Observable.empty();
                        } else if (ResponseCode.LOGIN_TOKEN_IS_EXPIRED_OR_INVALID.is(ret)) {
                            return ApiManger.getApiService().refreshToken(SpUtils.get(mContext, Constant.TOKEN_SP, Constant.REFRESH_TOKEN,
                                    ""), DeviceUtils.getUniqueId(mContext))
                                    .flatMap(new Function<HttpResult<TokenModel>,
                                            ObservableSource<HttpResult<LoginModel>>>() {
                                        @Override
                                        public ObservableSource<HttpResult<LoginModel>> apply(@NonNull
                                                                                                      HttpResult<TokenModel>
                                                                                                      tokenModelHttpResult) throws Exception {


                                            String refreshRet = tokenModelHttpResult.getRet();
                                            if (ResponseCode.SUCCESS.is(refreshRet)) {
                                                TokenModel tokenModel = tokenModelHttpResult.getData();
                                                SpUtils.put(mContext, Constant.TOKEN_SP, Constant.LOGIN_TOKEN, tokenModel.getLoginToken());
                                                SpUtils.put(mContext, Constant.TOKEN_SP, Constant.REFRESH_TOKEN, tokenModel
                                                        .getRefreshToken());
                                                return ApiManger.getApiService().loginToken(ApiManger.getUserAgent(), tokenModel.getLoginToken
                                                        (), DeviceUtils.getUniqueId(mContext));
                                            } else /*if(ResponseCode.REFRESH_TOKEN_IS_EXPIRED_OR_INVALID.is
                                            (refreshRet))*/ {
                                                SpUtils.put(mContext, Constant.TOKEN_SP, Constant.LOGIN_TOKEN, "");
                                                SpUtils.put(mContext, Constant.TOKEN_SP, Constant.REFRESH_TOKEN, "");
                                                SpUtils.put(mContext, AddCookiesInterceptor.COOKIE, "");
                                                toLogin();
                                                return Observable.empty();
                                            }
                                        }
                                    });
                        } else {
                            toLogin();
                            return Observable.empty();
                        }
                    }
                })
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(@NonNull Disposable disposable) throws Exception {
//                        SystemClock.sleep(2222);
//                    }
//                })
                .compose(RxUtils.<HttpResult<LoginModel>>applySchedulers())
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        dismissProgress();
                    }
                })
                .subscribe(new BaseObserver<LoginModel>(mActivity) {
                    @Override
                    protected void onSuccess(LoginModel loginModel) {
                        SpUtils.put(mContext, Constant.LOGIN_INFO, new Gson().toJson(loginModel));
                        startActivity(new Intent(mActivity, MainAct.class));
                        finish();
                    }

                    @Override
                    public void onFaild(String code, String message) {
                        super.onFaild(code, message);
                    }
                });
//        ToastUtils.showToast(getApplicationContext(), "登录");

    }

    /**
     * 更新状态
     *
     * @param status
     * @param pattern
     */
    private void updateStatus(Status status, List<LockPatternView.Cell> pattern) {
        mHintTv.setTextColor(getResources().getColor(status.colorId));
        mHintTv.setText(status.strId);
        switch (status) {
            case DEFAULT:
                largeLock.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case CORRECT:
                updateLockPatternIndicator();
                largeLock.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case LESSERROR:
                largeLock.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case CONFIRMERROR:
                largeLock.setPattern(LockPatternView.DisplayMode.ERROR);
                largeLock.postClearPatternRunnable(DELAYTIME);
                break;
            case CONFIRMCORRECT:
                saveChosenPattern(pattern);
                largeLock.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
        }
    }

    /**
     * 更新 Indicator
     */
    private void updateLockPatternIndicator() {
        if (mChosenPattern == null)
            return;
        smallLock.setIndicator(mChosenPattern);
    }

    /**
     * 成功设置了手势密码(跳到首页)
     */
    private void setLockPatternSuccess() {

        //Toast.makeText(this, "create gesture success", Toast.LENGTH_SHORT).show();
    }

    /**
     * 保存手势密码
     */
    private void saveChosenPattern(List<LockPatternView.Cell> cellsC) {
        final List<LockPatternView.Cell> cells = new ArrayList<>(cellsC);
        ApiManger.getApiService().addToken(DeviceUtils.getUniqueId(mContext))
                .compose(RxUtils.<HttpResult<TokenModel>>applySchedulers())
                .subscribe(new BaseObserver<TokenModel>(mActivity) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mProgressDialog.setCancelable(false);
                    }

                    @Override
                    protected void onSuccess(TokenModel tokenModel) {
                        SpUtils.put(mContext, Constant.TOKEN_SP, Constant.LOGIN_TOKEN, tokenModel.getLoginToken());
                        SpUtils.put(mContext, Constant.TOKEN_SP, Constant.REFRESH_TOKEN, tokenModel.getRefreshToken());
                        byte[] bytes = LockPatternUtil.patternToHash(cells);
                        aCache.put(Constant.GESTURE_PASSWORD, bytes);
                        ToastUtil.s(getString(R.string.set_gesture_success));
                        finish();
                    }

                    @Override
                    public void onFaild(String code, @StringRes int message) {
                        super.onFaild(code, message);
                        new CommonDialog.Builder(mActivity)
                                .setTitle(R.string.connect_net_failed)
                                .setNegativeButton(R.string.give_up, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        saveChosenPattern(cells);
                                    }
                                })
                                .show();
                    }
                });
    }

    @OnClick({R.id.back, R.id.to_login_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.to_login_tv:
                toLogin();
                break;
        }
    }

    private void toLogin() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProgress();
//        aCache.put(Constant.GESTURE_PASSWORD, new byte[0]);
//        SpUtils.put(context, COOKIE, "");
                startActivity(new Intent(mActivity, LoginAct.class));
                finish();
            }
        });

    }

    private enum Status {
        //默认的状态，刚开始的时候（初始化状态）
        DEFAULT(R.string.create_gesture_default, R.color.white),
        //第一次记录成功
        CORRECT(R.string.create_gesture_correct, R.color.white),
        //连接的点数小于4（二次确认的时候就不再提示连接的点数小于4，而是提示确认错误）
        LESSERROR(R.string.create_gesture_less_error, R.color.exit_red),
        //二次确认错误
        CONFIRMERROR(R.string.create_gesture_confirm_error, R.color.exit_red),
        //二次确认正确
        CONFIRMCORRECT(R.string.create_gesture_confirm_correct, R.color.white),


        //密码输入错误
        ERROR(R.string.gesture_error, R.color.exit_red),
        //密码输入正确
        OK(R.string.gesture_correct, R.color.white);

        private Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }

        private int strId;
        private int colorId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
