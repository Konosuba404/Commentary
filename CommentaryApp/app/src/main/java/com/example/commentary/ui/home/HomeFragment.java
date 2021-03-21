package com.example.commentary.ui.home;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;


import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.commentary.Const;
import com.example.commentary.MainActivity;
import com.example.commentary.MainActivity2;
import com.example.commentary.R;
import com.example.commentary.listener.MySensorListener;
import com.example.commentary.ui.MyDialogFragment;
import com.example.commentary.utils.BDLocationUtils;
import com.example.commentary.utils.BDSpeakerUtils;
import com.example.commentary.utils.DBUtils;
import com.example.commentary.utils.GsonUtils;
import com.example.commentary.utils.NetworkUtils;
import com.example.commentary.utils.SensorUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;


public class HomeFragment extends Fragment implements View.OnClickListener {


    FloatingActionButton menu_button, location_button, speak_button, relocation_button;
    BDLocationUtils bdLocationUtils;
    SensorUtils sensorUtils;
    GsonUtils gsonUtils;
    View view;
    private MapView mapView = null;
    BaiduMap mBaiduMap;
    ArrayList<HashMap<String, Object>> data_list = new ArrayList<>();
    static Boolean FLAG = true;
    BDSpeakerUtils bdSpeakerUtils;
    static String RequestSpeaker = "";
    static Bundle bundle;
//    boolean flag = false;

