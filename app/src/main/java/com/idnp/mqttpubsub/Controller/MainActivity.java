package com.idnp.mqttpubsub.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.idnp.mqttpubsub.R;
import com.idnp.mqttpubsub.Utilities.ToolHelper;

import java.util.UUID;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity
        implements MainActivityListener {

    private final static String TAG = "MainActivity";
    public final static String CLIENT_ID = UUID.randomUUID().toString();
    private MqttBroadcastReceiver mqttBroadcastReceiver;
    private TextView txtDisplay;
    private TextView txtAction;
    private EditText edtTopic;
    private EditText edtQos;
    private EditText edtNumMessage;
    private EditText edtDelay;

    TextView contador;
    SensorManager sensorManager;
    Sensor sensor;
    SensorEventListener sensorEventListener;
    int i=0;
    float valor =0;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio);

        mqttBroadcastReceiver = new MqttBroadcastReceiver(MainActivity.this);
/*
        txtAction = (TextView) findViewById(R.id.txtAction);
        txtDisplay = (TextView) findViewById(R.id.txtDisplay);
        edtTopic = (EditText) findViewById(R.id.edtTopic);
        edtQos = (EditText) findViewById(R.id.edtQos);
        edtNumMessage = (EditText) findViewById(R.id.edtNumMessage);
        edtDelay = (EditText) findViewById(R.id.edtDelay);*/

        mqttBroadcastReceiver = new MqttBroadcastReceiver(MainActivity.this);

        contador=(TextView) findViewById(R.id.contador);
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if(sensor==null)
            finish();


        inicioEjercicio();


        Button btnStart = (Button) findViewById(R.id.btnStart);
        Button btnStop = (Button) findViewById(R.id.btnStop);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        Button btnPublish = (Button) findViewById(R.id.btnPublish);

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.d(TAG, "stoping...");
                    initMqttService(MqttIntentService.ACTION_STOP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    initMqttService(MqttIntentService.ACTION_START);
                    //txtAction.setText("Connected");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


/*
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    initMqttService(MqttIntentService.ACTION_PUBLISH);
                    String datetime2 = ToolHelper.getDateTime();
                    ToolHelper.setPublishBegin(getApplicationContext(), datetime2);
                    //txtAction.setText("Started at: " + datetime2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    initMqttService(MqttIntentService.ACTION_SAVE);
                    //txtAction.setText("Saved");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MqttBroadcastReceiver.ACTION_MESSAGE);
        registerReceiver(mqttBroadcastReceiver, intentFilter);

        String datetime2 = ToolHelper.getPublishBegin(getApplicationContext());
        sensorManager.registerListener(sensorEventListener, sensor, sensorManager.SENSOR_DELAY_NORMAL);
        //txtAction.setText(datetime2);

    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mqttBroadcastReceiver);
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void display(String data) {
        //txtDisplay.setText(data);
    }


    private void initMqttService(String action) {
        //String topic = edtTopic.getText().toString();
        String topic = "Ejercicio";
        //int qos = Integer.parseInt(edtQos.getText().toString());
        int qos = Math.round(valor);
        //int delay = Integer.parseInt(edtDelay.getText().toString());
        int delay = 5;
        //int size = Integer.parseInt(edtNumMessage.getText().toString());
        int size = 600;
        Intent intent = new Intent(MainActivity.this, MqttHelperService.class);
        intent.putExtra(MqttIntentService.TOPIC, topic);
        intent.putExtra(MqttIntentService.QOS, qos);
        intent.putExtra(MqttIntentService.DELAY, delay);
        intent.putExtra(MqttIntentService.DATA, size);
        intent.setAction(action);
        startService(intent);

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }*/

    }

    private void inicioEjercicio(){
        sensorEventListener=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(
                        rotationMatrix, event.values);
                // Remap coordinate system
                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z ,
                        remappedRotationMatrix);

                // Convert to orientations
                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);

                for(int i = 0; i < 3; i++) {
                    orientations[i] = (float)(Math.toDegrees(orientations[i]));
                }

                if(orientations[1]>0 && orientations[1] < 75) {
                    getWindow().getDecorView().setBackgroundColor(Color.parseColor("#D5D8DC"));
                    if (flag==1){
                        flag=0;
                    }

                } else if(orientations[1]>75 && orientations[1] < 90) {
                    if (flag==0) {
                        valor = orientations[1];
                        i++;
                        contador.setText("" + i);
                        System.out.println("/****************//");
                        initMqttService(MqttIntentService.ACTION_PUBLISH);
                        String datetime2 = ToolHelper.getDateTime();
                        ToolHelper.setPublishBegin(getApplicationContext(), datetime2);
                        flag = 1;
                    }
                }
            }


            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

}
