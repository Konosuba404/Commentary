package com.example.commentary.utils;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import androidx.fragment.app.FragmentActivity;

import com.example.commentary.MainActivity;
import com.example.commentary.listener.MySensorListener;

import java.lang.ref.WeakReference;

/**
 * 感应器相关工具类
 * 实现SensorManager的获取
 * 注册监听器
 * 注销监听器
 */
public class SensorUtils {
    private FragmentActivity context;
    private static SensorUtils sensorUtils;
    private WeakReference<FragmentActivity> mContext;
    private SensorManager sensorManager;
    private MySensorListener listener;

    //使用单例模式保证只有一个单例
    private SensorUtils(FragmentActivity context, MySensorListener listener){
        this.context = context;
        this.listener = listener;
        initData(context);
    }

    //单例模式懒汉式
    public static SensorUtils getInstance(FragmentActivity context, MySensorListener listener){
        if (sensorUtils == null){
            synchronized (SensorUtils.class){
                if (sensorUtils == null){
                    sensorUtils = new SensorUtils(context, listener);
                }
            }
        }
        return sensorUtils;
    }

    //弱引用，避免ANR
    private void initData(FragmentActivity context){
        this.mContext = new WeakReference<>(context);
        if (mContext.get() != null){
            sensorManager = (SensorManager)(mContext.get().getSystemService(Context.SENSOR_SERVICE));
        }
    }

    public void register(){
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    public void unregister(){
        sensorManager.unregisterListener(listener);
    }
}