    //用于接收网络请求云端数据库中的结果
    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 7){
                bundle = msg.getData();
                Log.i("yingyingying", "handleMessage: "+ bundle.get("inf").toString());
                RequestSpeaker = bundle.get("inf").toString();
                Log.e("yingyingying", "handleMessage: " + RequestSpeaker);
//                textView.setText(bundle.get("Direction").toString());
//                textView.setText(bundle.get("inf").toString());
//                Toast.makeText(this, RequestSpeaker, Toast.LENGTH_SHORT).show();
            }
        }
    };


    //初始化视图
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        /*if(preferences.getBoolean("statue",false)){
            Intent intent=new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }else{
            initView();
        }*/
        initView();
        return view;
    }


    // 初始化控件、工具类、注册感应器
    public void initView(){
        //map实例
        mapView = view.findViewById(R.id.bmapView);
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        //加载RelativeLayout布局
        menu_button = view.findViewById(R.id.menu_button);
        //用于请求网络获取到当前对象所面对的景观
        location_button = view.findViewById(R.id.location_button);
        //用于请求百度语音
        speak_button = view.findViewById(R.id.speak_button);
        //用于重新定位
        relocation_button = view.findViewById(R.id.relocation_button);

        //为按钮添加点击事件
        menu_button.setOnClickListener(this);
        location_button.setOnClickListener(this);
        speak_button.setOnClickListener(this);
        relocation_button.setOnClickListener(this);

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
            Log.e("now_position", "initView: " + Const.LATITUDE + "," + Const.LONGITUDE );
            Log.e("marker", "initView: " + marker_position.toString() );
            MyDialogFragment myDialogFragment = new MyDialogFragment(now_position, marker_position);
            myDialogFragment.show(getParentFragmentManager(), "dialog");
            return false;
        });

        //初始化感应器辅助类
        sensorUtils = SensorUtils.getInstance(getActivity(), new MySensorListener());
        //注册感应器
        sensorUtils.register();

        //初始化Gson辅助类
        gsonUtils = new GsonUtils();

        //初始化BDSpeakerUtils
        bdSpeakerUtils = new BDSpeakerUtils(getActivity());

        //创建线程，在线程中获取改变的值并输出
        /*button.setOnClickListener((view)->{
            new Thread(()->{
//                while (true){
                try {
                    Thread.sleep(1000);
                    String string = "纬度：" + Const.LATITUDE + "\n经度：" + Const.LONGITUDE + "\n方向：" + Const.DIRECTION;
                    gsonUtils.createMap("Latitude", String.valueOf(Const.LATITUDE));
                    gsonUtils.createMap("Longitude", String.valueOf(Const.LONGITUDE));
                    gsonUtils.createMap("Direction", String.valueOf(Const.DIRECTION));
                    gsonUtils.createMap("Flag", "1");
                    Log.i("yingyingyingyingyingying", "initView: " + string);
                    Log.i("MAP", "initView: " + gsonUtils.map.toString());
                    new NetworkUtils(gsonUtils.createJsonString(), getActivity()).postRequest();
                    Log.i("yingyingyingyingyingying", "initView: " + new NetworkUtils(gsonUtils.createJsonString()).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });*/


    }

    //批量生成Marker
    public void batchMarker(){
        new Thread(()->{
            data_list = DBUtils.getData();
            Log.e("data_list", "batchMarker: "+data_list.toString() + "size:" + data_list.size());

            Log.e("data_list2", "batchMarker: "+data_list.toString() + "size:" + data_list.size());
            //创建OverlayOptions的集合
            List<OverlayOptions> options = new ArrayList<OverlayOptions>();

            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.mipmap.icon_openmap_mark);

            //遍历出数据库中传过来的经纬度，批量生成point和option，并添加到options中
            for (HashMap<String, Object> map_item : data_list){
                LatLng point = new LatLng((Float)map_item.get("lat"), (Float)map_item.get("lng"));

/*                //用来构造InfoWindow的Button
                Button button = new Button(getActivity().getApplicationContext());
//                button.setBackgroundResource(R.drawable.popup);
                button.setText((String)map_item.get("address"));
//                button.setVisibility(View.INVISIBLE);
                //构造InfoWindow
                //point 描述的位置点
                //-100 InfoWindow相对于point在y轴的偏移量
                InfoWindow mInfoWindow = new InfoWindow(button, point, -100);
                mBaiduMap.showInfoWindow(mInfoWindow);*/

                OverlayOptions option = new MarkerOptions()
                        .position(point)
//                        .infoWindow(mInfoWindow)
                        .icon(bitmap);
                options.add(option);
            }
            Log.e("options", "batchMarker: "+ options.size());
            mBaiduMap.addOverlays(options);
        }).start();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        TEXT = ((MainActivity2)context).getInfo();
        //为引用赋值,向Activity传值实现通知栏功能
        /*myListener = (MyListener) getActivity();
        myListener.sendValue(RequestSpeaker);*/
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
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
        sensorUtils.unregister();
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

    //点击方法
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //加载RelativeLayout布局
            case R.id.menu_button:
                Log.e("menu_button", "onClick: 中" );
                if (FLAG){
                    view.findViewById(R.id.pathRelative).setVisibility(View.VISIBLE);
                    FLAG = false;
                }else {
                    view.findViewById(R.id.pathRelative).setVisibility(View.INVISIBLE);
                    FLAG = true;
                }
                break;

            //用于请求网络获取到当前对象所面对的景观
            case R.id.location_button:
                //创建线程，在线程中获取改变的值并输出
                 new Thread(()->{
                    try {
                        Thread.sleep(1000);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                /*myListener = (MyListener) getActivity();
                myListener.sendValue(RequestSpeaker);*/
                break;

            //用于请求百度语音
            case R.id.speak_button:
                //请求百度语音
                bdSpeakerUtils.speak(RequestSpeaker);
                break;

            //用于重新定位
            case R.id.relocation_button:
                LatLng now = new LatLng(Const.LATITUDE, Const.LONGITUDE);//LatLng存放经纬度
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(now);//移动到指定经纬度
                mBaiduMap.animateMapStatus(update);
                update = MapStatusUpdateFactory.zoomTo(19f);//缩放为18f
                mBaiduMap.animateMapStatus(update);//animateMapStatus实现缩放功能
                break;

            default:
                Log.e("悬浮按钮", "onClick: 操作失败");
        }
    }

    /*//向Activity传值
    public interface MyListener{
        void sendValue(String value);
    }*/
}