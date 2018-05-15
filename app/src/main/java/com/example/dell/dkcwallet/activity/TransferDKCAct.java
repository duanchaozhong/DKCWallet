package com.example.dell.dkcwallet.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dell.dkcwallet.Constant;
import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.base.BaseAct;
import com.example.dell.dkcwallet.bean.FeeModel;
import com.example.dell.dkcwallet.bean.IsCheckModel;
import com.example.dell.dkcwallet.bean.MapBean;
import com.example.dell.dkcwallet.bean.TransferModel;
import com.example.dell.dkcwallet.bean.TypeModel;
import com.example.dell.dkcwallet.dialog.CommonDialog;
import com.example.dell.dkcwallet.dialog.PayPwdDialog;
import com.example.dell.dkcwallet.helper.CashierInputFilter;
import com.example.dell.dkcwallet.helper.DkcHelper;
import com.example.dell.dkcwallet.helper.OnResultListener;
import com.example.dell.dkcwallet.http.ApiManger;
import com.example.dell.dkcwallet.http.BaseObserver;
import com.example.dell.dkcwallet.http.HttpResult;
import com.example.dell.dkcwallet.http.RxUtils;
import com.example.dell.dkcwallet.util.ToastUtils;
import com.example.dell.dkcwallet.view.MiddleDialog;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by DELL on 2017/8/9.
 */

public class TransferDKCAct extends BaseAct {
    @InjectView(R.id.title_name)
    TextView titleName;
    @InjectView(R.id.back)
    ImageButton back;
    @InjectView(R.id.type_tv)
    TextView mTypeTv;
    @InjectView(R.id.address_et)
    EditText mAddressEt;
    @InjectView(R.id.amount_et)
    EditText mAmountEt;
    @InjectView(R.id.remark_et)
    EditText mRemarkEt;
    @InjectView(R.id.amount_tv)
    TextView mAmountTv;
    @InjectView(R.id.fee_tv)
    TextView mFeeTv;
    @InjectView(R.id.all_amount_tv)
    TextView mAllAmountTv;
    @InjectView(R.id.spinner)
    Spinner mSpinner;
    private PayPwdDialog payPwdDialog;
    private FeeModel mFee;
    private String mTransferAddr;
    private IsCheckModel mWalletModel;
    /**
     * 币种类型
     */
    private int mCurrType;
    /**
     * 转账类型，1转账，2出金到原力
     */
    private int mType = 1;
    private TypeModel mTypeModel;

    @Override
    protected int setLayoutResouceId() {
        return R.layout.activity_transfer_dkc;
    }

    @Override
    protected void initView() {
//        titleName.setText(R.string.dkc_transfer);
        titleName.setText("");
        back.setVisibility(View.VISIBLE);

        mTransferAddr = getIntent().getStringExtra(Constant.DKC_ADDR);
        mCurrType = getIntent().getIntExtra(Constant.DKC_TYPE, -1);
        if(mCurrType == -1){//没传币种类型，则为原力出金
            mType = 2;
        }

        if (TextUtils.isEmpty(mTransferAddr)) {
            //手动输入地址,通过api判断可以选择出金或者转账
            getType();
        }else {
            //扫码进入，不能选择
            if(mType == 2){
//                原力出金，锁定地址
                mAddressEt.setText(mTransferAddr);
                mAddressEt.setEnabled(false);
            }
            //这里不初始化下拉框,在调用isCheck后获得返回的类型再初始化
            walletCheck();
        }

mSpinner.setEnabled(false);
        mAddressEt.setText(mTransferAddr);
        mAmountEt.setFilters(new InputFilter[]{new CashierInputFilter()});
    }

    private void getType() {
        ApiManger.getApiService().getType(null, mCurrType)
                .compose(RxUtils.<HttpResult<TypeModel>>applySchedulers())
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        dismissProgress();
                    }
                })
                .subscribe(new BaseObserver<TypeModel>(mActivity, true) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        getProgress().setCancelable(false);
                    }

                    @Override
                    protected void onSuccess(TypeModel typeModel) {
                        mTransferAddr = typeModel.getAdds();
//                        initSpinner(typeModel.getMap());
                        mTypeModel = typeModel;
                        walletCheck();
                    }

                    @Override
                    public void onFaild(String code, String message) {
                        super.onFaild(code, message);
                        new CommonDialog.Builder(mActivity)
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton(R.string.give_up, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .show();
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
                                        getType();
                                    }
                                })
                                .show();
                    }
                });
    }

    private void initSpinner(final List<MapBean> map) {
        if(map == null){
            return;
        }
        if(map.size() < 2){
            mSpinner.setEnabled(false);
            mSpinner.setBackgroundColor(Color.TRANSPARENT);
        }else {
            mSpinner.setEnabled(true);
            mSpinner.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.down_arrow));
        }
        final String[] strings = new String[map.size()];
        for (int i = 0; i < map.size(); i++) {
            strings[i] = map.get(i).getValue();
        }


