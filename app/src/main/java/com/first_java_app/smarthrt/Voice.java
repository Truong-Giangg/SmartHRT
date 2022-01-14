package com.first_java_app.smarthrt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;

public class Voice extends AppCompatActivity {
    TextView hienthi;
    Button nutnhan;
    public int giatridieukhien;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    int count=0;
    private int currentWidget;
    UserHelperClassGadget[] userGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        hienthi= (TextView) findViewById(R.id.Hienthi);
        nutnhan=(Button) findViewById(R.id.Nutnhan);
        nutnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                startActivityForResult(intent,100);

            }
        });

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String a=new String(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        hienthi.setText(a);
        for(int i =0;i<currentWidget;i++){
            if(userGet[i].getWidType().equals("button")){
                if(a.contentEquals("Bật "+userGet[i].getbtnName())||a.contentEquals("bật "+userGet[i].getbtnName())){
                    userGet[i].btnValue="1";
                    reference.child(userGet[i].getBtnID()).setValue(userGet[i]);
                    hienthi.setText("đã bật "+userGet[i].getbtnName());
                    Toast.makeText(Voice.this, "đã bật "+userGet[i].getbtnName(), Toast.LENGTH_LONG).show();
                }
                else if(a.contentEquals("Tắt "+userGet[i].getbtnName())||a.contentEquals("tắt "+userGet[i].getbtnName())){
                    userGet[i].btnValue="0";
                    reference.child(userGet[i].getBtnID()).setValue(userGet[i]);
                    hienthi.setText("đã tắt "+userGet[i].getbtnName());
                    Toast.makeText(Voice.this, "đã tắt "+userGet[i].getbtnName(), Toast.LENGTH_LONG).show();
                }
//                else {
//                    //hienthi.setText("không tìm thấy thiết bị!!");
//                    Toast.makeText(Voice.this, "không tìm thấy thiết bị! ", Toast.LENGTH_LONG).show();
//                }
            }

        }
//        if (a.contentEquals("bật đèn 1")) {
//            giatridieukhien=1;
//
//        }
//        else if (a.contentEquals("bật đèn 2")) {
//            giatridieukhien=2;
//
//
//        }
//        else if (a.contentEquals("bật đèn 3")) {
//            giatridieukhien=3;
//
//        }
//
//        else if (a.contentEquals("bật đèn 4")) {
//            giatridieukhien=4;
//
//
//        }
//        else if (a.contentEquals("bật đèn 5")) {
//            giatridieukhien=5;
//
//        }
//        else if (a.contentEquals("bật đèn 6")) {
//            giatridieukhien=6;
//
//
//        }
//        else if (a.contentEquals("bật đèn 6")) {
//            giatridieukhien=6;
//
//        }
//        else {
//            hienthi.setText("khong co ");
//        }
//
//        nhagiatri(giatridieukhien);
    }
//    void nhagiatri(int giatridieukhie){
//        int  giatrinhanduoc=giatridieukhie;
//        rootNode = FirebaseDatabase.getInstance("https://doan-dc76b-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        reference = rootNode.getReference(" lay du lieu ");
//        int s=giatrinhanduoc;
//        reference.child("lay du lieu ").setValue(s);
//
//    }

}
