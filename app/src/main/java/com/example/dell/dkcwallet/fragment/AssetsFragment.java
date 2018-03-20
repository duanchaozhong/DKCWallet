package com.example.dell.dkcwallet.fragment;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.dell.dkcwallet.Constant;
import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.activity.DKCAct;
import com.example.dell.dkcwallet.activity.ReceivablesAct;
import com.example.dell.dkcwallet.activity.SettingAct;
import com.example.dell.dkcwallet.base.App;
import com.example.dell.dkcwallet.base.BaseFragment;
import com.example.dell.dkcwallet.bean.AssetsModel;
import com.example.dell.dkcwallet.bean.LoginModel;
import com.example.dell.dkcwallet.bean.TotalAssetsModel;
import com.example.dell.dkcwallet.decoration.SimpleDividerDecoration;
import com.example.dell.dkcwallet.helper.DkcHelper;
import com.example.dell.dkcwallet.helper.GsonHelper;
import com.example.dell.dkcwallet.helper.UserHelper;
import com.example.dell.dkcwallet.http.ApiManger;
import com.example.dell.dkcwallet.http.BaseObserver;
import com.example.dell.dkcwallet.http.HttpResult;
import com.example.dell.dkcwallet.http.RxUtils;
import com.example.dell.dkcwallet.util.L;
import com.example.dell.dkcwallet.util.SpUtils;
import com.example.dell.dkcwallet.util.TimeUtils;
import com.example.dell.dkcwallet.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by DELL on 2017/8/8.
 */

public class AssetsFragment extends BaseFragment {

    @InjectView(R.id.head_img)
    CircleImageView headImg;
    @InjectView(R.id.phone_text)
    TextView phoneText;
    @InjectView(R.id.sum_assets)
    TextView sumAssets;
    @InjectView(R.id.transfer_recycler)
    RecyclerView transferRecycler;
    @InjectView(R.id.transfer_refresh)
    SwipeRefreshLayout transferRefresh;

    private BaseQuickAdapter<AssetsModel, BaseViewHolder> assetsAdapter;
    private List<AssetsModel> assetsList = new ArrayList<AssetsModel>();

    public static AssetsFragment newInstance() {
        AssetsFragment fragment = new AssetsFragment();
        return fragment;
    }

    @Override
    protected void initView() {
        
        LoginModel loginModel = GsonHelper.get().fromJson(SpUtils.get(mContext, Constant.LOGIN_INFO, ""), LoginModel
                .class);
        phoneText.setText(loginModel.getInfos().getMobile());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        transferRecycler.setLayoutManager(layoutManager);
//        transferRecycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        transferRecycler.addItemDecoration(new SimpleDividerDecoration(mContext, getResources().getDimensionPixelSize(R.dimen.common_40dp)));

        assetsAdapter = new BaseQuickAdapter<AssetsModel, BaseViewHolder>(R.layout.item_assets, assetsList) {
            @Override
            protected void convert(BaseViewHolder helper, AssetsModel item) {
                String currTypeAd = item.getCurrTypeAd();
                currTypeAd = DkcHelper.getFormatName(currTypeAd);
                helper.setText(R.id.title, currTypeAd);
                //可用货币
                helper.setText(R.id.sum_dkc, item.getCurrency());
                //货币对应rmb
                helper.setText(R.id.sum_money, "≈¥" + item.getNetConvertRmb());
            }
        };
        transferRecycler.setAdapter(assetsAdapter);
        assetsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), DKCAct.class);
                intent.putExtra(Constant.DKC_TYPE, assetsList.get(position).getCurrType());
                startActivity(intent);
            }
        });

        transferRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        transferRefresh.measure(0, 0);
        loadData();
    }

    private void loadData() {
        ApiManger.getApiService().getTotalAssets(TimeUtils.getTimeStamp())
                .compose(RxUtils.<HttpResult<TotalAssetsModel>>applySchedulers())
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (transferRefresh.isRefreshing())
                            transferRefresh.setRefreshing(false);
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        transferRefresh.setRefreshing(true);
                    }
                })
                .subscribe(new BaseObserver<TotalAssetsModel>(mActivity, false, true) {
                    @Override
                    protected void onSuccess(TotalAssetsModel totalAssetsModel) {
                        assetsList.clear();
                        for (Map.Entry<String, AssetsModel> entry : totalAssetsModel.getInfo().entrySet()) {
                            L.i("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                            if (entry.getValue().getTotalAssets() == null) {
                                assetsList.add(entry.getValue());
                            } else {
                                sumAssets.setText("≈" + entry.getValue().getNetAssets());
                            }
                        }
                        App.setAssestModels(assetsList);
                        assetsAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onVisible() {
        super.onVisible();
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int setLayoutResouceId() {
        return R.layout.fragment_assets;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private String[] perimissionCheck = new String[]{
            Manifest.permission.VIBRATE, Manifest.permission.CAMERA
    };
    @OnClick({R.id.set_img, R.id.qr_iv, R.id.scan_iv, R.id.exchange_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.set_img:
                startActivity(new Intent(getActivity(), SettingAct.class));
                break;
            case R.id.qr_iv:
                startActivity(new Intent(getActivity(), ReceivablesAct.class));
                break;
            case R.id.scan_iv:
//                if (EasyPermissions.hasPermissions(getActivity(), perimissionCheck)) {//检查是否获取该权限
//                    startActivity(new Intent(getActivity(), CaptureActivity.class));
//                } else {
//                    EasyPermissions.requestPermissions(getActivity(), getString(R.string.necessary_permisson), 0, perimissionCheck);
//                }
                /*PermissionHelper.get(mActivity, perimissionCheck)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(@NonNull Boolean aBoolean) throws Exception {
                                startActivity(new Intent(mActivity, CaptureActivity.class));
                            }
                        });*/
                UserHelper.toCapture(mActivity);
                break;
            case R.id.exchange_layout:
                Uri uri = Uri.parse("http://happy-exchange.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
    }
}
