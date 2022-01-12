package com.first_java_app.smarthrt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainMenu extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener{
    public static Switch[] addedWidgetSW;
    public static SeekBar[] addedWidgetSB;
    public static TextView[] addedWidgetTX;
    TextView userTop;

    LinearLayout layout;
    String[] txViewName;
    private int currentWidget = 0;
    UserHelperClassGadget[] userGet;


    FirebaseDatabase rootNode;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        layout = findViewById(R.id.layout);
        userTop = findViewById(R.id.userName);
        BottomNavigationView navigationView = findViewById(R.id.bottom_nav);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_alarm:
                        startActivity(new Intent(MainMenu.this,alarmMainActivity.class));
                        break;
                    case R.id.action_voice:

                        break;
                    case R.id.action_gesture:
                        startActivity(new Intent(MainMenu.this,CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                    case R.id.action_ipcam:
                        startActivity(new Intent(MainMenu.this,ipCamere.class));
                        break;
                }
                return true;
            }
        });
        //--------------fetch data from previous activity----------
        Intent intent =getIntent();
        if(intent.getStringExtra("username")!=null){
            MainActivity.user_username_gadget =intent.getStringExtra("username");
        }
        //--------------end fetch data from previous activity----------

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("users").child(MainActivity.user_username_gadget).child("user's gadget");

        reference.addValueEventListener(new ValueEventListener() {        // get data from firebase when change
//        reference.addListenerForSingleValueEvent(new ValueEventListener() { //get data from firebase only once
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int userNum = 0;
                int size = (int) dataSnapshot.getChildrenCount();
                txViewName = new String[size];
                addedWidgetSW= new Switch[size];
                addedWidgetSB= new SeekBar[size];
                addedWidgetTX= new TextView[size];
                userGet = new UserHelperClassGadget[size];
                clearLayout();
                userTop.setText(MainActivity.user_username_gadget);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    userGet[userNum] = snapshot.getValue(UserHelperClassGadget.class);//get data store to class
                    if(userGet[userNum].getWidType().equals("button")){
                        createSwitch(addedWidgetSW,userNum,userGet[userNum].getWidType(),userGet[userNum].getBtnID(),userGet[userNum].getbtnName(),userGet[userNum].getbtnValue());
                    }
                    if(userGet[userNum].getWidType().equals("seekbar")){
                        createSeekBar(addedWidgetSB,userNum,userGet[userNum].getWidType(),userGet[userNum].getBtnID(),userGet[userNum].getbtnName(),userGet[userNum].getbtnValue());
                    }
                    if(userGet[userNum].getWidType().equals("temperature")){
                        createTemp(addedWidgetTX,userNum,userGet[userNum].getWidType(),userGet[userNum].getBtnID(),userGet[userNum].getbtnName(),userGet[userNum].getbtnValue());
                    }

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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        for(int i=0; i<currentWidget;i++){
            if(seekBar.getId() == Integer.parseInt(userGet[i].getBtnID())){
                if(userGet[i].getWidType().equals("seekbar")){
                    userGet[i].btnValue = String.valueOf(progress);
                }
            }
        }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        for(int i=0; i<currentWidget;i++){

            if(seekBar.getId() ==Integer.parseInt(userGet[i].getBtnID())){
                if(userGet[i].getWidType().equals("seekbar")){
                    pushSbData2Firebase(addedWidgetSB,i,String.valueOf(addedWidgetSB[i].getId()));
                }
            }
        }
    }
    @Override
    public void onClick(View view) {

    }
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for(int i=0; i<currentWidget;i++){
            if(buttonView.isPressed()){
                if(buttonView.getId() ==Integer.parseInt(userGet[i].getBtnID())){
                    if(userGet[i].getWidType().equals("button")){
                        pushSwData2Firebase(addedWidgetSW,i,String.valueOf(addedWidgetSW[i].getId()));
                    }
                }
            }
            //System.out.println("type:"+userGet[i].getWidType());
        }
    }
    public void createSwitch(Switch sw[], int count, String swType,String swId, String swName, String swValue){
        addedWidgetSW[count] = new Switch(MainMenu.this);
        addedWidgetSW[count].setText(swName);
        addedWidgetSW[count].setId(Integer.parseInt(swId));
        createLayoutForSwitches(sw, count);
        addedWidgetSW[count] = findViewById(Integer.parseInt(swId));
        addedWidgetSW[count].setOnCheckedChangeListener(MainMenu.this::onCheckedChanged);// calling onClick() method for new button
        if(userGet[count].getbtnValue().equals("1")){
            addedWidgetSW[count].setChecked(true); //set the current state of a Switch
        }else{
            addedWidgetSW[count].setChecked(false);
        }
    }

    public void createSeekBar(SeekBar sb[], int count, String sbType,String sbId, String sbName, String sbValue){
        addedWidgetSB[count] = new SeekBar(MainMenu.this);
        addedWidgetSB[count].setId(Integer.parseInt(sbId));
        createLayoutForSeekbar(sb,count);
        addedWidgetSB[count] = findViewById(Integer.parseInt(sbId));
        addedWidgetSB[count].setMax(256);
        addedWidgetSB[count].setOnSeekBarChangeListener(MainMenu.this);
        addedWidgetSB[count].setProgress(Integer.parseInt(sbValue));
    }
    public void createTemp(TextView tx[], int count, String txType,String txId, String txName, String txValue){
        addedWidgetTX[count] = new TextView(MainMenu.this);
        addedWidgetTX[count].setId(Integer.parseInt(txId));
        txViewName[count]= txName;
        createLayoutForTemp(tx,count);
        addedWidgetTX[count].setGravity(Gravity.CENTER);
        addedWidgetTX[count].setText(txValue+" celsius");
    }
    public void createLayoutForSwitches(Switch sw[], int count){
        LinearLayout ll = (LinearLayout)findViewById(R.id.layoutswitch);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        sw[count].setGravity(Gravity.CENTER);

        lp.setMargins(10,30,10,30); //for better layout
        if(sw[count].getParent() != null) {
            ((ViewGroup)sw[count].getParent()).removeView(sw[count]); // <- fix
        }
        ll.addView(sw[count], lp);

    }
    public void createLayoutForSeekbar(SeekBar sb[], int count){
        LinearLayout ll = (LinearLayout)findViewById(R.id.layoutseekbar);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(10,30,10,30); //for better layout
        lp.gravity = Gravity.CENTER;
        if(sb[count].getParent() != null) {
            ((ViewGroup)sb[count].getParent()).removeView(sb[count]); // <- fix
        }
        TextView textView = new TextView(MainMenu.this);
        textView.setText(userGet[count].getbtnName());
        textView.setGravity(Gravity.CENTER);
        ll.addView(textView);

        ll.addView(sb[count], lp);

    }
    public void createLayoutForTemp(TextView tx[], int count){
        LinearLayout ll = (LinearLayout)findViewById(R.id.layoutTemp);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(10,30,10,30); //for better layout
        lp.gravity = Gravity.CENTER;
        if(tx[count].getParent() != null) {
            ((ViewGroup)tx[count].getParent()).removeView(tx[count]); // <- fix
        }
        TextView textView = new TextView(MainMenu.this);
        textView.setText(txViewName[count]);
        textView.setGravity(Gravity.CENTER);
        ll.addView(textView);
        ll.addView(tx[count], lp);

    }
    private void clearLayout() {
        LinearLayout ll = (LinearLayout)findViewById(R.id.layoutTemp);
        ll.removeAllViews();
        LinearLayout ll1 = (LinearLayout)findViewById(R.id.layoutswitch);
        ll1.removeAllViews();
        LinearLayout ll2 = (LinearLayout)findViewById(R.id.layoutseekbar);
        ll2.removeAllViews();
    }
    public void pushSwData2Firebase(Switch sw[],int count,String firebaseChild){
        if(addedWidgetSW[count].isChecked()){
            userGet[count].btnValue="1";
            reference.child(firebaseChild).setValue(userGet[count]);
        }else{
            userGet[count].btnValue="0";
            reference.child(firebaseChild).setValue(userGet[count]);
        }
    }
    public void pushSbData2Firebase(SeekBar sb[],int count, String firebaseChild){
        reference.child(firebaseChild).setValue(userGet[count]);
    }

    public void gotoaddWidget(View view){
        Intent intent =new Intent(MainMenu.this,addWidget.class);
        intent.putExtra("widgetChild",String.valueOf(currentWidget));
        startActivity(intent);
    }
    public void gotoremoveWidget(View view){
        Intent intent =new Intent(MainMenu.this,removeWidget.class);
        startActivity(intent);
    }


}
