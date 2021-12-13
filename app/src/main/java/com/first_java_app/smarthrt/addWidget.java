package com.first_java_app.smarthrt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class addWidget extends AppCompatActivity implements View.OnClickListener{
    Button addWidgetSwitch;
    Button addWidgetSlider;
    EditText widgetNameAdd;

    String widgetName_s;
    String widgetType;
    int widgetId;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addwidget);
        addWidgetSwitch = findViewById(R.id.addWidgetSwitch);
        addWidgetSlider = findViewById(R.id.addWidgetSlider);
        widgetNameAdd = findViewById(R.id.widgetNameAdd);
        addWidgetSlider.setOnClickListener(addWidget.this);
        addWidgetSwitch.setOnClickListener(addWidget.this);
        //--------------fetch data from previous activity----------
        reference = FirebaseDatabase.getInstance().getReference("users");
        Intent intent =getIntent();
        if(intent.getStringExtra("username")!=null){
            MainActivity.user_username_gadget =intent.getStringExtra("username");
        }
        if(intent.getStringExtra("widgetid")!=null){
            MainActivity.widgetid =intent.getStringExtra("widgetid");
        }
        widgetId = Integer.parseInt(MainActivity.widgetid);
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
    }
    public void gobackMainMenu(View view){
        widgetId++;
        UserHelperClassGadget helperClass =new UserHelperClassGadget(String.valueOf(widgetId), widgetName_s, "0",widgetType);
        reference.child(String.valueOf(widgetId)).setValue(helperClass);
        //--------------push data to MainMenu acctivity via username------------
        Intent intent =new Intent(getApplicationContext(),MainMenu.class);
        intent.putExtra("username",MainActivity.user_username_gadget);
        startActivity(intent);
        //--------------end push data to MainMenu acctivity via username------------
    }
}
