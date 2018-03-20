package com.example.dell.dkcwallet.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.dell.dkcwallet.Constant;
import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.base.ActMgrs;
import com.example.dell.dkcwallet.base.BaseAct;
import com.example.dell.dkcwallet.bean.LoginModel;
import com.example.dell.dkcwallet.dialog.CommonDialog;
import com.example.dell.dkcwallet.helper.GsonHelper;
import com.example.dell.dkcwallet.helper.UserHelper;
import com.example.dell.dkcwallet.util.DataCleanManager;
import com.example.dell.dkcwallet.util.SpUtils;
import com.example.dell.dkcwallet.util.ToastUtils;
import com.example.dell.dkcwallet.util.VerUtils;
import com.example.dell.dkcwallet.view.CircleImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by DELL on 2017/8/10.
 */

public class SettingAct extends BaseAct {
    @InjectView(R.id.head_img)
    CircleImageView headImg;
    @InjectView(R.id.phone_num)
    TextView phoneNum;
    @InjectView(R.id.system_version)
    TextView systemVersion;
    @InjectView(R.id.language_tv)
    TextView mLanguageTv;

    @Override
    protected int setLayoutResouceId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        LoginModel loginModel = GsonHelper.get().fromJson(SpUtils.get(mContext, Constant.LOGIN_INFO, ""), LoginModel
                .class);
        phoneNum.setText(loginModel.getInfos().getMobile());
        systemVersion.setText(VerUtils.getVer(mContext));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Integer integer = SpUtils.get(mContext, Constant.LANGUAGE, Constant.LANGUAGE, -1);
        if (integer != -1) {
            if(integer == 0){
                mLanguageTv.setText(R.string.follow_system);
            }else if(integer == 1){
                mLanguageTv.setText(R.string.chinese);
            }else {
                mLanguageTv.setText(R.string.english);
            }
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    @OnClick({R.id.head_img, R.id.back, R.id.language_relative, R.id.lock_setting_relative, R.id.clear_relative, R.id.help_relative, R.id.update_relative,R.id.exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.head_img:
                break;
            case R.id.back:
                finish();
                break;
            case R.id.language_relative:
                startActivity(new Intent(SettingAct.this,LanguageAct.class));
                break;
            case R.id.lock_setting_relative:
                startActivity(new Intent(SettingAct.this,CreateGestureAct.class));
                break;
            case R.id.clear_relative:
                new CommonDialog.Builder(mActivity)
                        .setTitle(R.string.clear_cache)
                        .enableNegativeButton()
                        .enablePositiveButton(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
//                                    ToastUtils.showToast(getApplicationContext(), DataCleanManager.getTotalCacheSize
//                                                (mContext));
                                    DataCleanManager.clearAllCache(mContext);
                                    ToastUtils.showToast(getApplicationContext(), R.string.clear_cache_success);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                                ToastUtils.showToast(getApplicationContext(), R.string.clear_cache_success);
                            }
                        })
                        .show();

                break;
            case R.id.help_relative:
                break;
            case R.id.update_relative:
//                ApiManger.getApiService()
//                        .lastVersion("A")
//                        .compose(RxUtils.<HttpResult<VerModel>>applySchedulers())
//                        .subscribe(new BaseObserver<VerModel>(mActivity) {
//                            @Override
//                            protected void onSuccess(VerModel model) {
//                                int i = model.getVersion().compareTo(VerUtils.getVer(mContext));
//                                if (i <= 0) {
//                                        ToastUtils.showToast(mActivity.getApplicationContext(), "已是最新版本");
//                                }else {
//                                    ToastUtils.showToast(getApplicationContext(), "需要更新");
//                                }
//                            }
//                        });
                VerUtils.check(mActivity, true);
                break;
            case R.id.exit:
//                ActMgrs.getInstance().AppExit(mContext);
                new CommonDialog.Builder(mActivity)
                        .setTitle(R.string.confirm_to_logout)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                SpUtils.clear(mContext);
                                ActMgrs.getInstance().popAllActivity();
//                                ACache.get(new File(getFilesDir(), Constant.S_DIR_NAME)).put(Constant.GESTURE_PASSWORD, new byte[0]);
                                if(UserHelper.canGesture()){
                                    startActivity(new Intent(mActivity, CreateGestureAct.class).putExtra(Constant
                                            .IS_LOGIN, true));
                                }else {
                                    startActivity(new Intent(mActivity, LoginAct.class));
                                }
                            }
                        })
                        .enableNegativeButton()
                        .show();
                break;
        }
    }
}
