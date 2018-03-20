package com.example.dell.dkcwallet.helper;

import android.app.Activity;

import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.util.ToastUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 *
 * @author yiyang
 */
public class PermissionHelper {
    public static Observable<Boolean> get(final Activity activity, final String... permissions){
        return new RxPermissions(activity)
                .request(permissions)
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Boolean aBoolean) throws Exception {
                        if(!aBoolean) {
                            ToastUtils.showToast(activity.getApplicationContext(), R.string.permission_denied);
                            throw new IllegalArgumentException("sssss");
                        }
                        return aBoolean;
                    }
                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Boolean>>() {
                    @Override
                    public ObservableSource<? extends Boolean> apply(@NonNull Throwable throwable) throws Exception {
                        return Observable.empty();
                    }
                })/*
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ToastUtils.showToast(activity.getApplicationContext(), R.string.permission_denied);
                    }
                })*/;
    }
    public static Observable<Permission> get2(final Activity activity, final String... permissions){
        return new RxPermissions(activity)
                .requestEach(permissions)
                .flatMap(new Function<Permission, ObservableSource<Permission>>() {
                    @Override
                    public ObservableSource<Permission> apply(@NonNull Permission permission) throws Exception {
                        if(permission.granted){
                            return Observable.just(permission);
                        }else{
                            ToastUtils.showToast(activity.getApplicationContext(), permission.name+":"+activity.getString(R.string.permission_denied));
                            throw new IllegalArgumentException("sssss");
                        }
                    }
                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Permission>>() {
                    @Override
                    public ObservableSource<? extends Permission> apply(@NonNull Throwable throwable) throws Exception {
                        return Observable.empty();
                    }
                })/*
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ToastUtils.showToast(activity.getApplicationContext(), R.string.permission_denied);
                    }
                })*/;
    }
}
