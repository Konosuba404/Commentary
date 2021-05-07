package com.example.commentary;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.room.Room;

import com.example.commentary.retrofitUtil.UserService;
import com.example.commentary.sqlRoom.AppDatabase;
import com.example.commentary.sqlRoom.User;
import com.example.commentary.utils.SecurityUtils;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DBIntentService extends IntentService {

    public DBIntentService() {
        super("DBIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"Commentary.db3").build();
        List<User> list =  db.getUserDao().getAll();
        com.example.commentary.retrofitUtil.User user = new com.example.commentary.retrofitUtil.User();
        list.forEach(item -> {
            user.setUsername(item.username);
            user.setPassword(SecurityUtils.getResult(item.password));
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://101.200.192.103:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UserService userService = retrofit.create(UserService.class);
        Call<ResponseBody> responseBodyCall = userService.getPostDataBody(user);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("----------检测----------", "onResponse: " + "username:" + user.getUsername() + "password:" + user.getPassword() );
                Log.e("----------回调----------", "onResponse: 成功" );
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("----------回调----------", "onResponse: 失败" );
            }
        });
    }

}