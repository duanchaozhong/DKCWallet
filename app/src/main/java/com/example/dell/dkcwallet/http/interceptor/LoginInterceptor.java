package com.example.dell.dkcwallet.http.interceptor;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.dell.dkcwallet.Constant;
import com.example.dell.dkcwallet.activity.LoginAct;
import com.example.dell.dkcwallet.base.ActMgrs;
import com.example.dell.dkcwallet.base.App;
import com.example.dell.dkcwallet.http.HttpResult;
import com.example.dell.dkcwallet.http.ResponseCode;
import com.example.dell.dkcwallet.util.L;
import com.example.dell.dkcwallet.util.SpUtils;
import com.example.dell.dkcwallet.util.ToastUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 判断是否登录，做出相应操作
 * @author yiyang
 */
public class LoginInterceptor implements Interceptor {
    private Gson mGson = new Gson();

    private Context mContext = App.context;
    /**
     * 用来处理多次跳转到login请求
     */
    private ObservableEmitter<HttpResult> mObserableEmitter;

    public LoginInterceptor() {
        Observable.create(new ObservableOnSubscribe<HttpResult>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<HttpResult> e) throws Exception {
                mObserableEmitter = e;
            }
        })
                //100秒内只取第一个请求
                .debounce(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HttpResult>() {
                    @Override
                    public void accept(@NonNull HttpResult result) throws Exception {
                        L.i("LoginInterceptor", "accept");
                        if (ResponseCode.USER_HAD_NOT_LOGIN.is(result.getRet())) {
                            L.i("LoginInterceptor", "not login--------------------------------------");
//                            Activity activity = ActMgrs.getActManager().currentActivity();
//
//                            ActMgrs.getActManager().popAllActivity();
//
//                                Intent intent;
//                            if(activity==null) {
//                                intent = new Intent(activity, LoginAct.class);
//                            }else {
//                                intent = new Intent(App.context, LoginAct.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            }
//                            activity.startActivity(intent.putExtra(Constant.LOGIN_NO, true));

                            ToastUtils.showToast(App.context, result.getMsg(), Toast.LENGTH_LONG);
                            //清除登陆状态
                            SpUtils.put(App.context, AddCookiesInterceptor.COOKIE, "");

                            ActMgrs.getInstance().popAllActivity();
                            Intent intent = new Intent(App.context, LoginAct.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Constant.LOGIN_NO, true);
                            App.context.startActivity(intent);
                        }
                    }
                });
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (chain == null)
            L.i("LoginInterceptor", "Addchain == null");
        Response response = chain.proceed(chain.request());

        MediaType mediaType = response.body().contentType();
        if (mediaType.toString().contains("image")) {
            return response;
        }
        String s = response.body().string();
        HttpResult result = mGson.fromJson(s, HttpResult.class);
//        if ("-1".equals(result.getRet())) {
//            L.i("LoginInterceptor", "not login--------------------------------------");
//            ActMgrs.getActManager().currentActivity().startActivity(new Intent(ActMgrs.getActManager()
//                    .currentActivity(), LoginAct.class).putExtra(Constant.LOGIN_NO, true));
//        }
        //这里替换成rxjava来写
        L.i("LoginInterceptor", "intercept");

//        String login_token = SpUtils.get(mContext, Constant.LOGIN_TOKEN, "");
//        if(TextUtils.isEmpty(login_token)){
            if (result != null)
                mObserableEmitter.onNext(result);

            response = response.newBuilder()
                    .body(ResponseBody.create(null, s))
                    .build();
//        }else {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("time", TimeUtils.getTimeStamp());
//            params.put("token", login_token);
//            params.put("uuid", DeviceUtils.getUniqueId(mContext));
//            params.put("sign", AddSignInterceptor.getSign(params));
//
//            FormBody.Builder builder = new FormBody.Builder();
//            for (Map.Entry<String, String> item : params.entrySet()) {
//                builder.add(item.getKey(), item.getValue());
//            }
//            Request build = new Request.Builder()
//                    .url(ApiService.BASE_URL + "token/loginToken")
//                    .post(builder.build())
//                    .build();
//            Response loginRes = chain.proceed(build);
//            if(loginRes.isSuccessful()){
//                loginRes.body().string();
//
//            }
//        }




        return response;
    }

}
