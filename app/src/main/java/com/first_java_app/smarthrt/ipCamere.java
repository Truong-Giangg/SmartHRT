package com.first_java_app.smarthrt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.marcoscg.ipcamview.IPCamView;

public class ipCamere extends AppCompatActivity {

    Button start;
    EditText URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_camere);
        start=findViewById(R.id.start);
        URL=findViewById(R.id.url);
        IPCamView ipCamView = findViewById(R.id.ip_cam_view);
        /*ipCamView.setUrl("http://webcam.abaco-digital.es/zuda/image2.jpg");
        ipCamView.setInterval(1000); // In milliseconds, default 1000
        ipCamView.start();
        */
         start.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String url=URL.getText().toString();
                 ipCamView.setUrl("https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1");
                 ipCamView.setInterval(1000); // In milliseconds, default 1000
                 ipCamView.start();
             }
         });

    }
}