//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.emails, R
//                .layout.spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.spinner_view_item, android.R.id.text1, strings);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mEmailLast = strings[position];
                //转账类型
//                mType = position == 0 ? 1 : 2;
                try {
                    mType = Integer.parseInt(map.get(position).getType());
                } catch (Exception e) {
                    e.printStackTrace();
                    mType = position == 0 ? 1 : 2;
                }
                if(mTypeModel != null && mType == 1){//扫码进入时为空
                    mAddressEt.setText("");
                    mAddressEt.setEnabled(true);
                }else {
                    //原力出金不允许修改地址
                    mAddressEt.setText(mTransferAddr);
                    mAddressEt.setEnabled(false);
                }
                mFee = null;
                getFeeModel();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private Disposable getFeeDisposable;
    private void getFeeModel() {
//        clearDisposabl();
        if(getFeeDisposable!=null){
            remove(getFeeDisposable);
            getFeeDisposable=null;
        }
        ApiManger.getApiService().getFee(null, mCurrType, mType)
                .compose(RxUtils.<HttpResult<FeeModel>>applySchedulers())
                .subscribe(new BaseObserver<FeeModel>(mActivity, false) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        getFeeDisposable = d;
                    }

                    @Override
                    protected void onSuccess(FeeModel feeModel) {
                        if("0".equals(feeModel.getOnOff())){
                            //0为关闭，1为开启
                            new CommonDialog.Builder(mActivity)
                                    .setMessage(R.string.transfer_function_closed)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.give_up, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                            return;
                        }
                        mFee = feeModel;
                        Editable charSequence = mAmountEt.getText();
                        refreshTransfeInfo(TextUtils.isEmpty(charSequence) ? "0" : charSequence);
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
                                        getFeeModel();
                                    }
                                })
                                .show();
                    }
                });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
