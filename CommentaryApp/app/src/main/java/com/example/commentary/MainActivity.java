package com.example.commentary;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.commentary.listener.MySensorListener;
import com.example.commentary.sqlDB.MyDataBaseHelp;
import com.example.commentary.sqlDB.MyTabOperate;
import com.example.commentary.utils.BDLocationUtils;
import com.example.commentary.utils.DBUtils;
import com.example.commentary.utils.SensorUtils;

import java.lang.ref.SoftReference;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {

    BDLocationUtils bdLocationUtils;
    SensorUtils sensorUtils;
    EditText username;
    EditText password;
    Button login;
    Button register;
    ProgressBar loading;
    private SQLiteOpenHelper helper = null;
    private MyTabOperate mytab = null;
    String username1,password1,password2;
    SoftReference usernameSoft,password1Soft,password2Soft;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

/*    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Result = msg.obj.toString();
        }
    };*/

    //危险权限列表
    String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper=new MyDataBaseHelp(this);
        preferences=getSharedPreferences("login",MODE_PRIVATE);
        editor =preferences.edit();
        if (EasyPermissions.hasPermissions(this, permission)){
            if(preferences==null){
                initView();
            }else{
                if(preferences.getBoolean("statue",false)){
                    Intent intent=new Intent(MainActivity.this, MainActivity2.class);
                    startActivity(intent);
                    finish();
                }else{
                    initView();
                }
            }
        } else {
            EasyPermissions.requestPermissions(this, Const.PERMISSION_STORAGE_MSG, Const.PERMISSION_STORAGE_CODE, permission);
        }
    }

    /**
    * 初始化布局文件
     */
    private void initView(){
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        loading = findViewById(R.id.loading);

        // 为密码框设置监听事件
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                login.setEnabled(true);
                register.setEnabled(true);
            }
        });
        // 为按钮添加点击事件
        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                loading.setVisibility(View.VISIBLE);
                /*Thread thread = new Thread(()->{
                    String request = DBUtils.loginUser(username.getText().toString(), password.getText().toString());
                    Message msg = Message.obtain();
                    msg.obj = request;
                    Looper.prepare();
                    handler.sendMessage(msg);
                    Looper.loop();
                });
                thread.start();
                try {
                    thread.join();
                    Toast.makeText(this, Result, Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                new Thread(()->{
                    //使用软引用
                    usernameSoft = new SoftReference(username.getText().toString());
                    password1Soft = new SoftReference(password.getText().toString());

                    //获取软引用中的实例
                    username1 = (String)usernameSoft.get();
                    password1 = (String)password1Soft.get();

                    //用游标cursor来保存读取出来的数据
                    Cursor cursor=helper.getReadableDatabase().rawQuery("select * from User where username=?",new String[]{
                            username1
                    });
                    //判断cursor里面是否有数据，如果没有则说明账号不存在，如果有再判断密码正不正确
                    if(cursor.getCount()!=0) {
                        while(cursor.moveToNext()){
                            password2Soft = new SoftReference(cursor.getString(cursor.getColumnIndex("password")));
                            password2 = (String)password2Soft.get();
                        }
                        if(password1.equals(password2)){
                            editor.putString("username", username1);
                            editor.putBoolean("statue", true);
                            /*
                             * 登录成功
                             * */
                            editor.apply();
                            cursor.close();
                            Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                            startActivity(intent);
                        }else{
                            runOnUiThread(()->{
                                Toast.makeText(this,"密码错误",Toast.LENGTH_LONG).show();
                                password.setText("");
                            });
                        }
                    }else if(cursor.getCount()==0){
                        runOnUiThread(()->{
                            Toast.makeText(this,"未注册",Toast.LENGTH_LONG).show();
                        });
                    }
                    cursor.close();
                }).start();
                break;
            case R.id.register:
                /*loading.setVisibility(View.VISIBLE);
                Thread thread1 = new Thread(()->{
                    String request = DBUtils.registerUser(username.getText().toString(), password.getText().toString());
                    Message msg = Message.obtain();
                    msg.obj = request;
                    Looper.prepare();
                    handler.sendMessage(msg);
                    Looper.loop();
                });
                thread1.start();
                try {
                    thread1.join();
                    Toast.makeText(this, Result, Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                new Thread(()->{
                    mytab = new MyTabOperate(helper.getWritableDatabase());
                    mytab.insert(username.getText().toString(), password.getText().toString());
                    runOnUiThread(()->{
                        Toast.makeText(this,"注册成功",Toast.LENGTH_LONG).show();
                    });
                    editor.clear();
                }).start();
                break;
        }
    }


    //处理权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
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
    }
}