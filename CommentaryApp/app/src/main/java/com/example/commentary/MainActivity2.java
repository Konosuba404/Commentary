package com.example.commentary;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.commentary.listener.MySensorListener;
import com.example.commentary.sqlDB.MyLoginis;
import com.example.commentary.ui.home.HomeFragment;
import com.example.commentary.utils.BDLocationUtils;
import com.example.commentary.utils.SensorUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.VIBRATE;

public class MainActivity2 extends AppCompatActivity{

    /*BDLocationUtils bdLocationUtils;
    SensorUtils sensorUtils;
    static Bundle bundle = new Bundle();

    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 7){
                bundle = msg.getData();
                Log.i("yingyingying", "handleMessage: "+ bundle.get("inf").toString());
//                textView.setText(bundle.get("Direction").toString());
//                textView.setText(bundle.get("inf").toString());
            }
        }
    };*/

//    String value;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        preferences = getSharedPreferences("login",MODE_PRIVATE);
        //危险权限数组
        /*String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.CHANGE_WIFI_STATE,
//                Manifest.permission.VIBRATE
        };*/

        /*String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
        };

        //使用EasyPermissions框架判断危险权限是否申请，是的话则初始化视图，否则申请权限
        if (EasyPermissions.hasPermissions(this, permission)){
            if(preferences.getBoolean("statue",false)){
                Intent intent=new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
            }else{
                initView();
            }
            initView();
        } else {
            EasyPermissions.requestPermissions(this, Const.PERMISSION_STORAGE_MSG, Const.PERMISSION_STORAGE_CODE, permission);
        }*/
        if(preferences.getBoolean("statue",false)){
            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }else{
            initView();
        }
        initView();
    }

    //初始化视图
    public void initView(){
        /*//初始化定位辅助类
        bdLocationUtils = new BDLocationUtils(this);
        //开始定位
        bdLocationUtils.startLocation();
        //初始化感应器辅助类
        sensorUtils = SensorUtils.getInstance(this, new MySensorListener());
        //注册感应器
        sensorUtils.register();*/
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,  R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        new MyLoginis(preferences.getBoolean("statue", false), preferences.getString("username", "欢迎"));
        /*//创建通知栏管理工具
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //实例化通知栏构造器
        Notification mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("您当前位于")
                .setContentText(value)
                //设置大图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setWhen(System.currentTimeMillis())
                .build();
        //发送通知请求
        notificationManager.notify(1, mBuilder);*/
    }

    /*//处理权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //请求权限成功
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        initView();
        Toast.makeText(this, "用户授权成功", Toast.LENGTH_SHORT).show();
    }

    //请求权限失败
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "用户授权失败", Toast.LENGTH_SHORT).show();
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //当从软件设置界面，返回当前程序时候
            case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:
                Toast.makeText(this, "权限赋予成功", Toast.LENGTH_SHORT).show();
                //执行Toast显示或者其他逻辑处理操作
                break;
        }
    }*/

//    @Override
//    public void sendValue(String value) {
////        this.value = value;
//        //创建通知栏管理工具
//        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        //实例化通知栏构造器
//        Notification mBuilder = new NotificationCompat.Builder(this)
//                .setContentTitle("您当前位于")
//                .setContentText(value)
//                //设置大图标
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                //设置小图标
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setWhen(System.currentTimeMillis())
//                .build();
//        //发送通知请求
//        notificationManager.notify(1, mBuilder);
//    }



    /*//向fragment中传递参数
    public String getInfo(){
//        Log.e("bundle_info", "getInfo: " + bundle.get("inf").toString() );
        return bundle.get("inf").toString();
    }*/

    /*@Override
    protected void onRestart() {
        super.onRestart();
        //初始化感应器辅助类
        sensorUtils = SensorUtils.getInstance(this, new MySensorListener());
        //注册感应器
        sensorUtils.register();
    }

    @Override
    protected void onPause() {
        sensorUtils.unregister();
        super.onPause();
    }

    @Override
    protected void onStop() {
        sensorUtils.unregister();
        bdLocationUtils.stopLocation();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }*/

}