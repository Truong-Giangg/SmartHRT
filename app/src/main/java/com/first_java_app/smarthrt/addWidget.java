package com.first_java_app.smarthrt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class addWidget extends AppCompatActivity implements View.OnClickListener{
    Button addWidgetSwitch;
    Button addWidgetSlider;
    Button addWidgetTemp;
    EditText widgetNameAdd;

    String widgetName_s;
    String widgetType;
    int widgetChildH;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addwidget);
        addWidgetSwitch = findViewById(R.id.addWidgetSwitch);
        addWidgetSlider = findViewById(R.id.addWidgetSlider);
        addWidgetTemp = findViewById(R.id.addWidgetTemp);
        widgetNameAdd = findViewById(R.id.widgetNameAdd);
        addWidgetSlider.setOnClickListener(addWidget.this);
        addWidgetSwitch.setOnClickListener(addWidget.this);
        addWidgetTemp.setOnClickListener(addWidget.this);
        //--------------fetch data from previous activity----------
        reference = FirebaseDatabase.getInstance().getReference("users");
        Intent intent =getIntent();
//        if(intent.getStringExtra("username")!=null){
//            MainActivity.user_username_gadget =intent.getStringExtra("username");
//        }
        if(intent.getStringExtra("widgetChild")!=null){
            MainActivity.widgetChild =intent.getStringExtra("widgetChild");
        }
        widgetChildH = Integer.parseInt(MainActivity.widgetChild);
        //--------------end fetch data from previous activity----------
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("users").child(MainActivity.user_username_gadget).child("user's gadget");
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.addWidgetSwitch){
            widgetName_s = widgetNameAdd.getText().toString();
            widgetNameAdd.setText("");//clear the field when button pushed
            widgetType = "button";
            gobackMainMenu(view);
        }
        if(view.getId() == R.id.addWidgetSlider){
            widgetName_s = widgetNameAdd.getText().toString();
            widgetNameAdd.setText("");//clear the field when button pushed
            widgetType = "seekbar";
            gobackMainMenu(view);
        }
        if(view.getId() == R.id.addWidgetTemp){
            widgetName_s = widgetNameAdd.getText().toString();
            widgetNameAdd.setText("");//clear the field when button pushed
            widgetType = "temperature";
            gobackMainMenu(view);
        }
    }
    public void gobackMainMenu(View view){

        UserHelperClassGadget helperClass =new UserHelperClassGadget(String.valueOf(widgetChildH), widgetName_s, "0",widgetType, "null");
        reference.child(String.valueOf(widgetChildH)).setValue(helperClass).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                //--------------push data to MainMenu acctivity via username------------
                Intent intent =new Intent(getApplicationContext(),MainMenu.class);
                //intent.putExtra("username",MainActivity.user_username_gadget);
                startActivity(intent);
                //--------------end push data to MainMenu acctivity via username------------
            }
        });


    }
}
