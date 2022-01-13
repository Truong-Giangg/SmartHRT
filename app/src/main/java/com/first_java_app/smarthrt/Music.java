package com.first_java_app.smarthrt;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Music extends Service {
    MediaPlayer mediaPlayer;
    private int id;




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("toi tron music","Xin chao");

        String nhankey=intent.getExtras().getString("extra");
        Log.e("Music Nhan Key",nhankey);

        if(nhankey.equals("on")){
            id=1;
        }else if(nhankey.equals("off")){
            id=0;

        }
        if(id == 1){
            mediaPlayer =MediaPlayer.create(this,R.raw.nokia_tune_original_ringtone_alarm);
            mediaPlayer.start();
            MainActivity.alarmActive ="1";

//            new CountDownTimer(2000, 100) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    Toast.makeText(Music.this, "Vui lòng tắt báo thức", Toast.LENGTH_SHORT).show();
//                }
//                @Override
//                public void onFinish() {
//
//                }
//            }.start();
            Toast.makeText(Music.this, "Vui lòng tắt báo thức", Toast.LENGTH_SHORT).show();
            id=0;

        }else if(id==0){
            mediaPlayer.stop();
            mediaPlayer.reset();
            MainActivity.alarmActive ="0";
        }
        return START_NOT_STICKY;
    }
}
