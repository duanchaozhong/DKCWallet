package com.example.dell.dkcwallet.http;

import android.net.ParseException;
import android.support.annotation.CallSuper;
import android.support.annotation.StringRes;

import com.example.dell.dkcwallet.R;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 *
 * @author yiyang
 */
public abstract class BaseObserver<T> implements Observer<HttpResult<T>> {

    private static final int SHOW_MSG_CODE = 0;
    private static final String SUCCESS_CODE = "0";//自定义的业务逻辑，成功返回数据
    private static final String RESPONSE_CODE_FAILED = "10000";  //返回数据失败,严重的错误

    private final IHttpReact mReact;
    private final boolean isShowLoading;
    private final boolean showTip;

    //  Activity 是否在执行onStop()时取消订阅
    private boolean isAddInStop = false;
    private Disposable mDisposable;


    public BaseObserver(IHttpReact react) {
        this(react, true, true);
    }

    public BaseObserver(IHttpReact react, boolean isShowLoading) {
        this(react, isShowLoading, false);
    }

    public BaseObserver(IHttpReact mReact, boolean isShowLoading, boolean showTip) {
        this.mReact = mReact;
        this.isShowLoading = isShowLoading;
        this.showTip = showTip;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (null != mReact) {
            mDisposable = d;
            if (isAddInStop) {    //  在onStop中取消订阅
                mReact.addRxStop(mDisposable);
            } else { //  在onDestroy中取消订阅
                mReact.addRxDestroy(d);
            }
            if (isShowLoading) {
                mReact.showProgress(R.string.loading);
            }
        }
    }

    @Override
    public void onNext(HttpResult<T> result) {
        if (null != mReact && isShowLoading) {
            mReact.dismissProgress();
        }
//        if(result.getRet() == SUCCESS_CODE){
//            T t = result.getContent();
//            onSuccess(t);
//        }else {
//            onFaild(result.getRet(), result.getMessage());
//        }
        if (SUCCESS_CODE.equals(result.getRet())) {
            onSuccess(result.getData());
        } else {
//            onFaild(result.getMsg_code(), result.getTocon());
            onFaild(result.getRet(), result.getMsg());
        }

    }

//    @CallSuper
//    public void onFaild(T result) {
//        onFaild(result.getMsg_code(), result.getTocon());
//    }

    /**
     * 复写该方法来处理业务请求出错
     */
    @CallSuper//if overwrite,you should let it run.
    public void onFaild(String code, String message) {
        //
        if (null == mReact)
            return;

        if(!showTip)
            return;

        if (RESPONSE_CODE_FAILED.equals(code)) {
                mReact.showTips(message);
        } else /*if(code == SHOW_MSG_CODE)*/{
            //TODO 对一些通用的错误code进行处理
            mReact.showTips(message);
        }
    }

    /**
     * 复写该方法来处理网络请求出错
     */
    @CallSuper//if overwrite,you should let it run.
    public void onFaild(String code, @StringRes int message) {
        if (null == mReact)
            return;

        if(!showTip)
            return;

        if (RESPONSE_CODE_FAILED.equals(code)) {
                mReact.showTips(message);
        } else {
            //TODO 对一些通用的错误code进行处理
        }
    }

    protected abstract void onSuccess(T t);


    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
        if (null != mReact && isShowLoading) {
            mReact.dismissProgress();
        }
        String errorCode = RESPONSE_CODE_FAILED;
        int errorMsg = R.string.default_net_failed;
        if(t instanceof ApiException){
            onFaild(errorCode, t.getMessage());
            return;
        }else if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
//            errorCode = httpException.code();
//            errorMsg = httpException.getMessage();
//            getErrorMsg(httpException);

            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.http_net_failed;
        } else if (t instanceof SocketTimeoutException) {  //VPN open
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.socket_timeout_net_failed;
        } else if (t instanceof ConnectException) {
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.connect_net_failed;
//            errorMsg = R.string.the_current_network;
        } else if (t instanceof UnknownHostException) {
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.unknownhost__net_failed;
        } else if (t instanceof UnknownServiceException) {
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.unknownservice_net_failed;
        } else if (t instanceof IOException) {  //飞行模式等
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.io_net_failed;
        } else if (t instanceof JsonParseException
                || t instanceof JsonSyntaxException
                || t instanceof JSONException
                || t instanceof ParseException) {   //  解析错误
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.parse_net_failed;
        }else if (t instanceof RuntimeException) {
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.runtime_net_failed;
        }
        onFaild(errorCode, errorMsg);

    }

    @Override
    public void onComplete() {

    }

}
