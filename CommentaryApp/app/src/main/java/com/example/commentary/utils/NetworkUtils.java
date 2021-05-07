package com.example.commentary.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.commentary.MainActivity;
import com.example.commentary.MainActivity2;
import com.example.commentary.ui.home.HomeFragment;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * NetworkUtils
 * 使用OKHttp框架进行对云端数据库链接
 * 用于网络请求数据库并接收来自数据库中的消息
 * */
public class NetworkUtils {
    private String json;
    private Context context;
    public static final MediaType JSON = MediaType.Companion.parse("application/json; charset=utf-8");

    public NetworkUtils(){}

    public NetworkUtils(String json) {
        setJson(json);
    }

    public NetworkUtils(String json, Context context) {
        this.json = json;
        this.context = context;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    public void postRequest(){
        //创建一个OkHttpClient实例
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建一个RequestBody对象
        RequestBody body = RequestBody.Companion.create(json, JSON);
        //创建一个Request对象
        Request request = new Request.Builder()
                .url("http://101.200.192.103:8080/network/process")
                .post(body)
                .build();
        new Thread(()->{
            try {
                Log.i("MSG", "线程执行中");
                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()){
                    // response.body().string()只能调用一次
                    String responseData = response.body().string();
                    Log.i("yingyingying", "打印数据" + responseData);
                    Bundle bundle = new GsonUtils().createBundle(responseData);
                    Message message = Message.obtain();
                    message.setData(bundle);
                    message.what = 7;
                    Looper.prepare();
                    HomeFragment.handler.sendMessage(message);
                    Looper.loop();
                } else {
                    Log.e("!!!ERROR!!!\n","<------ERROR------>");
                }
                if (response == null){
                    Log.e("!!!ERROR!!!", "Response is null");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("MSG", "线程结束");
        }).start();
    }

    //网络请求获取marker的经纬度
    public void postMarker(){
        //创建一个OkHttpClient实例
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建一个RequestBody对象
        RequestBody body = RequestBody.Companion.create(json, JSON);
        //创建一个Request对象
        Request request = new Request.Builder()
                .url("http://101.200.192.103:8080/network/process")
                .post(body)
                .build();
        new Thread(()->{
            try {
                Log.i("MSG", "线程执行中");
                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()){
                    // response.body().string()只能调用一次
                    String responseData = response.body().string();
                    Log.i("yingyingying", "打印数据" + responseData);
                    Message message = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("marker_data", responseData);
                    message.setData(bundle);
                    message.what = 8;
                    Looper.prepare();
                    HomeFragment.handler.sendMessage(message);
                    Looper.loop();
                } else {
                    Log.e("!!!ERROR!!!\n","<------ERROR------>");
                }
                if (response == null){
                    Log.e("!!!ERROR!!!", "Response is null");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("MSG", "线程结束");
        }).start();
    }
}
