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
    private int currentWidget;

    String widgetName_s="";
    String espPin_s="";
    String widgetType;
    UserHelperClassGadget[] userGet;

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
        for(int i=0;i<9;i++) {
            dButton[i] = findViewById(buttonIds[i]);
        }
        //espPin = findViewById(R.id.espPin);
        addWidgetSlider.setOnClickListener(addWidget.this);
        addWidgetSwitch.setOnClickListener(addWidget.this);
        addWidgetTemp.setOnClickListener(addWidget.this);
        for(int i=0;i<9;i++) {
            dButton[i].setOnClickListener(addWidget.this);
            dButton[i].setBackgroundColor(Color.GRAY);
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
                currentWidget = (int)dataSnapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.addWidgetSwitch){
            widgetName_s = widgetNameAdd.getText().toString();
            widgetNameAdd.setText("");//clear the field when button pushed
            widgetType = "button";
            if(validInput())
                gobackMainMenu(view);
        }
        if(view.getId() == R.id.addWidgetSlider){
            widgetName_s = widgetNameAdd.getText().toString();
            widgetNameAdd.setText("");//clear the field when button pushed
            widgetType = "seekbar";
            if(validInput())
                gobackMainMenu(view);
        }
        if(view.getId() == R.id.addWidgetTemp){
            widgetName_s = widgetNameAdd.getText().toString();
            widgetNameAdd.setText("");//clear the field when button pushed
            widgetType = "temperature";
            if(validInput())
                gobackMainMenu(view);
        }
        for(int i=0; i<9;i++){
            dButton[i].setBackgroundColor(Color.GRAY);
            if(view.getId() == buttonIds[i]){
                dButton[i].setBackgroundColor(Color.GREEN);
                espPin_s = "D"+i;
            }
        }
    }
    private boolean validInput(){
        for(int i=0; i<currentWidget;i++){
            if(userGet[i].getbtnName().equals(widgetName_s)){
                Toast.makeText(addWidget.this, "Tên đã tồn tại, chọn lại!!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(userGet[i].getEspPin().equals(espPin_s)){
                Toast.makeText(addWidget.this, "Chân esp8266 đã chọn rồi, chọn lại!!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(widgetName_s.isEmpty()){
            Toast.makeText(addWidget.this, "Tên không được bỏ trống!!", Toast.LENGTH_SHORT).show();
            return false;
        }else if(espPin_s.isEmpty()){
            Toast.makeText(addWidget.this, "Hãy chọn 1 chân esp8266!!", Toast.LENGTH_SHORT).show();
            return false;
        }else return true;
    }

    public void gobackMainMenu(View view){

        UserHelperClassGadget helperClass =new UserHelperClassGadget(String.valueOf(currentWidget), widgetName_s, "0",widgetType, "null", espPin_s);
        reference.child(String.valueOf(currentWidget)).setValue(helperClass).addOnSuccessListener(new OnSuccessListener<Void>() {
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
