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
    public static String Version="1.1.1";
    public static String pri_key="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ9u3zX72Li1AacgM4iMsBbobY8EXB2zWe3XmOFPAZVVjPRUeZIL3bsPE2mwp5nlfcPYVV2xBhslrzpgBgk0/TtEz0aGNa/EkN+6QMzcKBpNh3R/VuUt9yQQKYQoL5iY3RKUdEU+LZWB1bBMA9eWx6zmjjTHxl/dTlEDXZ6NP9j9AgMBAAECgYEAjCk9hEj+8wr2BAYUJ/R/DfSR9IxBqz60TZsfZNw/KRjHzTjCk7lo2f1b9Kt//JS7ZsQ18CMjmhQcI3atd8Sk5eORtI/nSzOj4LDHqlxek2yOfutYtyO6XUAHw4q/EA9ESr0Es8cEB4xLTUnqEXGdGPquRGE+cPJS6Y/o3vOh2aECQQDiKmu90pOfMzNf0AuCD9J0UHsZh/iRoQuntEBhS87Gk58RDBMChwLa7HxeBks1AaxWZgTO1kJhnHx3oE6fClgZAkEAtHbmzJUOdNxD9taQToigKsjzrOlR1e/VS3zefo4JyRi9TBKjHtPzHCFGSK5HTbWoO0aY08iyque7dtvLNjM0hQJBALzHKd1tGllFFHnCHwj2CfWag9Xgv+NWqHLYKvDLpRHtgFVrXaa7aO+xe3HEERxyhBYt24+GGix9wQrdHSL1MGkCQBpRAIUy82G9QTf8wByDtf+nQEml2KY/DDau5e9EnU6zCd/PE/SJ6fjfLVWp/IHrjLXgLKctrEhZ9K93i+HCg9UCQF4HBx52zRwe9h7ZzWBdq5XOQDR7W1GHQDcoPD8lnoD5TgLwZQ4PVdrVaXx6tchrAj5XpV94ITgMgXyQPvA0fhM=";//私钥
    public static String pub_key="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfbt81+9i4tQGnIDOIjLAW6G2PBFwds1nt15jhTwGVVYz0VHmSC927DxNpsKeZ5X3D2FVdsQYbJa86YAYJNP07RM9GhjWvxJDfukDM3CgaTYd0f1blLfckECmEKC+YmN0SlHRFPi2VgdWwTAPXlses5o40x8Zf3U5RA12ejT/Y/QIDAQAB";//公钥

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
