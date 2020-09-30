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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer, senGyroscope;
    private SensorEventListener acceleromertListener, gyroscopeListener;
    private boolean resultAcc = false;
    private boolean gotoTwoPhase = false;
    private boolean gotoTwoPhaseGry = false;
    private int numberAcceleration = 0;
    private int numberGyrocope = 0;
    private double minSAcceleration = 100;
    private double maxSAcceleration = -100;
    private double minSGry = 100;
    private double maxSGry = -100;
    private double smvFF = 7.5;
    private double smvIP = 18.5;
    private double avFF = 6.5;
    private double avIP = 16.5;
    private float x=0, y=0, z=0;
    private TextView fallText, fallTextGry, result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senGyroscope = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        fallText = findViewById(R.id.fall);
        result = findViewById(R.id.result);
        fallTextGry = findViewById(R.id.fallGry);

        acceleromertListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                double smv = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                if(gotoTwoPhase){
                    numberAcceleration += 1;
                    if(minSAcceleration > smv){
                        minSAcceleration = smv;
                    }
                    if(maxSAcceleration < smv){
                        maxSAcceleration = smv;
                    }
                    if(numberAcceleration == 10){
                        gotoTwoPhase = false;
                        numberAcceleration = 0;
                        if(Math.abs(maxSAcceleration - minSAcceleration) > smvIP){
                            fallText.setText("Fall detection of acceleromert !");
                            resultAcc = true;
                        }
                        minSAcceleration = 100;
                        maxSAcceleration = -100;
                    }
                }else{
                    if(smv > smvFF){
                        gotoTwoPhase = true;
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        gyroscopeListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float xGry = event.values[0];
                float yGry = event.values[1];
                float zGry = event.values[2];
                double av = Math.abs(x*Math.sin(zGry)+y*Math.sin(yGry)-z*Math.cos(yGry)*Math.cos(zGry));
                if(gotoTwoPhaseGry){
                    numberGyrocope += 1;
                    if(minSGry > av){
                        minSGry = av;
                    }
                    if(maxSGry < av){
                        maxSGry = av;
                    }
                    if(numberGyrocope == 10){
                        gotoTwoPhaseGry = false;
                        numberGyrocope = 0;
                        if(Math.abs(maxSGry - minSGry) > avIP){
                            fallTextGry.setText("Fall detection of gyrocope !");
                            if(resultAcc){
                                result.setText("Fall detection !");
                            }
                        }
                        minSGry = 100;
                        maxSGry = -100;
                        resultAcc = false;
                        fallText.setText("Analyzing !");
                    }
                }else{
                    if(av > avFF){
                        gotoTwoPhaseGry = true;
                    }else{
                        resultAcc = false;
                        fallText.setText("Analyzing !");
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(acceleromertListener);
        senSensorManager.unregisterListener(gyroscopeListener);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(acceleromertListener,senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        senSensorManager.registerListener(gyroscopeListener, senGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }
}