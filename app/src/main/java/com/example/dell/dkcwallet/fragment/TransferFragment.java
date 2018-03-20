package com.example.dell.dkcwallet.fragment;

import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.activity.ReceivablesAct;
import com.example.dell.dkcwallet.base.BaseFragment;
import com.example.dell.dkcwallet.helper.UserHelper;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by DELL on 2017/8/8.
 */

public class TransferFragment extends BaseFragment{

    @InjectView(R.id.title_name)
    TextView titleName;

    private String[] perimissionCheck = new String[]{
            Manifest.permission.VIBRATE, Manifest.permission.CAMERA
    };

    public static TransferFragment newInstance() {
        TransferFragment fragment = new TransferFragment();
        return fragment;
    }

    @Override
    protected void initView() {
        titleName.setText(R.string.transfer);
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int setLayoutResouceId() {
        return R.layout.fragment_transfer;
    }

    @OnClick({R.id.receivables_relative, R.id.qr_code_relative})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.receivables_relative:
                startActivity(new Intent(getActivity(), ReceivablesAct.class));
                break;
            case R.id.qr_code_relative:
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
        }
    }

}