//        walletCheck();
        /*ApiManger.getApiService().walletCheck(null, mCurrType)
                .compose(RxUtils.<HttpResult<FeeModel>>applySchedulers())
                .compose(RxUtils.<HttpResult<FeeModel>>retryWhen(new Action() {
                    @Override
                    public void run() throws Exception {
                        L.i("获取fee失败");
                    }
                }))
                .subscribe(new BaseObserver<FeeModel>(mActivity, false) {
                    @Override
                    protected void onSuccess(FeeModel feeModel) {
                        mFee = new BigDecimal(feeModel.walletCheck());
                    }
                });*/

        RxTextView.textChanges(mAmountEt)
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(100, TimeUnit.MILLISECONDS)
                .map(new Function<CharSequence, CharSequence>() {
                    @Override
                    public CharSequence apply(@NonNull final CharSequence charSequence) throws Exception {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(TextUtils.isEmpty(charSequence)){
                                    mAmountEt.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.common_40sp));
                                }else {
                                    mAmountEt.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.common_72sp));
                                }
                            }
                        });
                        return TextUtils.isEmpty(charSequence) ? "0" : charSequence;
                    }
                })
                .compose(RxUtils.<CharSequence>life(mActivity))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(@NonNull CharSequence charSequence) throws Exception {
                        refreshTransfeInfo(charSequence);
                    }
                });

    }

    private void refreshTransfeInfo(@NonNull CharSequence charSequence) {
        if (mFee != null && !TextUtils.isEmpty(charSequence)) {
            if(".".equals(charSequence.toString())){
                mAmountEt.setText("");
                return;
            }
            BigDecimal value = new BigDecimal(charSequence.toString()).setScale(Constant.DECIMAL_DIGITS, BigDecimal.ROUND_UP);
            String currTypeAd = mWalletModel.getCurrTypeAd();
            currTypeAd = DkcHelper.getFormatName(currTypeAd);
            mAmountTv.setText(value.toString()+ currTypeAd);

//            BigDecimal fee = value.multiply(mFee).setScale(Constant.DECIMAL_DIGITS,
//                    BigDecimal.ROUND_UP);
//            System.out.println(fee);
            BigDecimal fee = getFee(value);
            mFeeTv.setText(fee.toString()+ currTypeAd);

            BigDecimal all = value.add(fee).setScale(Constant.DECIMAL_DIGITS, BigDecimal.ROUND_UP);
            mAllAmountTv.setText(all.toString()+ currTypeAd);
        }
    }

    public BigDecimal getFee(BigDecimal value){
        if(mFee == null){
            return new BigDecimal(0).setScale(Constant.DECIMAL_DIGITS, BigDecimal.ROUND_UP);
        }
        BigDecimal fee;
        if(value.doubleValue() == 0){
            return new BigDecimal(0).setScale(Constant.DECIMAL_DIGITS, BigDecimal.ROUND_UP);
        }
        if("1".equals(mFee.getModel())){//固定收费
            fee = new BigDecimal(mFee.getChargeAmount());
        }else {
            //比率收费（该模式下，通过比率计算的结果与最低手续费比较，取其中最大值）
            //最低手续费
            BigDecimal lessFee = new BigDecimal(mFee.getChargeLessAmount());
            //计算出来的手续费
            BigDecimal feeCalc = value.multiply(new BigDecimal(mFee.getFeeRatio()));
            if(feeCalc.compareTo(lessFee) > 0){
                fee = feeCalc;
            }else {
                fee = lessFee;
            }
        }
        return fee.setScale(Constant.DECIMAL_DIGITS, BigDecimal.ROUND_UP);
    }

    private void walletCheck() {
        ApiManger.getApiService().walletCheck(null, mType==2?null:mCurrType, mTransferAddr)
                .compose(RxUtils.<HttpResult<IsCheckModel>>applySchedulers())
                /*.flatMap(new Function<HttpResult<IsCheckModel>, ObservableSource<HttpResult<FeeModel>>>() {
                    @Override
                    public ObservableSource<HttpResult<FeeModel>> apply(@NonNull HttpResult<IsCheckModel> model) throws
                            Exception {
                        if(ResponseCode.SUCCESS.is(model.getRet())){
                            mWalletModel = model.getData();
                            Observable.just(mWalletModel.getCurrTypeAd())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(@NonNull String s) throws Exception {
                                            titleName.setText(s+(getResources().getConfiguration().locale.getLanguage().contains("zh")?"":" ")+getString(R.string.transfer));
                                        }
                                    });
//                            if(mCurrType == -1){
//                                //扫码进来的，而且是只传了地址
//                                initSpinner(mWalletModel.getMap());
//                            }
                            mCurrType = mWalletModel.getCurrType();
                            if(mTypeModel != null){
                                initSpinner(mTypeModel.getMap());
                            }else {
                                initSpinner(mWalletModel.getMap());
                            }

                            *//*return  ApiManger.getApiService().getFee(null, mCurrType, mType)
//                                    .doOnSubscribe(new Consumer<Disposable>() {
//                                        @Override
//                                        public void accept(@NonNull Disposable disposable) throws Exception {
//                                            SystemClock.sleep(15555);
//                                        }
//                                    })
                                    .compose(RxUtils.<HttpResult<FeeModel>>applySchedulers());*//*
                            return Observable.empty();
                        }else {
                            //没有该钱包
//                            finish();
//                            throw new ApiException("对不起，您没有这个钱包，不能转账");
                            Observable.just(0)
                                    .subscribeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Integer>() {
                                        @Override
                                        public void accept(@NonNull Integer integer) throws Exception {
                                            new CommonDialog.Builder(mActivity)
                                                    .setMessage(R.string.without_this_wallet)
                                                    .setCancelable(false)
                                                    .setPositiveButton(R.string.give_up, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            finish();
                                                        }
                                                    })
                                                    .show();
                                        }
                                    });
                            return Observable.empty();
                        }
                    }
                })
                .compose(RxUtils.<HttpResult<FeeModel>>applySchedulers())*/

                .subscribe(new BaseObserver<IsCheckModel>(mActivity, false) {
                    @Override
                    protected void onSuccess(IsCheckModel model) {
                        mWalletModel = model;
                        Observable.just(mWalletModel.getCurrTypeAd())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(@NonNull String s) throws Exception {
                                        titleName.setText(s+(getResources().getConfiguration().locale.getLanguage().contains("zh")?"":" ")+getString(R.string.transfer));
                                    }
                                });
                        mCurrType = mWalletModel.getCurrType();
                        if(mTypeModel != null){
                            initSpinner(mTypeModel.getMap());
                        }else {
                            initSpinner(mWalletModel.getMap());
                        }
                    }

                    @Override
                    public void onFaild(String code, String message) {
                        super.onFaild(code, message);
                        new CommonDialog.Builder(mActivity)
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton(R.string.give_up, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .show();
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
                                        walletCheck();
                                    }
                                })
                                .show();
                    }
                });
    }

    @android.support.annotation.NonNull
    private BaseObserver<FeeModel> getFeeObserver() {
        return new BaseObserver<FeeModel>(mActivity, false) {
            @Override
            protected void onSuccess(FeeModel feeModel) {
                if("0".equals(feeModel.getOnOff())){
                    //0为关闭，1为开启
                    new CommonDialog.Builder(mActivity)
                            .setMessage(R.string.transfer_function_closed)
                            .setCancelable(false)
                            .setPositiveButton(R.string.give_up, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                    return;
                }
                mFee = feeModel;
                Editable charSequence = mAmountEt.getText();
                refreshTransfeInfo(TextUtils.isEmpty(charSequence) ? "0" : charSequence);
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
                                walletCheck();
                            }
                        })
                        .show();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.back, R.id.sure_transfer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.sure_transfer:
                final String addr = mAddressEt.getText().toString().trim();
                if(TextUtils.isEmpty(addr)){
                    //ToastUtils.showToast(getApplicationContext(), String.format(getString(R.string.s_can_not_be_empty), getString(R.string.trade_receive_addr)));
                    new MiddleDialog(this,String.format(getString(R.string.s_can_not_be_empty), getString(R.string.trade_receive_addr)),R.style.registDialog).show();
                    return;
                }
                final String amount = mAmountEt.getText().toString().trim();
                if (TextUtils.isEmpty(amount)) {
                    //ToastUtils.showToast(getApplicationContext(), String.format(getString(R.string.s_can_not_be_empty), getString(R.string.trade_amount)));
                    new MiddleDialog(this,String.format(getString(R.string.s_can_not_be_empty), getString(R.string.trade_amount)),R.style.registDialog).show();
                    return;
                }
                if (Double.parseDouble(amount) < 1) {
                    //ToastUtils.showToast(getApplicationContext(), R.string.trade_amount_limit);
                    new MiddleDialog(this,this.getString( R.string.trade_amount_limit),R.style.registDialog).show();
                    return;
                }
                if(mFee == null){
//                    ToastUtils.showToast(getApplicationContext(), "费率未加载完成");
                    new CommonDialog.Builder(mActivity)
                            .setTitle(R.string.getting_fee)
                            .setPositiveButton(R.string.waiting, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.give_up, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .show();
                    return;
                }
                if (payPwdDialog == null)
                    payPwdDialog = new PayPwdDialog(TransferDKCAct.this);
                payPwdDialog.showDialog(new OnResultListener<String>() {
                    @Override
                    public void onResult(String s) {
//                        ToastUtils.showToast(getApplicationContext(), "onResult:" + s);
                        /*ApiManger.getApiService().transfer(null, 1, addr, amount,
                                mRemarkEt.getText().toString().trim(), 1, s)
                                .flatMap(new Function<HttpResult<TransferModel>, ObservableSource<HttpResult<TransferDetailModel>>>() {
                                    @Override
                                    public ObservableSource<HttpResult<TransferDetailModel>> apply(@NonNull HttpResult<TransferModel>
                                                                             transferModelHttpResult) throws Exception {
                                        String msg = transferModelHttpResult.getRet();
                                        if(ResponseCode.SUCCESS.is(msg)) {
                                            return ApiManger.getApiService().getTransfer(null, transferModelHttpResult.getData().getTradeNo())
                                                    .compose(RxUtils.<HttpResult<TransferDetailModel>>applySchedulers());
                                        }
                                        throw new ApiException(transferModelHttpResult.getMsg());
                                    }
                                })
                                .compose(RxUtils.<HttpResult<TransferDetailModel>>applySchedulers())
                                .subscribe(new BaseObserver<TransferDetailModel>(mActivity) {
                                    @Override
                                    protected void onSuccess(TransferDetailModel model) {
                                        startActivity(new Intent(mActivity, TransferDetailAct.class).putExtra(Constant.TRANSFER_DETAIL, model));
                                    }
                                });*/
                        ApiManger.getApiService().transfer(null, mCurrType, addr, amount,
                                mRemarkEt.getText().toString().trim(), mType, s)
                                .compose(RxUtils.<HttpResult<TransferModel>>applySchedulers())
                                .subscribe(new BaseObserver<TransferModel>(mActivity) {
                                    @Override
                                    protected void onSuccess(TransferModel model) {
                                        /*
                                        记录类型
                                        当转账类型为转账时，传8
                                        当转账类型为原力出金时，传2
                                         */
                                        int recordType = mType==1 ? 8 : 2;
                                        startActivity(new Intent(mActivity, TransferDetailAct.class).putExtra(Constant.RECORD_TYPE, recordType)
                                                .putExtra(Constant.TRANSFER_TRADE_NO, model.getTradeNo()));
                                        finish();
                                    }

                                    @Override
                                    public void onFaild(String code, String message) {
                                        toast(message);
                                    }
                                });
                    }
                });
                break;
        }
    }
    private void toast(String message){
        new MiddleDialog(this,message,R.style.registDialog).show();
    }
}
