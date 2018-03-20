package com.example.dell.dkcwallet.http.interceptor;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.dell.dkcwallet.http.ApiException;
import com.example.dell.dkcwallet.util.L;
import com.example.dell.dkcwallet.util.SpUtils;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * @author yiyang
 */
public class AddCookiesInterceptor implements Interceptor {
    private Context context;

    public static final String COOKIE = "cookie";

    public AddCookiesInterceptor(Context context) {
        super();
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (chain == null)
            L.i("http", "Addchain == null");
        final Request.Builder builder = chain.request().newBuilder();
        Observable.just(SpUtils.get(context, COOKIE,""))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String cookie) throws Exception {
                        if(TextUtils.isEmpty(cookie))
                            return;
                        //添加cookie
                        Log.i("http", "AddCookiesInterceptor:"+cookie);
                        builder.addHeader(COOKIE, cookie);
                    }
                });
//        return chain.proceed(builder.build());


        Response originalResponse = chain.proceed(builder.build());
        L.d("http", "originalResponse" + originalResponse.toString());

        //版本更新接口返回的不保存
if(originalResponse.request().url().toString().contains("version")){
    return originalResponse;
}
     if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            final StringBuffer cookieBuffer = new StringBuffer();
            Observable.just(originalResponse.headers("Set-Cookie"))
                    .map(new Function<List<String>, String>() {
                        @Override
                        public String apply(@NonNull List<String> cookies) throws Exception {
//                            String[] cookieArray = s.split(";");
//                            return cookieArray[0];
                            for (int i = 0; i < cookies.size(); i++) {
                                if(cookies.get(i).contains("SESSION=")){
                                    return cookies.get(i);
                                }
                            }
                            throw new ApiException("");
                        }
                    })
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(@NonNull String cookie) throws Exception {
//                            cookieBuffer.append(cookie).append(";");
                            cookieBuffer.append(cookie);
                            SpUtils.put(context, COOKIE, cookieBuffer.toString());
                            L.i("http", "ReceivedCookiesInterceptor:" + cookieBuffer.toString());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {

                        }
                    });

        }
        return originalResponse;
    }
}
