package com.example.dell.dkcwallet.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dell.dkcwallet.Constant;
import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.base.BaseAct;
import com.example.dell.dkcwallet.bean.AssetsModel;
import com.example.dell.dkcwallet.bean.WalletListModel;
import com.example.dell.dkcwallet.google.zxing.encoding.EncodingUtils;
import com.example.dell.dkcwallet.helper.DkcHelper;
import com.example.dell.dkcwallet.helper.GsonHelper;
import com.example.dell.dkcwallet.helper.UserHelper;
import com.example.dell.dkcwallet.http.ApiException;
import com.example.dell.dkcwallet.http.ApiManger;
import com.example.dell.dkcwallet.http.ApiService;
import com.example.dell.dkcwallet.http.HttpResult;
import com.example.dell.dkcwallet.http.RxUtils;
import com.example.dell.dkcwallet.http.interceptor.AddCookiesInterceptor;
import com.example.dell.dkcwallet.http.interceptor.AddSignInterceptor;
import com.example.dell.dkcwallet.http.interceptor.LoginInterceptor;
import com.example.dell.dkcwallet.util.L;
import com.example.dell.dkcwallet.util.TimeUtils;
import com.example.dell.dkcwallet.util.ToastUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.dell.dkcwallet.base.App.context;

/**
 * Created by DELL on 2017/8/9.
 */

public class ReceivablesAct extends BaseAct {
    @InjectView(R.id.money_kinds_title)
    TextView moneyKindsTitle;
    @InjectView(R.id.qr_code_img)
    ImageView qrCodeImg;
    @InjectView(R.id.money_address)
    TextView moneyAddress;
    @InjectView(R.id.title)
    RelativeLayout title;
    @InjectView(R.id.back)
    ImageButton back;
    @InjectView(R.id.change_text)
    TextView changeText;
    @InjectView(R.id.title_name)
    TextView titleName;
    private int mType;
    private OkHttpClient mOkHttpClient;
    private List<AssetsModel> mInfo;
    private int currentSize = -1;

    @Override
    protected int setLayoutResouceId() {
        return R.layout.activity_receivables;
    }

    @Override
    protected void initView() {
        back.setVisibility(View.VISIBLE);
        titleName.setText(R.string.gathering);

        mType = getIntent().getIntExtra(Constant.DKC_TYPE, -1);


        //判断是否有传递type进来，如果有就直接显示该dkc对应type的收款二维码
        if (mType == -1) {
            loadData();
        } else {
            String addr = getIntent().getStringExtra(Constant.DKC_ADDR);
            String typeName = getIntent().getStringExtra(Constant.DKC_TYPE_NAME);
            setInfo(addr, typeName);
            loadQrCode(mType);
        }


    }

