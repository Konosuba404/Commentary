package com.example.commentary.ui.home;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;


import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.commentary.Const;
import com.example.commentary.DBIntentService;
import com.example.commentary.MainActivity2;
import com.example.commentary.R;
import com.example.commentary.databinding.FragmentHomeBinding;
import com.example.commentary.listener.MySensorListener;
import com.example.commentary.ui.MyDialogFragment;
import com.example.commentary.utils.BDLocationUtils;
import com.example.commentary.utils.BDSpeakerUtils;
import com.example.commentary.utils.DBUtils;
import com.example.commentary.utils.GsonUtils;
import com.example.commentary.utils.NetworkUtils;
import com.example.commentary.utils.SensorUtils;
import com.example.commentary.utils.WalkOverlayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;


public class HomeFragment extends Fragment implements View.OnClickListener {


    BDLocationUtils bdLocationUtils;
    SensorUtils sensorUtils;
    GsonUtils gsonUtils;
    private MapView mapView = null;
    static BaiduMap mBaiduMap;
    ArrayList<HashMap<String, Object>> data_list = new ArrayList<>();
    static Boolean FLAG = true;
    static Boolean Marker_flag = true;
    BDSpeakerUtils bdSpeakerUtils;
    static String RequestSpeaker = "";
    static Bundle bundle;
    FragmentHomeBinding binding;
    private static String str;

