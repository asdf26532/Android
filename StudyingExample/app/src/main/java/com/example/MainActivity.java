package com.example;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btn_start, btn_stop;
    Thread thread;
    boolean isThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btn_start = findViewById(R.id.btn_start);
        btn_stop = findViewById(R.id.btn_stop);

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.d("HandlerDebug", "Toast message is being shown");
                Toast.makeText(MainActivity.this, "오류 발생", Toast.LENGTH_LONG).show();
            }
        };

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isThread = true;
                thread = new Thread() {
                    public void run() {
                        while (isThread) {
                            try {
                                sleep(5000);
                                Log.d("ThreadDebug", "Handler message sent");
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            handler.sendEmptyMessage(0);
                        }
                    }
                };

                thread.start();
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isThread = false;
            }
        });
    }
}
