package com.example.commentary;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.commentary.databinding.ActivityMainBinding;

import com.example.commentary.sqlRoom.AppDatabase;
import com.example.commentary.sqlRoom.User;
import com.example.commentary.utils.DBUtils;

import com.example.commentary.utils.SecurityUtils;

import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ActivityMainBinding binding;
    AppDatabase db;

    static List<Map<String, Object>> mapList = null;

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
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        helper = new MyDataBaseHelp(this);
        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = preferences.edit();
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
        // 为密码框设置监听事件
        binding.password.setOnFocusChangeListener((v, hasFocus) -> {
            binding.login.setEnabled(true);
            binding.register.setEnabled(true);
        });
        // 为按钮添加点击事件
        binding.login.setOnClickListener(this);
        binding.register.setOnClickListener(this);
        // 初始化Room
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Commentary.db3").build();
        // 启动异步，获取数据库中的值
        AsyncUtils asyncUtils = new AsyncUtils();
        asyncUtils.execute();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                binding.loading.setVisibility(View.VISIBLE);
                new Thread(()->{
                    if (!db.getUserDao().getAll().isEmpty()) {
                        if (db.getUserDao().loadPasswordWhereUsername(binding.username.getText().toString()).equals(binding.password.getText().toString())) {
                            editor.putString("username", binding.username.getText().toString());
                            editor.putBoolean("statue", true);
                            editor.apply();
                            Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                            intent.putExtra("status", "200");
                            startActivity(intent);
                            finish();
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        mapList.forEach(item -> {
                            if (item.get("username").equals(binding.username.getText().toString())) {
                                if (item.get("password").equals(SecurityUtils.getResult(binding.password.getText().toString()))) {
                                    editor.putString("username", binding.username.getText().toString());
                                    editor.putBoolean("statue", true);
                                    editor.apply();
                                    // 将输入框中的值添加到本地缓存中
                                    User user = new User();
                                    user.username = binding.username.getText().toString();
                                    user.password = binding.password.getText().toString();
                                    db.getUserDao().insertAll(user);
                                    Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                                    intent.putExtra("status", "200");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    runOnUiThread(() -> {
                                        Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "当前用户未注册", Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    }
                }).start();
                break;
            case R.id.register:
                new Thread(() -> {
                    for (Map<String, Object> item : mapList) {
                        if (item.get("username").equals(binding.username.getText().toString())) {
                            runOnUiThread(() -> {
                                Toast.makeText(this,"当前用户存在",Toast.LENGTH_SHORT).show();
                            });
                            return;
                        }
                    }
                    User user = new User();
                    user.username = binding.username.getText().toString();
                    user.password = binding.password.getText().toString();
                    db.getUserDao().insertAll(user);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                    });
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

    /**
     * 处理权限
     * */
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

    private static class AsyncUtils extends AsyncTask<Void, Void, List<Map<String, Object>>> {

        @Override
        protected List<Map<String, Object>> doInBackground(Void... voids) {
            return DBUtils.loginUser();
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> maps) {
            super.onPostExecute(maps);
            mapList = maps;
        }
    }
}