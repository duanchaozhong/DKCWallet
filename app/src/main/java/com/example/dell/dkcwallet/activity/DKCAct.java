package com.example.dell.dkcwallet.activity;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.dell.dkcwallet.Constant;
import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.base.BaseAct;
import com.example.dell.dkcwallet.bean.AssetsModel;
import com.example.dell.dkcwallet.bean.TradeRecordModel;
import com.example.dell.dkcwallet.bean.WalletModel;
import com.example.dell.dkcwallet.decoration.SimpleDividerDecoration;
import com.example.dell.dkcwallet.helper.DkcHelper;
import com.example.dell.dkcwallet.helper.UserHelper;
import com.example.dell.dkcwallet.http.ApiManger;
import com.example.dell.dkcwallet.http.BaseObserver;
import com.example.dell.dkcwallet.http.HttpResult;
import com.example.dell.dkcwallet.http.ResponseCode;
import com.example.dell.dkcwallet.http.RxUtils;
import com.example.dell.dkcwallet.util.TimeUtils;
import com.example.dell.dkcwallet.util.ToastUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

import static com.example.dell.dkcwallet.base.App.context;

/**
 * Created by DELL on 2017/8/10.
 */

public class DKCAct extends BaseAct {
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.sum_dkc)
    TextView sumDkc;
    @InjectView(R.id.sum_money)
    TextView sumMoney;
    @InjectView(R.id.dkc_chart)
    LineChart dkcChart;
    @InjectView(R.id.address_value)
    TextView addressValue;
    @InjectView(R.id.transfer_recycler)
    RecyclerView mRecyclerView;
    @InjectView(R.id.transfer_refresh)
    SwipeRefreshLayout mRefreshLayout;

    private ArrayList<Entry> dataSet1;
    private ArrayList<Entry> dataSet2;
    private List<String> dayList;

    private String[] perimissionCheck = new String[]{
            Manifest.permission.VIBRATE, Manifest.permission.CAMERA
    };
    private BaseQuickAdapter<TradeRecordModel.InfoBean, BaseViewHolder> mAdapter;
    private int mType;
    private AssetsModel mInfo;
    private HttpResult<WalletModel> mWalletModelHttpResult;

    @Override
    protected int setLayoutResouceId() {
        return R.layout.activity_dkc;
    }

    @Override
    protected void initView() {
//        initChart();
//        dataSet1 = new ArrayList<Entry>();
//        dataSet2 = new ArrayList<Entry>();
//        dayList = new ArrayList<String>();
//        Random random=new Random();
//        for (int i = 0;i<5;i++){
//            dataSet1.add(new Entry(i,random.nextInt(500)));
//            dataSet2.add(new Entry(i,random.nextInt(500)));
//            dayList.add(5+i+"/8");
//        }
//        setData(dataSet1,dataSet2,"A","B");

        initRecycler();

        mType = getIntent().getIntExtra(Constant.DKC_TYPE, 1);
        mRefreshLayout.measure(0, 0);
        mRefreshLayout.setRefreshing(true);
        loadData(false);
    }

    private final int pageSize = 15;
    private void loadData(final boolean loadMore) {
        clearDisposabl();
        if(loadMore)
            mRefreshLayout.setRefreshing(false);
        Observable.zip(loadMore?Observable.just(mWalletModelHttpResult):ApiManger.getApiService().getWalletById(null, mType), ApiManger.getApiService()
                .getUserTradeRecord(null, mType, null, null, null, null, null, null, loadMore ? "" + mDatas.get
                        (mDatas.size() - 1).getCreateTime() : TimeUtils.getTimeStamp(), loadMore?mDatas.get(mDatas.size() - 1).getId():null, pageSize), new
                BiFunction<HttpResult<WalletModel>, HttpResult<TradeRecordModel>, HttpResult<TradeRecordModel>>() {
                    @Override
                    public HttpResult<TradeRecordModel> apply(@io.reactivex.annotations.NonNull HttpResult<WalletModel>
                                                                      walletModelHttpResult, @io.reactivex.annotations
                            .NonNull HttpResult<TradeRecordModel> tradeRecordModelHttpResult) throws Exception {
                        mWalletModelHttpResult = walletModelHttpResult;
                        if (ResponseCode.SUCCESS.is(walletModelHttpResult.getRet()))
                            mInfo = walletModelHttpResult.getData().getInfo();
                        return tradeRecordModelHttpResult;
                    }
                })
                /*.doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                        SystemClock.sleep(1111);
                    }
                })*/
                .compose(RxUtils.<HttpResult<TradeRecordModel>>applySchedulers())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        mRefreshLayout.setRefreshing(true);
                    }
                })
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        if(mRefreshLayout.isRefreshing())
                        mRefreshLayout.setRefreshing(false);
                    }
                })
                .subscribe(new BaseObserver<TradeRecordModel>(mActivity, false, true) {
                    @Override
                    protected void onSuccess(TradeRecordModel tradeRecordModel) {
                        refreshUI();
                        if(!loadMore)
                        mDatas.clear();
                        mDatas.addAll(tradeRecordModel.getInfo());
                        mAdapter.notifyDataSetChanged();

                        if (tradeRecordModel.getInfo().size() < pageSize) {
//                            mAdapter.loadMoreEnd(true);
                            mAdapter.setEnableLoadMore(false);
                        } else {
                            if(!mAdapter.isLoadMoreEnable())
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
                        if(mAdapter.isLoading())
                            mAdapter.loadMoreFail();
                    }
                });

      /*
        ApiManger.getApiService().getWalletById(null, mType)
                .compose(RxUtils.<HttpResult<WalletModel>>applySchedulers())
                .subscribe(new BaseObserver<WalletModel>(mActivity, false) {
                    @Override
                    protected void onSuccess(WalletModel walletModel) {

                    }
                });
        ApiManger.getApiService().getUserTradeRecord(null, mType, null, null, null, null, null, null, null, null, null)
                .compose(RxUtils.<HttpResult<TradeRecordModel>>applySchedulers())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                        mRefreshLayout.setRefreshing(true);
                    }
                })
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        mRefreshLayout.setRefreshing(false);
                    }
                })
                .subscribe(new BaseObserver<TradeRecordModel>(mActivity, false) {
                    @Override
                    protected void onSuccess(TradeRecordModel tradeRecordModel) {


                    }
                });*/
    }

    private void refreshUI() {
        if (mInfo != null) {
            title.setText(DkcHelper.getFormatName(mInfo.getCurrTypeAd()));
            sumDkc.setText(mInfo.getCurrency());
            sumMoney.setText("≈¥" + mInfo.getNetConvertRmb());
            addressValue.setText(mInfo.getTransferAddr());
        }
    }

    List<TradeRecordModel.InfoBean> mDatas = new ArrayList<>();

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
                loadData(false);
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    private void initChart() {

        dkcChart.setDrawGridBackground(false);
        dkcChart.setDescription(null);    //右下角说明文字
        dkcChart.setDrawBorders(false);    //四周是不是有边框
        dkcChart.setBorderWidth(0.5f);
        dkcChart.setBorderColor(Color.WHITE);
        // enable touch gestures
        dkcChart.setTouchEnabled(false);
        // if disabled, scaling can be done on x- and y-axis separately
        //禁止x轴y轴同时进行缩放
        dkcChart.setPinchZoom(false);
        // enable scaling and dragging
        dkcChart.setDragEnabled(false);
        dkcChart.setScaleEnabled(false);

        //禁止图例
        //     dkcChart.getLegend().setEnabled(false);

        //控制轴上的坐标绘制在什么地方 上边下边左边右边
        XAxis xAxis = dkcChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);    //x轴是在上边显示还是显示在下边
