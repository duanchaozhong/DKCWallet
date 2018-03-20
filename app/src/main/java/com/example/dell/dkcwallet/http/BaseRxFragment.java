package com.example.dell.dkcwallet.http;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.dell.dkcwallet.util.L;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 *
 * @author yiyang
 */
public class BaseRxFragment extends Fragment implements IHttpReact {

    protected ProgressDialog mProgressDialog;
    protected Context mContext;
    protected BaseRxFragment mFragment;
    protected BaseRxActivity mActivity;

    @Override
    public boolean addRxStop(Disposable disposable) {
        if (disposables2Stop == null) {
            throw new IllegalStateException(
                    "addUtilStop should be called between onStart and onStop");
        }
//        disposables2Stop.add(disposable);
        return disposables2Stop.add(disposable);
    }

    @Override
    public boolean addRxDestroy(Disposable disposable) {
        if (disposables2Destroy == null) {
            throw new IllegalStateException(
                    "addUtilDestroy should be called between onCreate and onDestroy");
        }
//        disposables2Destroy.add(disposable);
        return disposables2Destroy.add(disposable);
    }

    @Override
    public void remove(Disposable disposable) {
        if (disposables2Stop == null && disposables2Destroy == null) {
            throw new IllegalStateException("remove should not be called after onDestroy");
        }
        if (disposables2Stop != null) {
            disposables2Stop.remove(disposable);
        }
        if (disposables2Destroy != null) {
            disposables2Destroy.remove(disposable);
        }
    }

    @Override
    public void showProgress(@StringRes int msg) {
        showProgress(getString(msg));
    }

    public void showProgress(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(mActivity, null, msg);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    clearDisposabl();
                }
            });
        } else {
            mProgressDialog.setMessage(msg);
            if (!mProgressDialog.isShowing())
                mProgressDialog.show();
        }
    }


    @Override
    public Dialog getProgress() {
        return mProgressDialog;
    }


    @Override
    public void dismissProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void showTips(String errorMsg) {
        if (!TextUtils.isEmpty(errorMsg))
            Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTips(@StringRes int errorMsg) {
        showTips(getString(errorMsg));
    }

    private CompositeDisposable disposables2Stop;// 管理Stop取消订阅者者
    private CompositeDisposable disposables2Destroy;// 管理Destroy取消订阅者者

    @Override
    public void clearDisposabl() {
        if (disposables2Stop != null) {
            disposables2Stop.clear();
        }
        if (disposables2Destroy != null) {
            disposables2Destroy.clear();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        L.i(this.getClass().getSimpleName(), "onCreate------------>");
        this.mContext = getContext().getApplicationContext();
        if(!(getActivity() instanceof BaseRxActivity))
            throw new IllegalStateException("Activity必须继承BaseRxActivity");
        this.mActivity = (BaseRxActivity)getActivity();
        this.mFragment = this;

        if (disposables2Destroy != null) {
            throw new IllegalStateException("onCreate called multiple times");
        }
        disposables2Destroy = new CompositeDisposable();

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        if (disposables2Stop != null) {
            throw new IllegalStateException("onStart called multiple times");
        }
        disposables2Stop = new CompositeDisposable();

        super.onStart();

    }
    public void onStop() {
        super.onStop();
        if (disposables2Stop == null) {
            throw new IllegalStateException("onStop called multiple times or onStart not called");
        }
        disposables2Stop.dispose();
        disposables2Stop = null;
    }

    public void onDestroy() {
        super.onDestroy();
        L.i(this.getClass().getSimpleName(), "onDestroy------------>");
        if (disposables2Destroy == null) {
            throw new IllegalStateException(
                    "onDestroy called multiple times or onCreate not called");
        }
        disposables2Destroy.dispose();
        disposables2Destroy = null;
    }

}
