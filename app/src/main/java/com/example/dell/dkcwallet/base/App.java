package com.example.dell.dkcwallet.base;

import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.chiclam.android.receiver.ApkInstallReceiver;
import com.example.dell.dkcwallet.bean.AssetsModel;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * @author weiwei
 */
public class App extends MultiDexApplication{

    //单例模式
    private static App myApplication = null;
    public static Context context = null;

    public static App getInstance() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        myApplication = this;
        context = getApplicationContext();

        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        registerReceiver(new ApkInstallReceiver(), filter);

        //bugly检测
        CrashReport.initCrashReport(getApplicationContext(), "616c2aa97e", false);

        //       KLog.init(BuildConfig.IS_DEBUG);

        //全局异常崩溃处理
//        CrashHandler handler = CrashHandler.getInstance();
//        handler.init(getApplicationContext());
//        Thread.setDefaultUncaughtExceptionHandler(handler);
        //全局异常崩溃处理

//        Context context = getApplicationContext();
//        // 获取当前包名
//        String packageName = context.getPackageName();
//        // 获取当前进程名
//        String processName = getProcessName(android.os.Process.myPid());
//        // 设置是否为上报进程
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
//        strategy.setBuglyLogUpload(processName == null || processName.equals(packageName));
//        // 初始化Bugly
//        CrashReport.initCrashReport(context, Contans.buglyID, Contans.isDebug, strategy);
        // 如果通过“AndroidManifest.xml”来配置APP信息，初始化方法如下
        // CrashReport.initCrashReport(context, strategy);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static List<AssetsModel> getAssetsModels() {
        return sAssetsModels;
    }

    private static List<AssetsModel> sAssetsModels;
    public static void setAssestModels(List<AssetsModel> assetsList) {
        sAssetsModels = assetsList;
    }
}
