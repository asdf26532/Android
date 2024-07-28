package com.example;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btn_start,btn_stop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       btn_start = (Button) findViewById(R.id.btn_start);
       btn_stop = (Button) findViewById(R.id.btn_stop);

       btn_start.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startService(new Intent(getApplicationContext(), MusicService.class));

           }
       });

       btn_stop.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               stopService(new Intent(getApplicationContext(), MusicService.class));
           }
       });

    }
}
