package com.example.dell.dkcwallet.http;

import android.support.annotation.NonNull;

import com.example.dell.dkcwallet.base.ActMgrs;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @author yiyang
 */
public class RxUtils {
    /**
     * 无进度Schedulers
     */
    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> life(@NonNull final IHttpReact httpReact) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                                httpReact.addRxDestroy(disposable);
                            }
                        });
            }
        };
    }

    public static <T> ObservableTransformer<T, T> life() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                                try {
                                    ((IHttpReact)ActMgrs.getInstance().currentActivity()).addRxDestroy(disposable);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        };
    }
    public static <T> ObservableTransformer<T, T> retryWhen() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@io.reactivex.annotations.NonNull Observable<T> upstream) {
                return upstream;
            }
        };
    }
    public static <T> ObservableTransformer<T, T> retryWhen(final Action action) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull final Observable<Throwable> throwableObservable) throws
                            Exception {
                        //这里对接口的请求进行错误重试，错误超过3次，显示退出窗口
                        return throwableObservable
                                .zipWith(Observable.range(1, 4), new BiFunction<Throwable, Integer,
                                        Integer>() {
                                    @Override
                                    public Integer apply(@NonNull Throwable throwable, @NonNull Integer integer) throws
                                            Exception {
                                        return integer;
                                    }
                                })
                                .flatMap(new Function<Integer, ObservableSource<?>>() {
                                    @Override
                                    public ObservableSource<?> apply(@NonNull Integer integer) throws Exception {
                                        if (integer > 3) {
                                            //重试超过了3次，操作
                                            if (action != null) {
                                                action.run();
                                            }
                                            return Observable.empty();
                                            //这里必然是出现网络异常
//                                            throw new ConnectException();
//									return null;
                                        }
                                        //在i*5秒后重试，暂时不需要
//								return Observable.timer((long) Math.pow(5,integer), TimeUnit.SECONDS);
                                        return Observable.timer(5, TimeUnit.SECONDS);
                                    }
                                });
                    }
                });
            }
        };
    }


}
