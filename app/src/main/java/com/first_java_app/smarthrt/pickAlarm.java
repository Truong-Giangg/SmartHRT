package com.first_java_app.smarthrt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class pickAlarm extends AppCompatActivity implements View.OnClickListener{
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    public static int[] swID;
    public static int size;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickalarm);


        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("users").child(MainActivity.user_username_gadget).child("user's gadget");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                size = (int) dataSnapshot.getChildrenCount();
                swID = new int[size];
                int userNum = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserHelperClassGadget userget = snapshot.getValue(UserHelperClassGadget.class);//get data store to class
                    if(userget.getWidType().equals("button")){
                        TextView gadgetList = new TextView(pickAlarm.this);
                        gadgetList.setText(userget.getbtnName());
                        gadgetList.setId(Integer.parseInt(userget.getBtnID()));
                        swID[userNum] = Integer.parseInt(userget.getBtnID());

                        LinearLayout ll = (LinearLayout)findViewById(R.id.layoutswitch);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(10,30,10,30); //for better layout
                        ll.addView(gadgetList, lp);

                        gadgetList.setOnClickListener(pickAlarm.this);
                        userNum++;
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        for(int i=0; i<size; i++){
            if(view.getId() == swID[i]){
                Toast.makeText(pickAlarm.this, "btn ID: "+swID[i], Toast.LENGTH_LONG).show();
                gotoalarmMainactivity(view, swID[i]);

            }
        }
    }
    public void gotoalarmMainactivity(View view, int swID){
        Intent intent =new Intent(getApplicationContext(),alarmMainActivity.class);
        MainActivity.gestureChild = String.valueOf(swID);
        intent.putExtra("swgestureid", MainActivity.gestureChild);
        startActivity(intent);
    }
}
