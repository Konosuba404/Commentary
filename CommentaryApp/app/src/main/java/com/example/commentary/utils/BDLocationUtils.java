package com.example.commentary.utils;

import android.content.Context;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.example.commentary.listener.MyLocationListener;

/**
 * 百度定位工具类
 * 实现定位相关操作
 * 开启、停止
 */
public class BDLocationUtils {
    public MapView mapView;
    public Context context;
    public BaiduMap mbaiduMap;
    public LocationClient locationClient;
    public MyLocationListener myLocationListener;

    public BDLocationUtils(Context context, MapView mapView, BaiduMap mbaiduMap) {
        this.context = context;
        this.mapView = mapView;
        this.mbaiduMap = mbaiduMap;
    }

    /**
     * 初始化定位服务端，并注册
     */
    public void startLocation(){
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        locationClient = new LocationClient(context);
        myLocationListener = new MyLocationListener(mapView, mbaiduMap);
        //初始化定位
        initLocationOption();
    }

    /**
     * 初始化定位参数配置
     */
    private void initLocationOption() {
        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener);
        //开始定位
        locationClient.start();
    }

    /**
     * 停止定位
     * 注销监听器
     * 将LocationClient暂停
     */
    public void stopLocation(){
        locationClient.unRegisterLocationListener(myLocationListener);
        locationClient.stop();
    }

}
