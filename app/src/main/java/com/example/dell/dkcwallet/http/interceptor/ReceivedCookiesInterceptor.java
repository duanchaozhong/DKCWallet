package com.example.dell.dkcwallet.http.interceptor;

import android.content.Context;
import android.util.Log;

import com.example.dell.dkcwallet.util.SpUtils;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 *
 * @author yiyang
 */
public class ReceivedCookiesInterceptor implements Interceptor {
    private Context context;

    public ReceivedCookiesInterceptor(Context context) {
        super();
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (chain == null)
            Log.d("http", "Receivedchain == null");
        Response originalResponse = chain.proceed(chain.request());
        Log.d("http", "originalResponse" + originalResponse.toString());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            final StringBuffer cookieBuffer = new StringBuffer();
            Observable.just(originalResponse.headers("Set-Cookie"))
                    .map(new Function<List<String>, String>() {
                        @Override
                        public String apply(@NonNull List<String> cookies) throws Exception {
//                            String[] cookieArray = s.split(";");
//                            return cookieArray[0];
                            return cookies.get(0);
                        }
                    })
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(@NonNull String cookie) throws Exception {
//                            cookieBuffer.append(cookie).append(";");
                            cookieBuffer.append(cookie);
                        }
                    });
            SpUtils.put(context, AddCookiesInterceptor.COOKIE, cookieBuffer.toString());
            Log.i("http", "ReceivedCookiesInterceptor:" + cookieBuffer.toString());
        }

        return originalResponse;
    }
}
