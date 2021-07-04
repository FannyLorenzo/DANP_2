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
import android.os.Handler;
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
    Sensor sensorAcelerometro;
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

        contador = (TextView) findViewById(R.id.contador);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorAcelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        /*  AQUI POR SI NO TENGO EL SENSOR, USA EL ACELEROMETRO */
        if (sensor != null){
            System.out.println("Se está trabajando con sensor TYPE_ROTATION_VECTOR ");
            // aqui deberia iniciarse en ejericico usando este sensor
            inicioEjercicio(); // creo q es este metodo
        }else if(sensor==null && sensorAcelerometro !=null ) {
            System.out.println("Se está trabajando con sensor TYPE_ACCELEROMETER ");
            // aqui deberia iniciarse en ejericico usando este sensor
            inicioEjercicioConAcelerometro();
        }else if(sensorAcelerometro ==null) {
            System.out.println("No se sensores TYPE_ROTATION_VECTOR ni TYPE_ACCELEROMETER ");
            finish();
        }

      //  inicioEjercicio();  se subio arriba, en el caso apliqie


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
        // se añadio estoooooo
        if (sensor != null){
            System.out.println("Se está trabajando con sensor TYPE_ROTATION_VECTOR ");
            sensorManager.registerListener(sensorEventListener, sensor, sensorManager.SENSOR_DELAY_NORMAL);
            //txtAction.setText(datetime2);
        }else if(sensor==null && sensorAcelerometro !=null ) {
            System.out.println("Se está trabajando con sensor TYPE_ACCELEROMETER ");
            sensorManager.registerListener(sensorEventListener, sensorAcelerometro, sensorManager.SENSOR_DELAY_NORMAL);
            //ejecutar(); // se añadio este metodo OJO
            //txtAction.setText(datetime2);
        }else if(sensorAcelerometro ==null) {
            System.out.println("No se tiene los sensores TYPE_ROTATION_VECTOR ni TYPE_ACCELEROMETER ");
            finish();
        }


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

    private void start(){
        sensorManager.registerListener(sensorEventListener, sensorAcelerometro, SensorManager.SENSOR_DELAY_NORMAL);
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
    float x, y, z;
    float alpha = (float) 0.8;
    float [] linear_acceleration =new float[3];
    float [] gravity = new float[3];
    private void inicioEjercicioConAcelerometro(){ // metodo que tiene q ser implementado por JOSE
                                                // ES MEJOR CALCULAR EL GRADO DE INCLINACION QUE LA DISTANCIA, POR AHI LEI Q ES MEJOR
        sensorEventListener=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // recuperando valores del sensor Acelerometro
                x=event.values[0];
                y=event.values[1];
                z=event.values[2];
                System.out.println(" sin filtros: "+x+ " , "+ y + " , "+z);

                // implementación del ejercicio
                if(y<-9){
                    if (flag==1){
                        flag=0;
                    }
                }
                if(y>0){
                    if (flag==0) {
                        valor = event.values[1];
                        i++;
                        contador.setText("" + i);
                        System.out.println("/****************//");
                        initMqttService(MqttIntentService.ACTION_PUBLISH);
                        String datetime2 = ToolHelper.getDateTime();
                        ToolHelper.setPublishBegin(getApplicationContext(), datetime2);
                        flag = 1;
                    }
                }

               // FILTRO DE PASO ALTO Y BAJO APLICADO A LOS VECTORES DE ACELEROMETRO.
                alpha = (float) 0.8;
                gravity =  new float[3];
                linear_acceleration =  new float[3];

                // Aísle la fuerza de la gravedad con el filtro de paso bajo.
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                // Elimina la contribución de la gravedad con el filtro de paso alto.
                linear_acceleration[0] = event.values[0] - gravity[0];
                linear_acceleration[1] = event.values[1] - gravity[1];
                linear_acceleration[2] = event.values[2] - gravity[2];

                // usar los valores de linear_acceleration[] para los CALCULOS
                System.out.println(" con filtros: "+linear_acceleration[0]+
                                             " , "+ linear_acceleration[1] +
                                              " , "+linear_acceleration[2]);

                // ejecutar();// AQUI NO LLAMAR A ESTE METODO, SI NO NO FUNCIONARA LA PAUSA DEL TIEMPO
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }



            /* Filtro de paso bajo del libro - no se está usando            *
            * */
            public float lowPass(float current, float last, float alpha){
                return last*(1.0f-alpha) + current*alpha;
            }
            /* Filtro de paso alto  del libro - no se está usando            *
             * */
            public float highPass(float current, float last, float filtered, float alpha){
                return alpha * (filtered + current - last);
            }

        };

        ejecutar(); // AQUI LLAMAMOS AL METODO QUE QUEREMOS QUE SE EJECUTE CADA X TIEMPO
    }
    // para activar cada x tiempo un metodo
    public void ejecutar(){
        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                metodoEjecutar();//llamamos nuestro metodo IMPLEMENTADO
                handler.postDelayed(this,1000);// 10000 se ejecutara cada 10 segundos // se puso 2000 para 2 segundo
            }
        },5000);//empezara a ejecutarse después de 5 milisegundos
    }
    public void metodoEjecutar() {
        //Aqui codigo de lo que haga tu metodo a IMPLEMENTAR
        //Por ahora solo est'a este print
        System.out.println(" se ejecutó metodo ");
        System.out.println(" PAUSADO : "+linear_acceleration[0]+
                " , "+ linear_acceleration[1] +
                " , "+linear_acceleration[2]);
    }



}
