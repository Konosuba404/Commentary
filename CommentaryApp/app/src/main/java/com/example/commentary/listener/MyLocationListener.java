package com.example.commentary.listener;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.commentary.Const;

/**
 * 继承BDAbstractLocationListener类
 * 从onReceiveLocation中获取出经纬度
 */
public class MyLocationListener extends BDAbstractLocationListener {

    public MapView mMapView;
    public BaiduMap mBaiduMap;
    private boolean isFirstLocate = true;

    public MyLocationListener(MapView mMapView, BaiduMap mBaiduMap){
        this.mMapView = mMapView;
        this.mBaiduMap = mBaiduMap;
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        Const.LATITUDE = bdLocation.getLatitude();
        Const.LONGITUDE = bdLocation.getLongitude();
        if (bdLocation == null || mMapView == null){
            return;
        }
        navigateTo(bdLocation);
    }

    //定位自己的位置和重定向
    public void navigateTo(BDLocation location){
        if(isFirstLocate){
            LatLng now = new LatLng(location.getLatitude(),location.getLongitude());//LatLng存放经纬度
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(now);//移动到指定经纬度
            mBaiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(19f);//缩放为18f
            mBaiduMap.animateMapStatus(update);//animateMapStatus实现缩放功能
            isFirstLocate = false;
        }
        //MyLocationData.Builder用来封装设备当前所在的位置，即自己所在的位置
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locationData);
    }
}
