package com.first_java_app.smarthrt;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class alarmMainActivity extends AppCompatActivity {

    //khai bao
    Button btnHenGio,btnDungLai;
    TextView txtHienThi;
    TimePicker timePicker;
    Calendar calendar;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // anh xa
        btnHenGio =(Button) findViewById(R.id.btnHenGio);
        btnDungLai =(Button) findViewById(R.id.btnDungLai);
        txtHienThi =(TextView) findViewById(R.id.textView);
        timePicker =(TimePicker) findViewById(R.id.timePicker);
        calendar =Calendar.getInstance(); // thoi gian tren may
        alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE); // truy cap ALARM_SERVICE cua may  bao thuc

        Intent intent=new Intent(alarmMainActivity.this, AlarmReceiver.class);// truyen du lieu MainActivity -> Alarmrecever

        // khai bao su kien
        btnHenGio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar.set(Calendar.HOUR_OF_DAY,timePicker.getCurrentHour());// lay gio
                calendar.set(Calendar.MINUTE,timePicker.getCurrentMinute());// lay phut

                int gio = timePicker.getCurrentHour();
                int phut = timePicker.getCurrentMinute();

                // chuyen int sang chuoi
                String string_gio =String.valueOf(gio);
                String string_phut =String.valueOf(phut);

                if(gio>12 ){
                    string_gio= String.valueOf(gio-12);
                }
                if(phut <10){
                    string_phut = "0" +String.valueOf(phut);
                }
                intent.putExtra("extra","on");
                pendingIntent =PendingIntent.getBroadcast(
                        alarmMainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT
                );
                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
                txtHienThi.setText("Giờ bạn đặt là "+string_gio+":"+ string_phut);
            }
        });
        btnDungLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtHienThi.setText("Dừng lại");
                alarmManager.cancel(pendingIntent);
                intent.putExtra("extra","off");
                sendBroadcast(intent);
            }
        });
    }
}