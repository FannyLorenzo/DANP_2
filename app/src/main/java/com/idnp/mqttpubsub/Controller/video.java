package com.idnp.mqttpubsub.Controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.idnp.mqttpubsub.R;

public class video extends AppCompatActivity {

    Button realizar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        realizar=(Button)findViewById(R.id.realizar);
        realizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(video.this);
                alerta.setMessage("Comenzaremos a calificar su ejercicio, por favor, asegure su celular a la parte frontal de su tobillo.")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent pasar=new Intent(video.this, MainActivity.class);
                                startActivity(pasar);
                            }
                        });
                AlertDialog titulo = alerta.create();
                titulo.setTitle("Realizar ejercicio");
                titulo.show();
            }
        });

    }
}