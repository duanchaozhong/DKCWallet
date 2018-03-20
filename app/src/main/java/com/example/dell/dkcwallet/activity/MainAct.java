package com.example.dell.dkcwallet.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.base.ActMgrs;
import com.example.dell.dkcwallet.base.BaseAct;
import com.example.dell.dkcwallet.bean.QueryTradeRecordBean;
import com.example.dell.dkcwallet.fragment.AssetsFragment;
import com.example.dell.dkcwallet.fragment.MineFragment;
import com.example.dell.dkcwallet.fragment.TransferFragment;
import com.example.dell.dkcwallet.helper.CashierInputFilter;
import com.example.dell.dkcwallet.util.KeyBoardUtils;
import com.example.dell.dkcwallet.util.ScreenUtils;
import com.example.dell.dkcwallet.util.TimeUtils;
import com.example.dell.dkcwallet.util.ToastUtil;
import com.example.dell.dkcwallet.util.VerUtils;
import com.example.dell.dkcwallet.util.rxbus2.BusCode;
import com.example.dell.dkcwallet.util.rxbus2.RxBus;
import com.example.dell.dkcwallet.util.rxbus2.Subscribe;
import com.example.dell.dkcwallet.util.rxbus2.ThreadMode;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by DELL on 2017/8/8.
 */

public class MainAct extends BaseAct {

    //Tab文字
    private final int[] TAB_TITLES = new int[]{
            R.string.main_tab_assets,R.string.main_tab_transfer,R.string.main_tab_mine
    };
    //Tab图片
    private final int[] TAB_IMGS = new int[]{
            R.drawable.assets_selector,R.drawable.transfer_selector,R.drawable.mine_selector
    };
    //Tab数目
    private final Fragment[] TAB_FRAGMENTS = new Fragment[3];
    @InjectView(R.id.main_tablayout)
    TabLayout mainTablayout;

    private int mCurrentPosition;
    private long exitTime = 0;
    private Dialog mDialog;

    @Override
    protected int setLayoutResouceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        setTabs(mainTablayout, this.getLayoutInflater(), TAB_TITLES, TAB_IMGS);
        mainTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                onTabItemSelected(position);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        onTabItemSelected(0);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        VerUtils.check(mActivity, false);
        RxBus.get().register(this);
    }

    @Subscribe(code = BusCode.SHOW_FILTER_DIALOG, threadMode = ThreadMode.MAIN)
    public void showDialog(){
        if(mDialog == null) {
            mDialog = new Dialog(mActivity, R.style.shapeDialogTheme);
            View root = LayoutInflater.from(mActivity).inflate(R.layout.dialog_filter, null);
            Button confimbtn = (Button) root.findViewById(R.id.confim_btn);
            Button resetbtn = (Button) root.findViewById(R.id.reset_btn);
            final TextView selectflowtypetv = (TextView) root.findViewById(R.id.select_flow_type_tv);
            final LinearLayout layoutflowtype = (LinearLayout) root.findViewById(R.id.layout_flow_type);
            final TextView endtimetv = (TextView) root.findViewById(R.id.end_time_tv);
            final TextView starttimetv = (TextView) root.findViewById(R.id.start_time_tv);
            final EditText endamountet = (EditText) root.findViewById(R.id.end_amount_tv);
            final EditText startamountet = (EditText) root.findViewById(R.id.start_amount_tv);
            final CheckBox flowoutcb = (CheckBox) root.findViewById(R.id.flow_out_cb);
            final CheckBox flowincb = (CheckBox) root.findViewById(R.id.flow_in_cb);
            selectflowtypetv.setSelected(false);
            layoutflowtype.setVisibility(View.GONE);
            resetbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layoutflowtype.setVisibility(View.GONE);
                    selectflowtypetv.setSelected(false);
                    startamountet.setText("");
                    endamountet.setText("");
                    starttimetv.setText("");
                    endtimetv.setText("");
                    flowincb.setChecked(false);
                    flowoutcb.setChecked(false);
                }
            });
            confimbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String flowType = layoutflowtype.getVisibility() == View.GONE ? null :
                            flowincb.isChecked() && flowoutcb.isChecked() ? null :
                                    !flowincb.isChecked() && !flowoutcb.isChecked() ? null :
                                            flowincb.isChecked() ? "0" : "1";
                    String startAmount = TextUtils.isEmpty(startamountet.getText().toString().trim()) ? null : startamountet

                            .getText().toString().trim();
                    String endAmount = TextUtils.isEmpty(endamountet.getText().toString().trim()) ? null : endamountet
                            .getText().toString().trim();
                    String startTime = TextUtils.isEmpty(starttimetv.getText().toString().trim()) ? null : starttimetv
                            .getText().toString().trim();
                    String endTime = TextUtils.isEmpty(endtimetv.getText().toString().trim()) ? null : endtimetv
                            .getText().toString().trim();

                    KeyBoardUtils.closeKeybord(mActivity, startamountet);
                    KeyBoardUtils.closeKeybord(mActivity, endamountet);

                    mDialog.dismiss();
                    RxBus.get().send(BusCode.START_QUERY, new QueryTradeRecordBean(flowType, startAmount, endAmount,
                            startTime, endTime));
                }
            });
            selectflowtypetv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutflowtype.getVisibility() == View.VISIBLE) {
                        layoutflowtype.setVisibility(View.GONE);
                        selectflowtypetv.setSelected(false);
                    } else {
                        layoutflowtype.setVisibility(View.VISIBLE);
                        selectflowtypetv.setSelected(true);
                    }
                }
            });

            startamountet.setFilters(new InputFilter[]{new CashierInputFilter()});
            endamountet.setFilters(new InputFilter[]{new CashierInputFilter()});
            showTimeDialog(starttimetv, startamountet, endamountet);
            showTimeDialog(endtimetv, startamountet, endamountet);

            mDialog.setContentView(root);
            Window dialogWindow = mDialog.getWindow();
            dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.dialogRightAnim);
            WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
            attributes.width = ScreenUtils.getScreenWidth(mContext)/3*2;
            attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            dialogWindow.setAttributes(attributes);
        }
        mDialog.show();
    }

    private void showTimeDialog(final TextView starttimetv, final EditText startamountet, final EditText endamountet) {
        starttimetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                KeyBoardUtils.closeKeybord(mActivity, startamountet);
                KeyBoardUtils.closeKeybord(mActivity, endamountet);

                Calendar selectedDate = Calendar.getInstance();
                Calendar startDate = Calendar.getInstance();
                //startDate.set(2013,1,1);
                Calendar endDate = Calendar.getInstance();
                //endDate.set(2020,1,1);

                //正确设置方式 原因：注意事项有说明
                startDate.set(Calendar.MONTH, startDate.get(Calendar.MONTH)-2);

                new TimePickerView.Builder(mActivity, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {//选中事件回调
                        starttimetv.setText(TimeUtils.getDay(date));
                    }
                }).setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                        .setCancelText(getString(R.string.cancel))//取消按钮文字
                        .setSubmitText(getString(R.string.sure))//确认按钮文字
                        .setContentSize(18)//滚轮文字大小
                        .setTitleSize(20)//标题文字大小
