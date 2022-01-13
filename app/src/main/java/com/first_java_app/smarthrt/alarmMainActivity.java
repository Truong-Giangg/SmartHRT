package com.first_java_app.smarthrt;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class alarmMainActivity extends AppCompatActivity {

    //khai bao
    Button btnHenGio,btnDungLai;
    TextView txtHienThi;
    TimePicker timePicker;
    Calendar calendar;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    final int delay = 100; //milliseconds

    UserHelperClassGadget[] userGet;
    private int currentWidget;
    final Handler handler = new Handler();
    FirebaseDatabase rootNode;
    DatabaseReference reference;


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

        Intent intent1 =getIntent();
        if(intent1.getStringExtra("swgestureid")!=null){
            MainActivity.gestureChild =intent1.getStringExtra("swgestureid");
            Toast.makeText(alarmMainActivity.this, "child:"+MainActivity.gestureChild, Toast.LENGTH_LONG).show();
        }
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("users").child(MainActivity.user_username_gadget).child("user's gadget");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int userNum = 0;
                int size = (int) dataSnapshot.getChildrenCount();
                userGet = new UserHelperClassGadget[size];

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    userGet[userNum] = snapshot.getValue(UserHelperClassGadget.class);//get data store to class
                    userNum++;
                }
                currentWidget = (int) dataSnapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        handler.postDelayed(new Runnable() {
            public void run() {
                for(int i=0; i<currentWidget; i++){
                    if(userGet[i].getBtnID().equals(MainActivity.gestureChild)){
                        if((MainActivity.alarmActive).equals("1")){
                            if(userGet[i].getbtnValue().equals("1")){
                                userGet[i].btnValue="0";
                                reference.child(MainActivity.gestureChild).setValue(userGet[i]);
                                MainActivity.alarmActive ="0";
                            }else{
                                userGet[i].btnValue="1";
                                reference.child(MainActivity.gestureChild).setValue(userGet[i]);
                                MainActivity.alarmActive ="0";
                            }
                        }
                    }

                }
                handler.postDelayed(this, delay);
            }
        }, delay);
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