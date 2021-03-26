package com.example.commentary.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.example.commentary.Const;

/**
 * 继承SensorEventListener
 * 对传感器方向进行解析
 * */
public class MySensorListener implements SensorEventListener {
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION){
            int degree = (int)event.values[0];
            Const.DEGREE = degree;
            if (degree >= 68 && degree <= 112){
                Const.DIRECTION = "东：" + degree;
            }else if (degree >= 248 && degree <= 292){
                Const.DIRECTION = "西：" + degree;
            }else if (degree >= 158 && degree <= 202){
                Const.DIRECTION = "南：" + degree;
            }else if ((degree >= 0 && degree <= 22 ) || (degree > 338)){
                Const.DIRECTION = "北：" + degree;
            }else if (degree >= 23 && degree <= 67){
                Const.DIRECTION = "东北：" + degree;
            }else if (degree >= 123 && degree <= 157){
                Const.DIRECTION = "东南：" + degree;
            }else if (degree >= 203 && degree <= 247){
                Const.DIRECTION = "西南：" + degree;
            }else if (degree >= 293 && degree <= 337){
                Const.DIRECTION = "西北：" + degree;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
