package com.example.dell.dkcwallet.http;

import java.io.File;
import java.lang.reflect.Field;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

/**
 *
 * @author yiyang
 */
public class HttpHelper {
    public static  MultipartBody.Part getPicPart(String paramName, String path){
        File file = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData(paramName, file.getName(), requestBody);
        return part;
    }

    /**
     * 使用part注解时在Post请求中默认的Content-Type类型是“application/json”，这就说明我们在接口中不能再使用@Part注解了，我们需要在代码中指定类型。
     * 这时使用@PartMap并
     * 将文字参数类型转换为“text/plain”
     * @param value
     * @return
     */
    public static RequestBody toTextPlain(String value){
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }


    /**
     * 动态设置超时时间
     */
    public static void setDynamicTimeout(Retrofit retrofit, int timeout) {
        try {
            //1、private final okhttp3.Call.Factory callFactory;   Retrofit 的源码 构造方法中
            Field callFactoryField = retrofit.getClass().getDeclaredField("callFactory");
            callFactoryField.setAccessible(true);
            //2、callFactory = new OkHttpClient();   Retrofit 的源码 build()中
            OkHttpClient client = (OkHttpClient) callFactoryField.get(retrofit);
            //3、OkHttpClient(Builder builder)     OkHttpClient 的源码 构造方法中
            Field connectTimeoutField = client.getClass().getDeclaredField("connectTimeout");
            connectTimeoutField.setAccessible(true);
            connectTimeoutField.setInt(client, timeout);
            Field readTimeoutField = client.getClass().getDeclaredField("readTimeout");
            readTimeoutField.setAccessible(true);
            readTimeoutField.setInt(client, timeout);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
