package com.example.commentary.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.commentary.MainActivity;
import com.example.commentary.R;
import com.example.commentary.sqlDB.MyLoginis;

import static android.content.Context.MODE_PRIVATE;


public class NotificationsFragment extends Fragment {

    View view;
    ImageButton imageButton;
    TextView login;
    TextView myStatus;
    Button user, update, call, back, exit;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        initView();
        /*if(preferences.getBoolean("statue",false)){
            Intent intent=new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }else{
            initView();
        }*/
        return view;
    }

    @SuppressLint("CommitPrefEdits")
    private void initView() {
        imageButton = view.findViewById(R.id.imageButton);
        login = view.findViewById(R.id.login);
        myStatus = view.findViewById(R.id.myStatus);
        user = view.findViewById(R.id.user);
        update = view.findViewById(R.id.update);
        call = view.findViewById(R.id.call);
        back = view.findViewById(R.id.back);
        exit = view.findViewById(R.id.exit);
        //根据在线情况显示是否在线
        login.setText(MyLoginis.getUsername());
        if (MyLoginis.getStatue()){
            myStatus.setText("在线");
        } else {
            myStatus.setText("未在线");
        }
        exit.setOnClickListener((view)->{
            preferences = getActivity().getSharedPreferences("login", MODE_PRIVATE);
            editor = preferences.edit();
            editor.putBoolean("statue", false);
            editor.apply();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
//            getActivity().finish();
        });
    }
}