    private void setInfo(String addr, String typeName) {

        if(mInfo != null && mInfo.size()<2 || UserHelper.isOneAssests()){
            changeText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.arrow_iv).setVisibility(View.GONE);
                    findViewById(R.id.change_bt).setClickable(false);
                }
            },50);
        }

        typeName = DkcHelper.getFormatName(typeName);
        moneyKindsTitle.setText(String.format(getString(R.string.scan_and_pay_type_to_me), typeName));
        changeText.setText(String.format(getString(R.string.pay_type_to_me), typeName));
        moneyAddress.setText(addr);
    }

    private void loadQrCode(int type) {

        //开始加载前，先将qrcode清空
        Glide.with(mActivity).load(new byte[0]).into(qrCodeImg);

        HashMap<String, String> params = new HashMap<>();
        params.put("time", TimeUtils.getTimeStamp());
        params.put("currType", "" + type);
        params.put("sign", AddSignInterceptor.getSign(params));

        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> item : params.entrySet()) {
            builder.add(item.getKey(), item.getValue());
        }

        if (mOkHttpClient == null)
            mOkHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LoginInterceptor())
                    .addInterceptor(new AddCookiesInterceptor(context))
                    .readTimeout(ApiService.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .connectTimeout(ApiService.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .build();


        Observable.just(new Request.Builder()
                .url(ApiService.QR_CODE_URL)
                .post(builder.build())
                .build())
//                .delay(2, TimeUnit.SECONDS)
                .map(new Function<Request, Response>() {
                    @Override
                    public Response apply(@NonNull Request request) throws Exception {
                        return mOkHttpClient.newCall(request).execute();
                    }
                })
                .map(new Function<Response, byte[]>() {
                    @Override
                    public byte[] apply(@NonNull Response response) throws Exception {
                        if (response.isSuccessful()) {
                            MediaType mediaType = response.body().contentType();
                            if (mediaType.toString().contains("image")) {
                                return response.body().bytes();
                            } else {
                                HttpResult result = GsonHelper.get().fromJson(response.body().string(),
                                        HttpResult.class);
                                throw new ApiException(result.getMsg());
//                                ToastUtils.showToast(getApplicationContext(), result.getMsg());
                            }

                        } else {
                            throw new ApiException(getString(R.string.http_net_failed));
//                            ToastUtils.showToast(getApplicationContext(), R.string.http_net_failed);
                        }
//                        return new byte[0];
                    }
                })
                .compose(RxUtils.<byte[]>applySchedulers())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addRxDestroy(disposable);
                        showProgress(R.string.loading);
                        mProgressDialog.setCancelable(false);
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mProgressDialog.setCancelable(true);
                        dismissProgress();
                    }
                })
                .subscribe(new Consumer<byte[]>() {
                    @Override
                    public void accept(@NonNull byte[] bytes) throws Exception {
                        L.i("获取二维码成功");
                        Glide.with(mActivity).load(bytes).into(qrCodeImg);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (throwable instanceof ApiException) {
                            ToastUtils.showToast(getApplicationContext(), throwable.getMessage());
                            return;
                        }
                        ToastUtils.showToast(getApplicationContext(), R.string.default_net_failed);
                    }
                });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @OnLongClick({R.id.money_address})
    public boolean onViewLongClicked(View view){
        if(!TextUtils.isEmpty(moneyAddress.getText().toString().trim())) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(moneyAddress.getText().toString());
            ToastUtils.showToast(getApplicationContext(), R.string.copy_success);
            return true;
        }
        return false;
    }

    private void createQR_Code(String str) {
        Bitmap qrCode = EncodingUtils.createQRCode(str, 750, 750, null);//CheckBox选中就设置Logo
        qrCodeImg.setImageBitmap(qrCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }


    @OnClick({R.id.back, R.id.change_bt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.change_bt:
                loadData();
                break;
        }
    }

    private void loadData() {
        Observable.concat(getLocalInfo(), getRemotoInfo())
                .compose(RxUtils.<List<AssetsModel>>applySchedulers())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addRxDestroy(disposable);
                        showProgress(R.string.loading);
                        mProgressDialog.setCancelable(false);
                    }
                })
                .subscribe(new Consumer<List<AssetsModel>>() {
                    @Override
                    public void accept(@NonNull List<AssetsModel> assetsModels) throws Exception {
                        mInfo = assetsModels;
                        AssetsModel assetsModel = assetsModels.get(++currentSize % (assetsModels.size()));
                        //判断是否进来有带类型，如果有则判断是否与现在要展示的相同，如果相同则继续展示下一个
                        if (assetsModel.getCurrType() == mType) {
                            assetsModel = assetsModels.get(++currentSize % (assetsModels.size()));
                            //清空带进来的参数
                            mType = -1;
                        }

                        setInfo(assetsModel.getTransferAddr(), assetsModel.getCurrTypeAd());
                        //加载二维码
                        loadQrCode(assetsModel.getCurrType());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        dismissProgress();
                        ToastUtils.showToast(getApplicationContext(), R.string.default_net_failed);
                    }
                });
    }

    private ObservableSource<List<AssetsModel>> getRemotoInfo() {
        return getWalletList()
                .map(new Function<HttpResult<WalletListModel>, List<AssetsModel>>() {
                    @Override
                    public List<AssetsModel> apply(@NonNull HttpResult<WalletListModel>
                                                           walletListModelHttpResult)
                            throws Exception {
                        return walletListModelHttpResult.getData().getInfo();
                    }
                });
    }

    private ObservableSource<List<AssetsModel>> getLocalInfo() {
        if (mInfo != null) {
            Observable.just(mInfo);
        }
        return Observable.empty();
    }

    private Observable<HttpResult<WalletListModel>> getWalletList() {
        return ApiManger.getApiService().walletGet(TimeUtils.getTimeStamp())
//                .delay(2, TimeUnit.SECONDS)
                .compose(RxUtils.<HttpResult<WalletListModel>>applySchedulers());
    }
}
