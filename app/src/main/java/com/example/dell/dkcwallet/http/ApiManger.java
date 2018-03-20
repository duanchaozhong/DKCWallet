package com.example.dell.dkcwallet.http;


import android.os.Build;
import android.webkit.WebSettings;

import com.example.dell.dkcwallet.http.interceptor.AddCookiesInterceptor;
import com.example.dell.dkcwallet.http.interceptor.AddSignInterceptor;
import com.example.dell.dkcwallet.http.interceptor.LanguageInterceptor;
import com.example.dell.dkcwallet.http.interceptor.LoginInterceptor;
import com.example.dell.dkcwallet.util.L;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.dell.dkcwallet.base.App.context;

/**
 *
 * @author yiyang
 */
public class ApiManger {
    public static Retrofit retrofit;
    private final ApiService service;

    private ApiManger() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                L.i("Okhttp", message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        File cacheFile = new File(Utils.getContext().getCacheDir(), "cache");
//        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(ApiService.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(ApiService.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(new AddSignInterceptor())
                .addInterceptor(new LanguageInterceptor(context))
                .addInterceptor(new AddCookiesInterceptor(context))
                .addInterceptor(new LoginInterceptor())
                .addInterceptor(interceptor)
//                .addNetworkInterceptor(new HttpCacheInterceptor())
//                .cache(cache)
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(ApiService.BASE_URL)
                .build();

        service = retrofit.create(ApiService.class);
    }

    //  创建单例
    private static class SingletonHolder {
        private static final ApiManger INSTANCE = new ApiManger();
    }

    public static ApiService getApiService() {

        return SingletonHolder.INSTANCE.service;
    }

    public static String getUserAgent() {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

//    class HttpCacheInterceptor implements Interceptor {
//
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request request = chain.request();
//            if (!NetworkUtils.isConnected()) {  //没网强制从缓存读取
//                request = request.newBuilder()
//                        .cacheControl(CacheControl.FORCE_CACHE)
//                        .build();
//                LogUtils.d("Okhttp", "no network");
//            }
//
//
//            Response originalResponse = chain.proceed(request);
//            if (NetworkUtils.isConnected()) {
//                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
//                String cacheControl = request.cacheControl().toString();
//                return originalResponse.newBuilder()
//                        .header("Cache-Control", cacheControl)
//                        .removeHeader("Pragma")
//                        .build();
//            } else {
//                return originalResponse.newBuilder()
//                        .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
//                        .removeHeader("Pragma")
//                        .build();
//            }
//        }
//    }

}
