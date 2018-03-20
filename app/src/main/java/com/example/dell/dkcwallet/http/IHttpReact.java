package com.example.dell.dkcwallet.http;

import android.app.Dialog;
import android.support.annotation.StringRes;

import io.reactivex.disposables.Disposable;

/**
 *  Rx的生命周期管理，
 *  以及网络请求时的一些交互
 * @author yiyang
 */
public interface IHttpReact {
    boolean addRxStop(Disposable disposable);

    boolean addRxDestroy(Disposable disposable);
    //取消所有
    void clearDisposabl();

    void remove(Disposable disposable);

    /**
     * 显示ProgressDialog
     */
    void showProgress(@StringRes int msg);

    /**
     * 取消ProgressDialog
     */
    void dismissProgress();

    Dialog getProgress();

    /**
     * 展示失败时候的提示
     */
    void showTips(String errorMsg);
    /**
     * 展示失败时候的提示
     */
    void showTips(@StringRes int errorMsg);
}
