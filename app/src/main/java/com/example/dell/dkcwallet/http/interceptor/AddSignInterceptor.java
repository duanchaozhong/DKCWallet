package com.example.dell.dkcwallet.http.interceptor;

import com.example.dell.dkcwallet.util.TimeUtils;
import com.yiyi.providecodelib.CodeUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * @author yiyang
 */
public class AddSignInterceptor implements Interceptor {

//    public static String key = CodeUtils.getCode();

    public AddSignInterceptor() {
        super();

    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();

        if(request.method().equals("POST")){
            if (request.body() instanceof FormBody && ((FormBody) request.body()).size()>0) {
                FormBody body = (FormBody) request.body();
                FormBody.Builder newFormBodyBuilder = new FormBody.Builder();
                HashMap<String, String> params = new HashMap<>();
                for (int i = 0; i < body.size(); i++) {
                    String name = body.name(i);
                    String value = body.value(i);
                    newFormBodyBuilder.add(name, value);
                    params.put(name, value);
                }

                //有sign的地方有时间戳
                if(!params.containsKey("time")){
                    String timeStamp = TimeUtils.getTimeStamp();
                    newFormBodyBuilder.add("time", timeStamp);
                    params.put("time", timeStamp);
                }
                //

//                Iterator<String> iterator = params.keySet().iterator();
//                List<String> keyList = new ArrayList<String>();
//                while (iterator.hasNext()) {
//                    keyList.add(iterator.next());
//                }
//                Collections.sort(keyList);
//
//                StringBuffer sb = new StringBuffer();
//                for (int i = 0; i < keyList.size(); i++) {
//                    String key = keyList.get(i);
//                    String value = params.get(key);
//                    if (i == 0) {
//                        sb.append(key);
//                    } else {
//                        sb.append("&").append(key);
//                    }
//                    sb.append("=").append(value);
//                }
//
//                String stringSignTemp = sb.toString()+"&key="+key;
//                L.i("stringSignTemp",stringSignTemp);
//                String sign = MD5.getMD5Str(stringSignTemp).toUpperCase();
                String sign = getSign(params);
                newFormBodyBuilder.add("sign", sign);
                requestBuilder.post(newFormBodyBuilder.build());
                request = requestBuilder.build();
            }
        }
        return chain.proceed(request);
    }

    public static String getSign(Map<String, String> params){
//        Iterator<String> iterator = params.keySet().iterator();
//        List<String> keyList = new ArrayList<String>();
//        while (iterator.hasNext()) {
//            keyList.add(iterator.next());
//        }
//        Collections.sort(keyList);
//
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < keyList.size(); i++) {
//            String key = keyList.get(i);
//            String value = params.get(key);
//            if (i == 0) {
//                sb.append(key);
//            } else {
//                sb.append("&").append(key);
//            }
//            sb.append("=").append(value);
//        }
//
//        String stringSignTemp = sb.toString()+"&key="+key;
//        L.i("stringSignTemp",stringSignTemp);
//        return MD5.getMD5Str(stringSignTemp).toUpperCase();
        return CodeUtils.getSign(params);
    }
}
