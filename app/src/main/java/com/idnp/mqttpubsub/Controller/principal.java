package com.idnp.mqttpubsub.Controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.idnp.mqttpubsub.R;

public class principal extends AppCompatActivity {
    Button primerEjerA;
    Button primerEjerB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        primerEjerA=(Button)findViewById(R.id.ejer1a);
        primerEjerB=(Button)findViewById(R.id.ejer1b);

        primerEjerA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(principal.this);
                alerta.setMessage("Vea bien el video para después realizarlo")
                        .setCancelable(false)
                        .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent pasar=new Intent(principal.this, video.class);
                                startActivity(pasar);
                            }
                        });
                AlertDialog titulo = alerta.create();
                titulo.setTitle("Ver Ejercicio");
                titulo.show();
            }
        });

        primerEjerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(principal.this);
                alerta.setMessage("Vea bien el video para después realizarlo")
                        .setCancelable(false)
                        .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent pasar=new Intent(principal.this, video.class);
                                startActivity(pasar);
                            }
                        });
                AlertDialog titulo = alerta.create();
                titulo.setTitle("Ver Ejercicio");
                titulo.show();
            }
        });

    }
}