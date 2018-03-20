package com.example.dell.dkcwallet.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dell.dkcwallet.Constant;
import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.base.BaseAct;
import com.example.dell.dkcwallet.bean.TransferDetailModel;
import com.example.dell.dkcwallet.http.ApiManger;
import com.example.dell.dkcwallet.http.BaseObserver;
import com.example.dell.dkcwallet.http.HttpResult;
import com.example.dell.dkcwallet.http.RxUtils;
import com.example.dell.dkcwallet.util.TimeUtils;

import java.math.BigDecimal;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by DELL on 2017/8/14.
 */

public class TransferDetailAct extends BaseAct {
    @InjectView(R.id.title_name)
    TextView titleName;
    @InjectView(R.id.back)
    ImageButton back;
    @InjectView(R.id.title)
    RelativeLayout mTitle;
    @InjectView(R.id.amount_tv)
    TextView mAmountTv;
    @InjectView(R.id.status_tv)
    TextView mStatusTv;
    @InjectView(R.id.to_address_tv)
    TextView mToAddressTv;
    @InjectView(R.id.in_address_tv)
    TextView mInAddressTv;
    @InjectView(R.id.fee_name)
    TextView mFeeName;
    @InjectView(R.id.fee_tv)
    TextView mFeeTv;
    @InjectView(R.id.trade_no_tv)
    TextView mTradeNoTv;
    @InjectView(R.id.transfer_time_tv)
    TextView mTransferTimeTv;
    @InjectView(R.id.status_iv)
    ImageView mStatusIv;
    private String mTradeNo;
    private int mRecordType;

    @Override
    protected int setLayoutResouceId() {
        return R.layout.activity_transfer_detail;
    }

    @Override
    protected void initView() {
        back.setVisibility(View.VISIBLE);
        titleName.setText(R.string.trade_detail);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        mTradeNo = getIntent().getStringExtra(Constant.TRANSFER_TRADE_NO);
        mRecordType = getIntent().getIntExtra(Constant.RECORD_TYPE, -1);
        ApiManger.getApiService().getTransfer(null, mTradeNo, mRecordType)
                .compose(RxUtils.<HttpResult<TransferDetailModel>>applySchedulers())
                .subscribe(new BaseObserver<TransferDetailModel>(mActivity) {
                    @Override
                    protected void onSuccess(TransferDetailModel model) {
                        mAmountTv.setText(""+model.getQuantity().setScale(4, BigDecimal.ROUND_UP));
                        mFeeTv.setText(model.getFee().setScale(4, BigDecimal.ROUND_UP)+model.getCurrTypeAd());
                        mToAddressTv.setText(model.getToAddr());
                        mInAddressTv.setText(model.getInAddr());
                        mTradeNoTv.setText(mTradeNo);
                        mTransferTimeTv.setText(TimeUtils.stampToDate(model.getCreateTime()));
                        mStatusTv.setText(model.getStatus()==0?getString(R.string.in_review):model.getStatus()==1?getString(R.string.success):getString(R.string.failed));
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }
}
