package com.example.commentary.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.commentary.MainActivity;
import com.example.commentary.databinding.FragmentNotificationsBinding;
import com.example.commentary.sqlDB.MyLoginis;
import com.example.commentary.utils.CustomAdapter;

import static android.content.Context.MODE_PRIVATE;


public class NotificationsFragment extends Fragment {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    @SuppressLint("CommitPrefEdits")
    private void initView() {
        //根据在线情况显示是否在线
        binding.login.setText(MyLoginis.getUsername());
        if (MyLoginis.getStatue()){
            binding.myStatus.setText("在线");
        } else {
            binding.myStatus.setText("未在线");
        }
        binding.exit.setOnClickListener((view)->{
            preferences = getActivity().getSharedPreferences("login", MODE_PRIVATE);
            editor = preferences.edit();
            editor.putBoolean("statue", false);
            editor.apply();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });
        //准备数据集
        String[] dataSet = {"使用说明", "更新日记", "联系我们", "意见反馈"};
        //初始化recyclerView
        RecyclerView recyclerView = binding.myRecyclerView;
        //初始化并注册布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        //初始化并注册适配器
        CustomAdapter customAdapter = new CustomAdapter(dataSet);
        recyclerView.setAdapter(customAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}