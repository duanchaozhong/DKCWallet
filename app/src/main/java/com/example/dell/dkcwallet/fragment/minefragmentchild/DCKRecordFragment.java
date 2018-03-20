package com.example.dell.dkcwallet.fragment.minefragmentchild;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.dell.dkcwallet.Constant;
import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.activity.TransferDetailAct;
import com.example.dell.dkcwallet.base.BaseFragment;
import com.example.dell.dkcwallet.bean.QueryTradeRecordBean;
import com.example.dell.dkcwallet.bean.TradeRecordModel;
import com.example.dell.dkcwallet.decoration.SimpleDividerDecoration;
import com.example.dell.dkcwallet.http.ApiManger;
import com.example.dell.dkcwallet.http.BaseObserver;
import com.example.dell.dkcwallet.http.HttpResult;
import com.example.dell.dkcwallet.http.RxUtils;
import com.example.dell.dkcwallet.util.TimeUtils;
import com.example.dell.dkcwallet.util.rxbus2.BusCode;
import com.example.dell.dkcwallet.util.rxbus2.RxBus;
import com.example.dell.dkcwallet.util.rxbus2.Subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by DELL on 2017/8/9.
 */

public class DCKRecordFragment extends BaseFragment {
    @InjectView(R.id.search_text)
    EditText searchText;
    @InjectView(R.id.dkc_recycler)
    RecyclerView mRecyclerView;
    @InjectView(R.id.dkc_refresh)
    SwipeRefreshLayout mRefreshLayout;
    private int mType;

    public static DCKRecordFragment newInstance(int currType) {
        DCKRecordFragment fragment = new DCKRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.DKC_TYPE, currType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView() {
        mType = getArguments().getInt(Constant.DKC_TYPE, -1);
        initRecycler();
    }

    private void initRecycler() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new SimpleDividerDecoration(mContext));
        mAdapter = new BaseQuickAdapter<TradeRecordModel.InfoBean, BaseViewHolder>(R.layout
                .item_dkc_trade_record, mDatas) {
            @Override
            protected void convert(BaseViewHolder helper, TradeRecordModel.InfoBean item) {
                /**
                 * tradeOrderNum 流水号
                 * flowType 判断正负  0流入   1流出
                 * amount 金额
                 * createTime 时间戳
                 */

                //如果为true则为转入
                boolean isIn = 0 == item.getFlowType();
                helper.setText(R.id.remark_tv, item.getRemark());
                helper.setText(R.id.trade_order_num_tv, item.getTradeOrderNum());
                helper.setText(R.id.amount_tv, (isIn ? "+" : "-") + item.getAmount().setScale(6, BigDecimal.ROUND_DOWN));
                helper.setText(R.id.time_tv, TimeUtils.stampToDay(item.getCreateTime()));
                helper.setTextColor(R.id.amount_tv, getResources().getColor(isIn ? R.color.item_plus_text : R.color
                        .item_sub_text));
                helper.setImageResource(R.id.flow_type_iv, isIn ? R.drawable.plus_money : R.drawable.subtract_money);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData(true);
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int recordType = mDatas.get(position).getRecordType();
                if(recordType == 8 || recordType == 9 || recordType == 2)
                startActivity(new Intent(mActivity, TransferDetailAct.class).putExtra(Constant.RECORD_TYPE, recordType)
                        .putExtra(Constant.TRANSFER_TRADE_NO, mDatas.get(position).getTradeOrderNum()));
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                filterBean = null;
                loadData(false);
            }
        });
    }

    List<TradeRecordModel.InfoBean> mDatas = new ArrayList<>();
    private BaseQuickAdapter<TradeRecordModel.InfoBean, BaseViewHolder> mAdapter;

    @Override
    protected void lazyLoad() {
        mRefreshLayout.measure(0, 0);
        mRefreshLayout.setRefreshing(true);
        loadData(false);
    }

    QueryTradeRecordBean filterBean;
    @Subscribe(code = BusCode.START_QUERY)
    public void filterQuery(QueryTradeRecordBean bean){
        if(isVisible) {
            this.filterBean = bean;
            loadData(false);
        }
    }

    private final int pageSize = 15;

    private void loadData(boolean loadMore){
        if(filterBean == null) {
            loadData(loadMore, TextUtils.isEmpty(searchText.getText().toString().trim())?null:searchText.getText().toString().trim(), null, null, null, null, null);
        }else {
            loadData(loadMore, TextUtils.isEmpty(searchText.getText().toString().trim())?null:searchText.getText().toString().trim(), filterBean.flowType, filterBean.beginAmount, filterBean.endAmount, filterBean.beginTime, filterBean.endTime);
        }
    }

    private void loadData(final boolean loadMore, String search, String flowType, String beginAmount, String endAmount, String beginTime, String endTime) {
        clearDisposabl();
        if (loadMore)
            mRefreshLayout.setRefreshing(false);
        else
            mRefreshLayout.setRefreshing(true);
        ApiManger.getApiService()
                .getUserTradeRecord(null, mType, search, flowType, beginAmount, endAmount, beginTime, endTime, loadMore ? "" + mDatas.get
                        (mDatas.size() - 1).getCreateTime() : TimeUtils.getTimeStamp(), loadMore ? mDatas.get(mDatas
                        .size() - 1).getId() : null, pageSize)
                /*.doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                        SystemClock.sleep(1111);
                    }
                })*/
                .compose(RxUtils.<HttpResult<TradeRecordModel>>applySchedulers())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
//                        mRefreshLayout.setRefreshing(true);
                    }
                })
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (mRefreshLayout.isRefreshing())
                            mRefreshLayout.setRefreshing(false);
                    }
                })
                .subscribe(new BaseObserver<TradeRecordModel>(mActivity, false, true) {
                    @Override
                    protected void onSuccess(TradeRecordModel tradeRecordModel) {
                        if (!loadMore)
                            mDatas.clear();
                        mDatas.addAll(tradeRecordModel.getInfo());
                        mAdapter.notifyDataSetChanged();
                        if (tradeRecordModel.getInfo().size() < pageSize) {
//                            mAdapter.loadMoreEnd(true);
                            mAdapter.setEnableLoadMore(false);
                        } else {
                            if (!mAdapter.isLoadMoreEnable())
                                mAdapter.setEnableLoadMore(true);
                            mAdapter.loadMoreComplete();
                        }
                    }

                    @Override
                    public void onFaild(String code, String message) {
                        super.onFaild(code, message);
                        if (mAdapter.isLoading())
                            mAdapter.loadMoreFail();
                    }

                    @Override
                    public void onFaild(String code, @StringRes int message) {
                        super.onFaild(code, message);
                        if (mAdapter.isLoading())
                            mAdapter.loadMoreFail();
                    }
                });
    }

    @Override
    protected int setLayoutResouceId() {
        return R.layout.fragment_dkc_record;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        RxBus.get().register(this);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        RxBus.get().unRegister(this);
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.search_iv, R.id.filter_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_iv:
                loadData(false, TextUtils.isEmpty(searchText.getText().toString().trim())?null:searchText.getText().toString().trim(),
                        null,null,null,null,null);
                break;
            case R.id.filter_iv:
                RxBus.get().send(BusCode.SHOW_FILTER_DIALOG);
                break;
        }
    }
}
