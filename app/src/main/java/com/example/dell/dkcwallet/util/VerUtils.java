package com.example.dell.dkcwallet.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.chiclam.android.updater.BuildConfig;
import com.chiclam.android.updater.Updater;
import com.chiclam.android.updater.UpdaterConfig;
import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.base.ActMgrs;
import com.example.dell.dkcwallet.base.BaseAct;
import com.example.dell.dkcwallet.bean.VerModel;
import com.example.dell.dkcwallet.dialog.CommonDialog;
import com.example.dell.dkcwallet.http.ApiManger;
import com.example.dell.dkcwallet.http.BaseObserver;
import com.example.dell.dkcwallet.http.HttpResult;
import com.example.dell.dkcwallet.http.RxUtils;

/**
 *
 * @author yiyang
 */
public class VerUtils {

    public static String getVer(Context ctx) {
        return String.valueOf(getPi(ctx).versionName);
    }

    public static PackageInfo getPi(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(ctx.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }// getPackageName表示获取当前ctx所在的包的名称，0表示获取版本信息
        return pi;
    }

    public static void check(final BaseAct mActivity, final boolean isForeground) {
        ApiManger.getApiService()
                .lastVersion("A")
                .compose(RxUtils.<HttpResult<VerModel>>applySchedulers())
                .subscribe(new BaseObserver<VerModel>(mActivity, isForeground) {
                    @Override
                    protected void onSuccess(final VerModel model) {
                        int i = model.getVersion().compareTo(getVer(mActivity));
                        if (i <= 0) {
                            if(isForeground)
                                ToastUtils.showToast(mActivity.getApplicationContext(), R.string.not_update);
                            return;
                        }

                        StringBuilder sb = new StringBuilder();
                        sb.append(mActivity.getString(R.string.laster_ver)).append(model.getVersion())
                                .append(mActivity.getString(R.string.laster_ver_size)).append(model.getSize()).append("\n")
                                .append(mActivity.getString(R.string.ver_update_content))
                                .append(model.getContent().replace("\\n", "\n"));
                        new CommonDialog.Builder(mActivity)
                                .setTitle(R.string.need_to_update)
                                .setMessage(sb.toString())
                                .setPositiveButton(R.string.ver_update_rightNow, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        /*PermissionHelper.get(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                .subscribe(new Consumer<Boolean>() {
                                                    @Override
                                                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                                                        dialog.dismiss();
                                                        update(mActivity, model.getUrl(), model.getVersion());
//                                                        update(mActivity, "http://imtt.dd.qq.com/16891/153B967A535E71146BECD2A4227C894C.apk?fsname=com.l.live.activity_2.0.1_10.apk&csr=1bbd", model.getVersion());
                                                    }
                                                });*/
                                        //TODO 需要改成直接下载,FileUriExposedException
                                        Uri uri = Uri.parse("http://fir.im/hermest");
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        mActivity.startActivity(intent);

                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(model.getType()==0){
                                            ActMgrs.getInstance().popAllActivity();
                                        }else {
                                            dialog.dismiss();
                                        }
                                    }
                                })
                                .show();

                    }

                    @Override
                    public void onFaild(String code, String message) {
                        super.onFaild(code, message);
                        if(isForeground)
                            ToastUtils.showToast(mActivity.getApplicationContext(), message);
                    }
                });
    }

    public static void update(Activity activity, String url, String ver) {
        UpdaterConfig config = new UpdaterConfig.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.app_name))
                .setDescription(activity.getString(R.string.system_download_description))
                .setFilename(String.format("Her-mest_v%s.apk", ver))
                .setFileUrl(url)
                .setCanMediaScanner(true)
                .build();
        Updater.get().showLog(!BuildConfig.DEBUG).download(config);
    }
}