//                        .setTitleText("Title")//标题文字
                        .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                        .isCyclic(false)//是否循环滚动
                        /*.setTitleColor(Color.BLACK)//标题文字颜色
                        .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                        .setCancelColor(Color.BLUE)//取消按钮文字颜色
                        .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
                        .setBgColor(0xFF333333)//滚轮背景颜色 Night mode*/
                        .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                        .setRangDate(startDate,endDate)//起始终止年月日设定
                        .setLabel(getString(R.string.year),getString(R.string.month),getString(R.string.day),getString(R.string.hour),getString(R.string.min),getString(R.string.sec))//默认设置为年月日时分秒
                        .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                        .isDialog(true)//是否显示为对话框样式
                        .build()
                        .show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    private void onTabItemSelected(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                if (TAB_FRAGMENTS[position] == null) {
                    TAB_FRAGMENTS[position] = AssetsFragment.newInstance();
                }
                fragment = TAB_FRAGMENTS[position];
                break;
            case 1:
                if (TAB_FRAGMENTS[position] == null) {
                    TAB_FRAGMENTS[position] = TransferFragment.newInstance();
                }
                fragment = TAB_FRAGMENTS[position];
                break;
            case 2:
                if (TAB_FRAGMENTS[position] == null) {
                    TAB_FRAGMENTS[position] = MineFragment.newInstance();
                }
                fragment = TAB_FRAGMENTS[position];
                break;
        }
        if (TAB_FRAGMENTS[mCurrentPosition] != null) {
            getSupportFragmentManager().beginTransaction().hide(TAB_FRAGMENTS[mCurrentPosition]).commit();
        }
        if (fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(fragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.fl_container, fragment).show(fragment).commit();
        }
        mCurrentPosition = position;
    }

    private void setTabs(TabLayout tabLayout, LayoutInflater inflater, int[] tabTitles, int[] tabImgs) {
        for (int i = 0; i < tabImgs.length; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View view = inflater.inflate(R.layout.item_main_tab_layout, null);
            tab.setCustomView(view);

            TextView tvTitle = (TextView) view.findViewById(R.id.tv_tab);
            tvTitle.setText(tabTitles[i]);
            ImageView imgTab = (ImageView) view.findViewById(R.id.img_tab);
            imgTab.setImageResource(tabImgs[i]);
            tabLayout.addTab(tab);
        }
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.s(getString(R.string.press_again_exit));
            exitTime = System.currentTimeMillis();
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            ActMgrs.getInstance().popAllActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //把申请权限的回调交由EasyPermissions处理
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, MainAct.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
//        startActivity(new Intent(MainAct.this, CaptureActivity.class));
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

}
