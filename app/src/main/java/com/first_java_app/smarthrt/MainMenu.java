package com.first_java_app.smarthrt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainMenu extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener{

    Switch addedWidget[]= new Switch[8];
    SeekBar addedWidgetSB[]= new SeekBar[8];
    Drawable btnDraw;
    Drawable btnDraw1;

    LinearLayout layout;
    String btnValue[]=new String[8];
    String btnType[]=new String[8];
    String SeekBName[]=new String[8];

    public static int[] BtnID= {1, 2, 3, 4, 5, 6, 7, 8};
    public static int currentWidget = 0;

    Activity activity;	// for .setBackground()

    ArrayList<String> list = new ArrayList<>();
    String[] userDataGetId = new String[8];
    String[] userDataGetName = new String[8];
    String[] userDataGetValue = new String[8];
    String[] userDataGetType = new String[8];

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        activity = MainMenu.this;// for .setBackground()

        layout = findViewById(R.id.layout);
        btnDraw = getResources().getDrawable(R.drawable.btn_bg);
        btnDraw1 = getResources().getDrawable(R.drawable.btn_bg_1);

        //--------------fetch data from previous activity----------
        reference = FirebaseDatabase.getInstance().getReference("users");
        Intent intent =getIntent();
        if(intent.getStringExtra("username")!=null){
            MainActivity.user_username_gadget =intent.getStringExtra("username");
        }
        //--------------end fetch data from previous activity----------
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("users").child(MainActivity.user_username_gadget).child("user's gadget");

//        reference.addValueEventListener(new ValueEventListener() {        // get data from firebase when change
        reference.addListenerForSingleValueEvent(new ValueEventListener() { //get data from firebase only once
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                int userNum = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    list.add(snapshot.getValue().toString());
                    UserHelperClassGadget userget = snapshot.getValue(UserHelperClassGadget.class);//get data store to class
                    userDataGetId[userNum] = userget.getBtnID();
                    userDataGetName[userNum] = userget.getbtnName();
                    userDataGetValue[userNum] = userget.getbtnValue();
                    userDataGetType[userNum] = userget.getWidType();
                    userNum++;
                }
                for(int i=0; i<list.size();i++){ // list.size = userNum
                    if(userDataGetType[i].equals("button")){
                        createSwitch(addedWidget,i,userDataGetType[i],userDataGetId[i],userDataGetName[i],userDataGetValue[i]);

                    }
                    if(userDataGetType[i].equals("seekbar")){
                        createSeekBar(addedWidgetSB,i,userDataGetType[i],userDataGetId[i],userDataGetName[i],userDataGetValue[i]);

                    }
                }
                currentWidget = list.size();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        for(int i=0; i<currentWidget;i++){
            if(seekBar.getId() ==BtnID[i]){
                if(btnType[i].equals("seekbar")){
                    btnValue[i] = String.valueOf(progress);
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
            if(seekBar.getId() ==BtnID[i]){
                if(btnType[i].equals("seekbar")){
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
            if(buttonView.getId() ==BtnID[i]){
                if(btnType[i].equals("button")){
                    pushSwData2Firebase(addedWidget,i,String.valueOf(addedWidget[i].getId()));
                }
            }
        }
    }
    public void createSwitch(Switch sw[], int count, String swType,String swId, String swName, String swValue){
        addedWidget[count] = new Switch(MainMenu.this);
        addedWidget[count].setText(swName);
        addedWidget[count].setId(Integer.parseInt(swId));
        btnType[count] = swType;
        btnValue[count] = swValue;  //get button data
        createLayoutForSwitches(sw, count);
        addedWidget[count] = findViewById(Integer.parseInt(swId));
        addedWidget[count].setOnCheckedChangeListener(MainMenu.this::onCheckedChanged);// calling onClick() method for new button
        if(btnValue[count].equals("1")){
//            activity.findViewById(addedWidget[count].getId()).setBackground(btnDraw);
            addedWidget[count].setChecked(true); //set the current state of a Switch
        }else{
//            activity.findViewById(addedWidget[count].getId()).setBackground(btnDraw1);
            addedWidget[count].setChecked(false);
        }
    }
    public void createSeekBar(SeekBar sb[], int count, String sbType,String sbId, String sbName, String sbValue){
        addedWidgetSB[count] = new SeekBar(MainMenu.this);
        SeekBName[count] = sbName;
        addedWidgetSB[count].setId(Integer.parseInt(sbId));
        btnType[count] = sbType;
        btnValue[count] = sbValue;  //get seekbar data
        createLayoutForSeekbar(sb,count);
        addedWidgetSB[count] = findViewById(Integer.parseInt(sbId));
        addedWidgetSB[count].setMax(256);
        addedWidgetSB[count].setOnSeekBarChangeListener(MainMenu.this);
        addedWidgetSB[count].setProgress(Integer.parseInt(sbValue));
//        addedWidgetSB[count].setProgressDrawable(getResources().getDrawable(R.drawable.seek_bar));
        //https://coderedirect.com/questions/270994/how-to-set-android-seekbar-progress-drawable-programmatically
    }
    public void createLayoutForSwitches(Switch sw[], int count){
        LinearLayout ll = (LinearLayout)findViewById(R.id.layoutswitch);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,30,10,30); //for better layout
        if(sw[count].getParent() != null) {
            ((ViewGroup)sw[count].getParent()).removeView(sw[count]); // <- fix
        }
        ll.addView(sw[count], lp);
        activity.findViewById(sw[count].getId()).setBackground(btnDraw);
    }
    public void createLayoutForSeekbar(SeekBar sb[], int count){
        LinearLayout ll = (LinearLayout)findViewById(R.id.layoutseekbar);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(10,30,10,30); //for better layout
        if(sb[count].getParent() != null) {
            ((ViewGroup)sb[count].getParent()).removeView(sb[count]); // <- fix
        }
        TextView textView = new TextView(MainMenu.this);
        textView.setText(SeekBName[count]);
        ll.addView(textView);
        ll.addView(sb[count], lp);
        activity.findViewById(sb[count].getId()).setBackground(btnDraw1);
    }
    public void pushSwData2Firebase(Switch sw[],int count,String firebaseChild){
        String widgetID  =String.valueOf(addedWidget[count].getId());   //int to string
        String widgetName = addedWidget[count].getText().toString();
        String widgetType = btnType[count];
        if(addedWidget[count].isChecked()){
            btnValue[count]="1";
            String widgetBtnValue = btnValue[count];
            UserHelperClassGadget helperClass =new UserHelperClassGadget(widgetID, widgetName, widgetBtnValue,widgetType);
            reference.child(firebaseChild).setValue(helperClass);
        }else{
            btnValue[count]="0";
            String widgetBtnValue = btnValue[count];   //REMEMBER to update widgetBtnValue before push on database
            UserHelperClassGadget helperClass =new UserHelperClassGadget(widgetID, widgetName, widgetBtnValue,widgetType);
            reference.child(firebaseChild).setValue(helperClass);
        }
    }
    public void pushSbData2Firebase(SeekBar sb[],int count, String firebaseChild){
        String widgetID  =String.valueOf(addedWidgetSB[count].getId());   //int to string
        String widgetName = SeekBName[count];
        String widgetBtnValue = String.valueOf(btnValue[count]);
        String widgetType = btnType[count];
        UserHelperClassGadget helperClass =new UserHelperClassGadget(widgetID, widgetName, widgetBtnValue,widgetType);
        reference.child(firebaseChild).setValue(helperClass);
    }
    public void gotoGesture(View view) {
        Intent intent = new Intent(MainMenu.this, handGesture.class);
        startActivity(intent);
    }
    public void gotoIpCamera(View view){
        Intent intent =new Intent(MainMenu.this,ipCamere.class);
        startActivity(intent);
    }
    public void gotoaddWidget(View view){
        //--------------push data to MainMenu acctivity via username------------
        Intent intent =new Intent(MainMenu.this,addWidget.class);
        intent.putExtra("username",MainActivity.user_username_gadget);
        intent.putExtra("widgetid",String.valueOf(currentWidget));
        startActivity(intent);
        //--------------end push data to MainMenu acctivity via username------------
    }
    public void gotoremoveWidget(View view){
        //--------------push data to MainMenu acctivity via username------------
        Intent intent =new Intent(MainMenu.this,removeWidget.class);
        intent.putExtra("username",MainActivity.user_username_gadget);
        intent.putExtra("widgetid",String.valueOf(currentWidget));
        startActivity(intent);
        //--------------end push data to MainMenu acctivity via username------------
    }
}