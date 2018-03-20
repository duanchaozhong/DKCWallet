package com.example.dell.dkcwallet.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.adapter.ArrayPagerAdapter;
import com.example.dell.dkcwallet.base.BaseFragment;
import com.example.dell.dkcwallet.bean.AssetsModel;
import com.example.dell.dkcwallet.dialog.CommonDialog;
import com.example.dell.dkcwallet.fragment.minefragmentchild.DCKRecordFragment;
import com.example.dell.dkcwallet.helper.UserHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by DELL on 2017/8/8.
 */

public class MineFragment extends BaseFragment {

    @InjectView(R.id.transfer_tablelayout)
    TabLayout transferTablelayout;
    @InjectView(R.id.dck_record_viewpager)
    ViewPager dckRecordViewpager;

    private String[] TAB_TITLE ;

    //Tab数目
    private  Fragment[] TAB_FRAGMENTS;
    @InjectView(R.id.title_name)
    TextView titleName;

    private ArrayPagerAdapter mViewPagerAdapter;//适配器

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        return fragment;
    }

    @Override
    protected void initView() {
        titleName.setText(R.string.trade_record);

        /*//TODO 这里要改成使用从接口获取到的钱包
        if(UserHelper.isOneAssests()){

            TAB_TITLE = new int[]{
                    R.string.mine_tab_dkc
            };
            TAB_FRAGMENTS = new Fragment[]{
                    DCKRecordFragment.newInstance(1)
            };
        }else {
            TAB_TITLE = new int[]{
                    R.string.mine_tab_dkc, R.string.mine_tab_dkc2
            };
            TAB_FRAGMENTS = new Fragment[]{
                    DCKRecordFragment.newInstance(1), DCKRecordFragment.newInstance(3)
            };
        }*/

        initTab();
//        initTabLayout();


    }

    private void initTabLayout() {
        //设置TabLayout标签的显示方式
        transferTablelayout.setTabMode(TabLayout.MODE_FIXED);
        transferTablelayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //循环注入标签
        for (String tab : TAB_TITLE) {
            transferTablelayout.addTab(transferTablelayout.newTab().setText(tab));
        }

        mViewPagerAdapter = new ArrayPagerAdapter(getActivity().getSupportFragmentManager(), TAB_FRAGMENTS);
        dckRecordViewpager.setAdapter(mViewPagerAdapter);
        dckRecordViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(transferTablelayout));
        //滑动效果
        transferTablelayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(dckRecordViewpager));//点击效果
        dckRecordViewpager.setOffscreenPageLimit(2);
    }

    private boolean needInit = false;
    private void initTab() {
        if(TAB_TITLE != null){
            return;
        }
        UserHelper.getAssests(mActivity, new UserHelper.OnAssestsListener() {
            @Override
            public void onSuccess(List<AssetsModel> assetsModels) {
                needInit = false;
                int size = assetsModels.size();
                TAB_TITLE = new String[size];
                TAB_FRAGMENTS = new Fragment[size];
                for (int i = 0; i<assetsModels.size(); i++){
                    TAB_TITLE[i] = assetsModels.get(i).getCurrTypeAd() + getString(R.string.transfer_record);
                    TAB_FRAGMENTS[i] = DCKRecordFragment.newInstance(assetsModels.get(i).getCurrType());
                }
                initTabLayout();
            }

            @Override
            public void onFaild() {
                new CommonDialog.Builder(mActivity)
                        .setTitle(R.string.retry)
                        .enableNegativeButton(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                needInit = true;
                            }
                        })
                        .enablePositiveButton(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                initTab();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    protected void onVisible() {
        super.onVisible();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(needInit && !hidden){
                initTab();
        }
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int setLayoutResouceId() {
        return R.layout.fragment_mine;
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
}
