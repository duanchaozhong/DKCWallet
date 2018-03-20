package com.example.dell.dkcwallet.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dell.dkcwallet.Constant;
import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.base.ActMgrs;
import com.example.dell.dkcwallet.base.BaseAct;
import com.example.dell.dkcwallet.util.SpUtils;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LanguageAct extends BaseAct {


    @InjectView(R.id.title_name)
    TextView mTitleName;
    @InjectView(R.id.back)
    ImageButton mBack;
    @InjectView(R.id.title)
    RelativeLayout mTitle;
    @InjectView(R.id.follow_layout)
    RelativeLayout mFollowLayout;
    @InjectView(R.id.chinese_layout)
    RelativeLayout mChineseLayout;
    @InjectView(R.id.english_layout)
    RelativeLayout mEnglishLayout;

    @Override
    protected int setLayoutResouceId() {
        return R.layout.activity_language;
    }

    @Override
    protected void initView() {
        mBack.setVisibility(View.VISIBLE);
        mTitleName.setText(R.string.set_language);
    }

    private int mCheckLan;
    private int mCurrentLan;
    @Override
    protected void initEvent() {
        super.initEvent();
        mCurrentLan = SpUtils.get(mContext, Constant.LANGUAGE, Constant.LANGUAGE, -1);
        if (mCurrentLan != -1) {
            mCheckLan = mCurrentLan;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    @OnClick({R.id.back, R.id.follow_layout, R.id.chinese_layout, R.id.english_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.follow_layout:
                mCheckLan = 0;
                changeLan();
                break;
            case R.id.chinese_layout:
                mCheckLan = 1;
                changeLan();
                break;
            case R.id.english_layout:
                mCheckLan = 2;
                changeLan();
                break;
        }
    }

    /**
     * 切换语言，切换之后，在每次启动SplashAct的时候就进行设置
     */
    private void changeLan() {
//        if (mCurrentLan == mCheckLan) {
//            onBackPressed();
//            return;
//        }
        SpUtils.put(mContext, Constant.LANGUAGE, Constant.LANGUAGE, mCheckLan);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        if (mCheckLan == 0) {//默认
            config.locale = Locale.getDefault();
        } else if (mCheckLan == 2) {//英文
            config.locale = Locale.ENGLISH;
        } else if (mCheckLan == 1) {//中文
            config.locale = Locale.CHINESE;
        }
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(config, dm);
        ActMgrs.getInstance().popAllActivity();
        Intent intent = new Intent(mActivity, MainAct.class);
        startActivity(intent);
    }
}