//        xAxis.enableGridDashedLine(10f, 10f, 0f);    //背景用虚线表格来绘制  给整成虚线
        xAxis.setAxisMinimum(0f);//设置轴的最小值。这样设置将不会根据提供的数据自动计算。
        xAxis.setGranularityEnabled(true);    //粒度
        xAxis.setGranularity(1f);    //缩放的时候有用，比如放大的时候，我不想把横轴的月份再细分

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dayList.get((int) value);
            }

        });
//        xAxis.setAxisLineWidth(0f);    //设置坐标轴那条线的宽度
        xAxis.setDrawAxisLine(false);    //是否显示坐标轴那条轴
        xAxis.setAxisLineWidth(1f);
        //      xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setLabelCount(5);    //强制有多少个刻度
        xAxis.setTextColor(getResources().getColor(R.color.white));//x坐标轴颜色
        //下面绘制的Grid不会有 "竖的线(与X轴有关
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);    //是不是显示轴上的刻度
        xAxis.setDrawGridLines(false);


        //隐藏左侧坐标轴显示右侧坐标轴，并对右侧的轴进行配置
        dkcChart.getAxisRight().setEnabled(false);
        YAxis leftAxis = dkcChart.getAxisLeft();
        leftAxis.setEnabled(true);
//        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setAxisMinimum(0);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisLineWidth(1f);
        //     leftAxis.setAxisLineColor(R.color.white);
        //坐标轴绘制在图表的内侧
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        //  leftAxis.setTextColor(R.color.white);
        leftAxis.setTextColor(Color.WHITE);
        //确实没看懂这个是干嘛用的，默认是10f
        //这个玩意好像有坐标轴enable的时候是不可用的
        leftAxis.setSpaceBottom(10f);

        //一个chart中包含一个Data对象，一个Data对象包含多个DataSet对象，
        // 每个DataSet是对应一条线上的所有点(相对于折线图来说)
        //   mChart.setData(new LineData());


        // 加载数据
        //从X轴进入的动画
        dkcChart.animateY(3000);   //从Y轴进入的动画
        dkcChart.setData(new LineData());
        //     dkcChart.animateXY(3000, 3000);    //从XY轴一起进入的动画
        Legend l = dkcChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
    }

    private void setData(ArrayList<Entry> entryList1, ArrayList<Entry> entryList2, String title1, String title2) {

        LineData data = dkcChart.getData();

        if (data != null) {
            LineDataSet set = new LineDataSet(entryList1, title1);
            set.setLineWidth(1.5f);
            //       set.setCircleRadius(3.5f);
            set.setColor(getResources().getColor(R.color.chart_slide1));
            set.setDrawCircles(false);
            //      set.setHighLightColor(getResources().getColor(R.color.green_button));
            set.setValueTextSize(10f);
            set.setDrawValues(false);    //节点不显示具体数值
            set.setValueTextColor(R.color.white);
            //      set.enableDashedHighlightLine(10f, 5f, 0f);    //选中某个点的时候高亮显示只是线
            set.setDrawFilled(true);     //填充折线图折线和坐标轴之间
            set.setFillColor(getResources().getColor(R.color.chart_fill1));

            set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set.setDrawVerticalHighlightIndicator(false);//取消纵向辅助线
            set.setDrawHorizontalHighlightIndicator(false);//取消横向辅助线
            set.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler
                        viewPortHandler) {
                    return String.valueOf((int) value);
                }
            });
            LineDataSet set1 = new LineDataSet(entryList2, title2);
            set1.setLineWidth(1.5f);
            //set.setCircleRadius(3.5f);
            set1.setColor(getResources().getColor(R.color.chart_slide2));
            set1.setDrawCircles(false);
            //      set1.setHighLightColor(getResources().getColor(R.color.green_button));
            set1.setValueTextSize(10f);
            set1.setDrawValues(false);    //节点不显示具体数值
            set1.setValueTextColor(getResources().getColor(R.color.white));
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            //     set1.enableDashedHighlightLine(10f, 5f, 0f);    //选中某个点的时候高亮显示只是线
            set1.setDrawFilled(true);     //填充折线图折线和坐标轴之间
            set1.setFillColor(getResources().getColor(R.color.chart_fill2));

            set1.setDrawVerticalHighlightIndicator(false);//取消纵向辅助线
            set1.setDrawHorizontalHighlightIndicator(false);//取消横向辅助线
            set1.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler
                        viewPortHandler) {
                    return String.valueOf((int) value);
                }
            });
            data.addDataSet(set);
            data.addDataSet(set1);
            data.notifyDataChanged();
            dkcChart.notifyDataSetChanged();
            //这行代码必须放到这里，这里设置的是图表这个视窗能显示，x坐标轴，从最大值到最小值之间
            //多少段，好像这个库没有办法设置x轴中的间隔的大小
            //    dkcChart.setVisibleXRangeMaximum(6);
            dkcChart.invalidate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }

    @OnLongClick({R.id.money_address_relative})
    public boolean onViewLongClicked(View view){
        if(!TextUtils.isEmpty(addressValue.getText().toString().trim())) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(addressValue.getText().toString());
            ToastUtils.showToast(getApplicationContext(), R.string.copy_success);
            return true;
        }
        return false;
    }
    @OnClick({R.id.back, R.id.qr_iv, R.id.transfer_relative, R.id.receivables_relative})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.transfer_relative:
//                if (EasyPermissions.hasPermissions(DKCAct.this, perimissionCheck)) {//检查是否获取该权限
//                    startActivity(new Intent(DKCAct.this, CaptureActivity.class));
//                } else {
//                    EasyPermissions.requestPermissions(DKCAct.this, getString(R.string.necessary_permisson), 0, perimissionCheck);
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
            case R.id.qr_iv:
            case R.id.receivables_relative:
                Intent intent = new Intent(DKCAct.this, ReceivablesAct.class);
                if (mInfo != null) {
                    intent.putExtra(Constant.DKC_TYPE, mType);
                    intent.putExtra(Constant.DKC_ADDR, mInfo.getTransferAddr());
                    intent.putExtra(Constant.DKC_TYPE_NAME, mInfo.getCurrTypeAd());
                }
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //把申请权限的回调交由EasyPermissions处理
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, DKCAct.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
//        startActivity(new Intent(DKCAct.this, CaptureActivity.class));
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }
}