    //用于接收网络请求云端数据库中的结果
    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 7) {
                bundle = msg.getData();
                Log.i("yingyingying", "handleMessage: "+ bundle.get("inf").toString());
                RequestSpeaker = bundle.get("inf").toString();
                Log.e("yingyingying", "handleMessage: " + RequestSpeaker);
            }else if (msg.what == 8 ) {
                bundle = msg.getData();
                if (bundle.get("marker_data").toString().equals("null")) {
                    str = "没有数据";
                }
                if (bundle != null) {
                    List<Map<String, String>> dataSet =  GsonUtils.listToString((String) bundle.get("marker_data"));
                    if (dataSet.get(0).containsKey("inf")) {
                        str = dataSet.get(0).get("inf");
                    } else {
                        batchMarkerFromNetWork(dataSet);
                        str = "初始化成功";
                    }
                }

                Log.e("yingyingying", "handleMessage: "+ str);
                Log.i("yingyingying", "handleMessage: "+ bundle.get("marker_data").toString());
            }
        }
    };


    //初始化视图
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }


    // 初始化控件、工具类、注册感应器
    public void initView(){
        //map实例
        mapView = binding.bmapView;
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        //为按钮添加点击事件
        binding.includeXml.menuButton.setOnClickListener(this);
        binding.includeXml.locationButton.setOnClickListener(this);
        binding.includeXml.speakButton.setOnClickListener(this);
        binding.includeXml.relocationButton.setOnClickListener(this);
        binding.includeXml.markerButton.setOnClickListener(this);

        //初始化定位辅助类
        bdLocationUtils = new BDLocationUtils(getActivity(), mapView, mBaiduMap);
        //开始定位
        bdLocationUtils.startLocation();

        //从数据库中读取出经纬度生成Marker
        batchMarker();

        //为marker添加点击事件，即获得它们之间的距离
        mBaiduMap.setOnMarkerClickListener((marker)->{
            LatLng now_position = new LatLng(Const.LATITUDE, Const.LONGITUDE);
            LatLng marker_position = marker.getPosition();
            Bundle bundle2 = marker.getExtraInfo();
            String address = (String) bundle2.get("address");
            String description = (String) bundle2.get("description");
            MyDialogFragment myDialogFragment = new MyDialogFragment(now_position, marker_position, address, description, mBaiduMap);
            myDialogFragment.show(getParentFragmentManager(), "dialog");
            return false;
        });

        //初始化感应器辅助类
        sensorUtils = SensorUtils.getInstance(getActivity(), new MySensorListener());
        //注册感应器
//        sensorUtils.register();

        //初始化Gson辅助类
        gsonUtils = new GsonUtils();

        //初始化BDSpeakerUtils
        bdSpeakerUtils = new BDSpeakerUtils(getActivity());
    }

    //批量生成Marker
    public void batchMarker(){
        new Thread(()->{
            data_list = DBUtils.getData();
            Log.e("data_list", "batchMarker: "+data_list.toString() + "size:" + data_list.size());

            //创建OverlayOptions的集合
            List<OverlayOptions> options = new ArrayList<OverlayOptions>();

            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.mipmap.icon_openmap_mark);

            //遍历出数据库中传过来的经纬度，批量生成point和option，并添加到options中
            for (HashMap<String, Object> map_item : data_list){
                //构建bundle用于存储数据
                Bundle bundle1 = new Bundle();
                bundle1.putString("description", (String) map_item.get("description"));
                bundle1.putString("address", (String) map_item.get("address"));

                LatLng point = new LatLng((Float)map_item.get("lat"), (Float)map_item.get("lng"));
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap)
                        .extraInfo(bundle1);
                options.add(option);
            }
            Log.e("options", "batchMarker: "+ options.size());
            mBaiduMap.addOverlays(options);
        }).start();
    }

    //批量生成当前方向的Marker
    public static void batchMarkerFromNetWork(List<Map<String, String>> dataSet){
        new Thread(()->{

            //创建OverlayOptions的集合
            List<OverlayOptions> options = new ArrayList<OverlayOptions>();


            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_openmap_focuse_mark);

            //遍历出数据库中传过来的经纬度，批量生成point和option，并添加到options中

            dataSet.forEach(item -> {
                float lat = Float.parseFloat(item.get("latitude"));
                float lng = Float.parseFloat(item.get("longitude"));
                LatLng point = new LatLng(lat, lng);
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap);
                options.add(option);
            });
            Log.e("options", "batchMarker: "+ options.size());
            mBaiduMap.addOverlays(options);
        }).start();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        sensorUtils.register();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorUtils.unregister();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        bdSpeakerUtils.stop();
        bdLocationUtils.stopLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        bdSpeakerUtils.onDestroy();
        bdLocationUtils.stopLocation();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //点击方法
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //加载RelativeLayout布局
            case R.id.menu_button:
                Log.e("menu_button", "onClick: 中" );
                if (FLAG){
                    binding.includeXml.pathRelative.setVisibility(View.VISIBLE);
                    FLAG = false;
                }else {
                    binding.includeXml.pathRelative.setVisibility(View.INVISIBLE);
                    FLAG = true;
                }
                break;

            //用于请求网络获取到当前对象所面对的景观
            case R.id.location_button:
                //创建线程，在线程中获取改变的值并输出
                 new Thread(()->{
                    try {
                        String string = "纬度：" + Const.LATITUDE + "\n经度：" + Const.LONGITUDE + "\n方向：" + Const.DIRECTION;
                        gsonUtils.createMap("Latitude", String.valueOf(Const.LATITUDE));
                        gsonUtils.createMap("Longitude", String.valueOf(Const.LONGITUDE));
                        gsonUtils.createMap("Direction", String.valueOf(Const.DIRECTION));
                        gsonUtils.createMap("Flag", "1");
                        Log.i("yingyingyingyingyingying", "initView: " + string);
                        Log.i("MAP", "initView: " + gsonUtils.map.toString());
                        new NetworkUtils(gsonUtils.createJsonString(), getActivity()).postRequest();
                        Log.i("yingyingyingyingyingying", "initView: " + new NetworkUtils(gsonUtils.createJsonString()).toString());
                        //用于延迟等待消息被handler接收到
                        Thread.sleep(1000);
                        //使用runOnUiThread()方法来更改UI
                        getActivity().runOnUiThread(()->{
                            Toast.makeText(getActivity(), RequestSpeaker, Toast.LENGTH_SHORT).show();
                            //创建通知栏管理工具
                            NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(NOTIFICATION_SERVICE);
                            //设置点击跳转事件
                            Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity2.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent, 0);
                            //实例化通知栏构造器
                            Notification mBuilder = new NotificationCompat.Builder(getActivity().getApplicationContext())
                                    .setContentTitle("您当前位于")
                                    .setContentText(RequestSpeaker)
                                    //设置大图标
                                    .setLargeIcon(BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.mipmap.ic_launcher))
                                    //设置小图标
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setWhen(System.currentTimeMillis())
                                    .setContentIntent(pendingIntent)
                                    .setChannelId(getActivity().getPackageName())
                                    .build();
                            NotificationChannel channel = new NotificationChannel(
                                    getActivity().getApplicationContext().getPackageName(),
                                    "会话消息",
                                    NotificationManager.IMPORTANCE_DEFAULT
                            );
                            notificationManager.createNotificationChannel(channel);
                            //发送通知请求
                            notificationManager.notify(1, mBuilder);
                        });
                        bdSpeakerUtils.speak(RequestSpeaker);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                break;

            //清除路径规划，重新加载Mark点
            case R.id.speak_button:
                //将对象释放掉，清空地图上的marker，重新加载并缩放
//                WalkOverlayUtils.destory();
                mBaiduMap.clear();
                batchMarker();
                MapStatusUpdate updateMapZoom = MapStatusUpdateFactory.zoomTo(19f);
                mBaiduMap.animateMapStatus(updateMapZoom);
                break;

            //用于重新定位
            case R.id.relocation_button:
                LatLng now = new LatLng(Const.LATITUDE, Const.LONGITUDE);//LatLng存放经纬度
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(now);//移动到指定经纬度
                mBaiduMap.animateMapStatus(update);
                update = MapStatusUpdateFactory.zoomTo(19f);//缩放为18f
                mBaiduMap.animateMapStatus(update);//animateMapStatus实现缩放功能
                break;

            //用于遍历出面前的景观用不同的marker标记出
            case R.id.marker_button:
                if (Marker_flag){
                    new Thread(()->{
                        try {
                            String string = "纬度：" + Const.LATITUDE + "\n经度：" + Const.LONGITUDE + "\n方向：" + Const.DIRECTION;
                            gsonUtils.createMap("Latitude", String.valueOf(Const.LATITUDE));
                            gsonUtils.createMap("Longitude", String.valueOf(Const.LONGITUDE));
                            gsonUtils.createMap("Direction", String.valueOf(Const.DIRECTION));
                            gsonUtils.createMap("Flag", "marker");
                            Log.i("yingyingyingyingyingying", "marker_dataSet: " + string);
                            Log.i("MAP", "marker_dataSet: " + gsonUtils.map.toString());
                            new NetworkUtils(gsonUtils.createJsonString(), getActivity()).postMarker();
                            Log.i("yingyingyingyingyingying", "marker_dataSet: " + new NetworkUtils(gsonUtils.createJsonString()).toString());
                            //用于延迟等待消息被handler接收到
                            Thread.sleep(1000);
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    Marker_flag = false;
                } else {
                    mBaiduMap.clear();
                    batchMarker();
                    MapStatusUpdate updateMapZoom1 = MapStatusUpdateFactory.zoomTo(19f);
                    mBaiduMap.animateMapStatus(updateMapZoom1);
                    Marker_flag = true;
                }
                break;
            default:
                Log.e("悬浮按钮", "onClick: 操作失败");
        }
    }
}