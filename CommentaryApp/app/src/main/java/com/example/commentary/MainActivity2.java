package com.example.commentary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


import com.example.commentary.databinding.ActivityMain2Binding;
import com.example.commentary.utils.WalkOverlayUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;


public class MainActivity2 extends AppCompatActivity{

    SharedPreferences preferences;
    ActivityMain2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        if(preferences.getBoolean("statue",false)){
            initView();
        }else{
            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        initView();
    }

    //初始化视图
    public void initView(){
        BottomNavigationView navView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,  R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        if (Objects.equals(getIntent().getStringExtra("status"), "200")) {
            //初始化服务
            Intent intent = new Intent(this, DBIntentService.class);
            startService(intent);
            Log.e("------------Get Data------------", "initView: Success" );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WalkOverlayUtils.destory();
    }
}