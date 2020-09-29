package com.example.wtwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    private boolean gotoTwoPhase = true;
    private double numberAcceleration = 0;
    private double minSAcceleration = 19.6;
    private double maxSAcceleration = -19.6;
    private double smvFF = 7.5;
    private double smvIP = 18.5;
    TextView fallText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        fallText = findViewById(R.id.fall);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            Log.i("x", "" + x);
            Log.i("y", "" + y);
            Log.i("x", "" + z);
            double smv = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)
                    + Math.pow(z, 2));
            if(gotoTwoPhase){
                numberAcceleration++;
                if(minSAcceleration > smv) minSAcceleration = smv;
                if(maxSAcceleration < smv) maxSAcceleration = smv;
                if(numberAcceleration == 10){
                    gotoTwoPhase = false;
                    if(Math.abs(maxSAcceleration - minSAcceleration) > smvIP){
                        fallText.setText("Fall detection");
                    }
                }
            }else{
                if(smv > smvFF){
                    gotoTwoPhase = true;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}