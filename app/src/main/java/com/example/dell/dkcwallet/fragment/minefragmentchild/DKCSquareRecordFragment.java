package com.example.dell.dkcwallet.fragment.minefragmentchild;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by DELL on 2017/8/9.
 */

public class DKCSquareRecordFragment extends BaseFragment {
    @InjectView(R.id.search_text)
    EditText searchText;
    @InjectView(R.id.dkcsquare_recycler)
    RecyclerView dkcsquareRecycler;
    @InjectView(R.id.dkcsquare_refresh)
    SwipeRefreshLayout dkcsquareRefresh;

    public static DKCSquareRecordFragment newInstance(){
        DKCSquareRecordFragment fragment = new DKCSquareRecordFragment();
        return fragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int setLayoutResouceId() {
        return R.layout.fragment_dkcsquare_record;
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
