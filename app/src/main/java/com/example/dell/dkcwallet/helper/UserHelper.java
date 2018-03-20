package com.example.dell.dkcwallet.helper;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.example.dell.dkcwallet.Constant;
import com.example.dell.dkcwallet.base.App;
import com.example.dell.dkcwallet.base.BaseAct;
import com.example.dell.dkcwallet.bean.AssetsModel;
import com.example.dell.dkcwallet.bean.TotalAssetsModel;
import com.example.dell.dkcwallet.google.zxing.activity.CaptureActivity;
import com.example.dell.dkcwallet.http.ApiManger;
import com.example.dell.dkcwallet.http.BaseObserver;
import com.example.dell.dkcwallet.http.HttpResult;
import com.example.dell.dkcwallet.http.IHttpReact;
import com.example.dell.dkcwallet.http.RxUtils;
import com.example.dell.dkcwallet.util.ACache;
import com.example.dell.dkcwallet.util.L;
import com.example.dell.dkcwallet.util.SpUtils;
import com.example.dell.dkcwallet.util.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 *
 * @author yiyang
 */
public class UserHelper {
    public static boolean canGesture(){
        Context mContext = App.context;
        byte[] pwd = ACache.get(new File(mContext.getFilesDir(), Constant.S_DIR_NAME)).getAsBinary(Constant.GESTURE_PASSWORD);

        String login_token = SpUtils.get(mContext, Constant.TOKEN_SP, Constant.LOGIN_TOKEN, "");
        String refresh_token = SpUtils.get(mContext, Constant.TOKEN_SP, Constant.REFRESH_TOKEN, "");
        if (pwd != null && pwd.length > 0 && (!TextUtils.isEmpty(refresh_token) || !TextUtils.isEmpty(login_token))) {
            return true;
        }else {
            return false;
        }
    }

    public static boolean isOneAssests(){
        if(App.getAssetsModels() == null){
            return false;
        }else if(App.getAssetsModels().size()==1){
            return true;
        }
        return false;

    }

    public interface OnAssestsListener{
        void onSuccess(List<AssetsModel> assetsModels);
        void onFaild();
    }
    public static void getAssests(final IHttpReact react, final OnAssestsListener listener){
        if(App.getAssetsModels() != null){
            listener.onSuccess(App.getAssetsModels());
            return;
        }
        if(react == null){
            return;
        }
        ApiManger.getApiService().getTotalAssets(TimeUtils.getTimeStamp())
                .compose(RxUtils.<HttpResult<TotalAssetsModel>>applySchedulers())
                .subscribe(new BaseObserver<TotalAssetsModel>(react, true, true) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        Dialog progress = react.getProgress();
                        if(progress !=null)
                            progress.setCancelable(false);
                    }

                    @Override
                    protected void onSuccess(TotalAssetsModel totalAssetsModel) {
                        List<AssetsModel> assetsList = new ArrayList<AssetsModel>();
                        for (Map.Entry<String, AssetsModel> entry : totalAssetsModel.getInfo().entrySet()) {
                            L.i("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                            if (entry.getValue().getTotalAssets() == null) {
                                assetsList.add(entry.getValue());
                            }
                        }
                        App.setAssestModels(assetsList);
                        listener.onSuccess(assetsList);
                    }

                    @Override
                    public void onFaild(String code, String message) {
                        super.onFaild(code, message);
                        listener.onFaild();
                    }

                    @Override
                    public void onFaild(String code, @StringRes int message) {
                        super.onFaild(code, message);
                        listener.onFaild();
                    }
                });
    }

    private static String[] perimissionCheck = new String[]{
            Manifest.permission.VIBRATE, Manifest.permission.CAMERA
    };
    public static void toCapture(final BaseAct mActivity){
        PermissionHelper.get(mActivity, perimissionCheck)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        getAssests(mActivity, new OnAssestsListener() {
                            @Override
                            public void onSuccess(List<AssetsModel> assetsModels) {
                                mActivity.startActivity(new Intent(mActivity, CaptureActivity.class));
                            }

                            @Override
                            public void onFaild() {

                            }
                        });

                    }
                });
    }
}
