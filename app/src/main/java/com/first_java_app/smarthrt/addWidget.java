package com.first_java_app.smarthrt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class addWidget extends AppCompatActivity implements View.OnClickListener{
    Button addWidgetSwitch;
    Button addWidgetSlider;
    Button addWidgetTemp;
    Button[] dButton = new Button[9];
    int[] buttonIds = {R.id.D0, R.id.D1, R.id.D2, R.id.D3, R.id.D4, R.id.D5, R.id.D6, R.id.D7, R.id.D8};
    EditText widgetNameAdd;
    //EditText espPin;

    String widgetName_s;
    String espPin_s;
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
        for(int i=0;i<buttonIds.length-1;i++) {
            dButton[i] = findViewById(buttonIds[i]);
        }
        //espPin = findViewById(R.id.espPin);
        addWidgetSlider.setOnClickListener(addWidget.this);
        addWidgetSwitch.setOnClickListener(addWidget.this);
        addWidgetTemp.setOnClickListener(addWidget.this);
        for(int i=0;i<buttonIds.length-1;i++) {
            dButton[i].setOnClickListener(addWidget.this);
        }
        //--------------fetch data from previous activity----------
        reference = FirebaseDatabase.getInstance().getReference("users");
        Intent intent =getIntent();
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
        for(int i=0; i<9;i++){
            if(view.getId() == buttonIds[i]){
                dButton[i].setBackgroundColor(Color.GREEN);
                espPin_s = "D"+i;
                Toast.makeText(addWidget.this, "espPin"+espPin_s, Toast.LENGTH_LONG).show();
            }
        }
    }
    public void gobackMainMenu(View view){

        UserHelperClassGadget helperClass =new UserHelperClassGadget(String.valueOf(widgetChildH), widgetName_s, "0",widgetType, "null", espPin_s);
        reference.child(String.valueOf(widgetChildH)).setValue(helperClass).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // hide virtual keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                //--------------push data to MainMenu acctivity via username------------
                Intent intent =new Intent(addWidget.this,MainMenu.class);
                //intent.putExtra("username",MainActivity.user_username_gadget);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                addWidget.this.finish();
                //--------------end push data to MainMenu acctivity via username------------
            }
        });


    }
}
