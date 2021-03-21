package com.example.commentary.utils;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用Gson框架进行数据的包装和解析
 * */
public class GsonUtils {

    public Map<String, String> map = new HashMap<>();

    public Map<String, String> createMap(String key, String value){
        map.put(key, value);
        return map;
    }

    //创建Json字符串
    public String createJsonString(){
        return new Gson().toJson(map);
    }

    /**
     * 将传过来的数据解析成Map,并遍历Map的值将其放入Bundle中
     * @param jsonString
     * @return Bundle 用于传输数据到UI线程
     */
    public Bundle createBundle(String jsonString){
        Map<String, String> map1 = new Gson().fromJson(jsonString, new TypeToken<Map<String, String>>(){}.getType());
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : map1.entrySet()){
            bundle.putString(entry.getKey(), entry.getValue());
        }
        return bundle;
    }

}
