package com.example.commentary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.commentary.overlayutil.WalkingRouteOverlay;

//步行路径规划
public class WalkOverlayUtils {
    private Context context;
    private BaiduMap mBaiduMap;
    static RoutePlanSearch routePlanSearch;
    public RouteLine routeLine;

    @SuppressLint("StaticFieldLeak")
    private volatile static WalkOverlayUtils walkOverlayUtils;
    private WalkOverlayUtils(Context context, BaiduMap mBaiduMap) {
        this.context = context;
        this.mBaiduMap = mBaiduMap;
        initRoutePlan();
    }
    //单例懒汉式
    public static WalkOverlayUtils getWalkOverlayUtils(Context context, BaiduMap mBaiduMap) {
        if (walkOverlayUtils == null) {
            synchronized (WalkOverlayUtils.class) {
                if (walkOverlayUtils == null) {
                    walkOverlayUtils = new WalkOverlayUtils(context, mBaiduMap);
                }
            }
        }
        return walkOverlayUtils;
    }
    //初始化路径规划
    private void initRoutePlan(){
        routePlanSearch = RoutePlanSearch.newInstance();//路线规划对象
        //给路线规划添加监听
        routePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            //步行路线结果回调
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
//                mBaiduMap.clear();
                if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    routeLine = walkingRouteResult.getRouteLines().get(0);
                    WalkingRouteOverlay walkingOverlay = new WalkingRouteOverlay(mBaiduMap);
                    walkingOverlay.setData((WalkingRouteLine) routeLine);// 设置一条路线方案
                    walkingOverlay.addToMap();
                    walkingOverlay.zoomToSpan();
                    mBaiduMap.setOnMarkerClickListener(walkingOverlay);
                } else {
                    Toast.makeText(context, "搜不到！", Toast.LENGTH_SHORT).show();
                }
            }

            //换乘线结果回调
            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
            }

            //跨城公共交通路线结果回调
            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
            }

            //驾车路线结果回调
            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            }


            //室内路线规划回调
            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
            }

            // 骑行路线结果回调
            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
            }
        });
    }
    //距离+时间
    public String updateRouteOverViewCard() {
        String totalTime = "";
        String totalDistance = "";
        int time = routeLine.getDuration();
        if (time / 3600 == 0) {
            totalTime = time / 60 + "分钟";
        } else {
            totalTime = time / 3600 + "小时" + (time % 3600) / 60 + "分钟";
        }

        int distance = routeLine.getDistance();
        if (distance / 1000 == 0) {
            totalDistance = distance + "米";
        } else {
            totalDistance = String.format("%.1f", distance / 1000f) + "公里";
        }
        return "据您" + totalDistance + "大概" + totalTime;
    }

    //步行路径规划
    public void selectWalkingLine(LatLng now_position, LatLng mark_position) {
        //创建步行路线搜索对象
        WalkingRoutePlanOption walkingSearch = new WalkingRoutePlanOption();
        //设置节点对象，可以通过城市+关键字或者使用经纬度对象来设置
        PlanNode fromeNode = PlanNode.withLocation(now_position);
        PlanNode toNode = PlanNode.withLocation(mark_position);
        walkingSearch.from(fromeNode).to(toNode);
        routePlanSearch.walkingSearch(walkingSearch);
    }
    //销毁
    public static void destory(){
        routePlanSearch.destroy();
    }
}
