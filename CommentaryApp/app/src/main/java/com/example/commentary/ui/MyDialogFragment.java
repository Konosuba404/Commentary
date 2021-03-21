package com.example.commentary.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.baidu.mapapi.model.LatLng;

import static com.baidu.mapapi.utils.DistanceUtil.getDistance;

public class MyDialogFragment extends DialogFragment {

    private final LatLng now_position;
    private final LatLng marker_position;
    private final String address;
    private final String description;


    public MyDialogFragment(LatLng now_position, LatLng marker_position, String address, String description){
        this.now_position = now_position;
        this.marker_position = marker_position;
        this.address = address;
        this.description = description;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("您是否前往当前位置")
                .setMessage(getTwoPointDistance())
                .setPositiveButton("确定", (dialog12, which) -> dismiss())
                .setNegativeButton("取消", (dialog1, which) -> dismiss())
                .create();

        return dialog;
    }

    @SuppressLint("DefaultLocale")
    public String getTwoPointDistance(){
        double temp_location = getDistance(now_position, marker_position);
        String distance = address + "据您" + description;
        if(temp_location<500){
            distance = distance+String.format("%6.0f",temp_location)+"米";
        }else{
            distance = distance+String.format("%.2f",(temp_location)/1000)+"公里";
        }
        return  distance;
    }

}
