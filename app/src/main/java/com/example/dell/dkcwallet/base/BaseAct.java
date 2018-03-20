package com.example.dell.dkcwallet.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.http.BaseRxActivity;
import com.example.dell.dkcwallet.util.PhoneUtil;
import com.gyf.barlibrary.ImmersionBar;

import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public abstract class BaseAct extends BaseRxActivity implements EasyPermissions.PermissionCallbacks{

    protected BaseAct mActivity;
    public Context mContext;
    private ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_base);
        mContext = App.context;
        this.mActivity = this;

        ActMgrs.getInstance().pushActivity(this);

        //固定屏幕方向
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //设置在activity启动的时候输入法默认不开启
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(setLayoutResouceId());
        if (setWhiteBackground()){
                mImmersionBar = ImmersionBar.with(this);
                mImmersionBar.statusBarDarkFont(true,0.2f).init();
        }else{
            mImmersionBar = ImmersionBar.with(this);
            mImmersionBar.statusBarColor(R.color.transparent).init();
        }
        ButterKnife.inject(this);
        initView();
        initData();

        initEvent();


    }

    @Override
    protected void onDestroy() {
        if (mImmersionBar != null){
            mImmersionBar.destroy();
        }

        ButterKnife.reset(this);
        ActMgrs.getInstance().popActivityWithoutFinish(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        ActMgrs.getInstance().popActivity();
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();

    }

    protected abstract int setLayoutResouceId();

    protected abstract void initView();

    protected  void initData(){

    }

    protected void initEvent(){

    }

    public boolean setWhiteBackground(){
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击屏幕空白区域隐藏软键盘
        if (null != this.getCurrentFocus()) {
            PhoneUtil.hideInput(BaseAct.this);
        }
        return super.onTouchEvent(event);
    }
}
