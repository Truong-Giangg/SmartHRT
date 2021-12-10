package com.first_java_app.smarthrt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainMenu extends AppCompatActivity implements View.OnClickListener{
    Button addWidget;
    Button addedWidget[]= new Button[8];
    Drawable btnDraw;
    Drawable btnDraw1;

    LinearLayout layout;
    EditText widgetName;
    String widgetName_s;
    String btnValue[]=new String[8];

    public static int[] BtnID= {1, 2, 3, 4, 5, 6, 7, 8};
    public static int currentWidget = 0;
    ArrayList<String> list = new ArrayList<>();
    Activity activity;	// for .setBackground()

    String[] userDataGetId = new String[8];
    String[] userDataGetName = new String[8];
    String[] userDataGetValue = new String[8];

    FirebaseDatabase rootNode;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        activity = MainMenu.this;// for .setBackground()
        addWidget = findViewById(R.id.addWidget);
        layout = findViewById(R.id.layout);
        widgetName = (EditText) findViewById(R.id.widgetName);
        btnDraw = getResources().getDrawable(R.drawable.btn_bg);
        btnDraw1 = getResources().getDrawable(R.drawable.btn_bg_1);
        addWidget.setOnClickListener(MainMenu.this);

        //--------------fetch data from previous activity----------
        reference = FirebaseDatabase.getInstance().getReference("users");
        Intent intent =getIntent();
        if(intent.getStringExtra("username")!=null){
            MainActivity.user_username_gadget =intent.getStringExtra("username");
        }
        //--------------end fetch data from previous activity----------
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("users").child(MainActivity.user_username_gadget).child("user's gadget");
//        reference = rootNode.getReference().child("user's widget");

        // fetch data from firebase when first run
//        reference.addValueEventListener(new ValueEventListener() {        // get data when change
        reference.addListenerForSingleValueEvent(new ValueEventListener() { //get data only once
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                int userNum = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    list.add(snapshot.getValue().toString());

                    UserHelperClassGadget userget = snapshot.getValue(UserHelperClassGadget.class);//get data store to class
//                    reference.child("userget").setValue(userget);
                    userDataGetId[userNum] = userget.getBtnID();
                    userDataGetName[userNum] = userget.getbtnName();
                    userDataGetValue[userNum] = userget.getbtnValue();
                    userNum++;

                }
                //userNum=0;
                for(int i=0; i<list.size();i++){ // list.size = userNum
                    addedWidget[i] = new Button(MainMenu.this);
                    addedWidget[i].setText(userDataGetName[i]);
                    addedWidget[i].setId(Integer.parseInt(userDataGetId[i]));

                    btnValue[i] = userDataGetValue[i];  //get button data

                    LinearLayout ll = (LinearLayout)findViewById(R.id.layout);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(10,10,10,10); //for better layout
                    ll.addView(addedWidget[i], lp); // add
                    addedWidget[i] = findViewById(Integer.parseInt(userDataGetId[i]));
                    addedWidget[i].setOnClickListener(MainMenu.this::onClick);// calling onClick() method for new button
                    if(btnValue[i].equals("1")){
                        activity.findViewById(addedWidget[i].getId()).setBackground(btnDraw);
                    }else{
                        activity.findViewById(addedWidget[i].getId()).setBackground(btnDraw1);
                    }

                    currentWidget = list.size();
                    System.out.println("currentWidget = "+currentWidget);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // END fetch data from firebase when first run

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.addWidget){
            widgetName_s = widgetName.getText().toString();
            addedWidget[currentWidget] = new Button(MainMenu.this);
            addedWidget[currentWidget].setText(widgetName_s);
            addedWidget[currentWidget].setId(BtnID[currentWidget]);


            btnValue[currentWidget] = "0";  //set data default for new button
            LinearLayout ll = (LinearLayout)findViewById(R.id.layout);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10,10,10,10); //for better layout
            ll.addView(addedWidget[currentWidget], lp);
            System.out.println("new button id = "+BtnID[currentWidget]);
            addedWidget[currentWidget] = findViewById(BtnID[currentWidget]);
            addedWidget[currentWidget].setOnClickListener(MainMenu.this);    // calling onClick() method for new button
            activity.findViewById(BtnID[currentWidget]).setBackground(btnDraw1);

            String widgetID  =String.valueOf(addedWidget[currentWidget].getId());   //int to string
            String widgetName = addedWidget[currentWidget].getText().toString();
            String widgetBtnValue = btnValue[currentWidget];
            UserHelperClassGadget helperClass =new UserHelperClassGadget(widgetID, widgetName, widgetBtnValue);
            reference.child(widgetID).setValue(helperClass);

            currentWidget++;
        }
        for(int i=0; i<currentWidget;i++){
            if(view.getId() ==BtnID[i]){
                String widgetID  =String.valueOf(addedWidget[i].getId());   //int to string
                String widgetName = addedWidget[i].getText().toString();
                String widgetBtnValue = btnValue[i];
                UserHelperClassGadget helperClass =new UserHelperClassGadget(widgetID, widgetName, widgetBtnValue);
                reference.child(widgetID).setValue(helperClass);

                if(widgetBtnValue.equals("1")){
                    btnValue[i]="0";
                    widgetBtnValue = btnValue[i];
                    helperClass =new UserHelperClassGadget(widgetID, widgetName, widgetBtnValue);

//                    activity.findViewById(BtnID[i]).setBackgroundColor(Color.parseColor("#f1cade"));//red
                    activity.findViewById(BtnID[i]).setBackground(btnDraw1);


                    reference.child(widgetID).setValue(helperClass);
                    System.out.println("gia tri button = 0");
                }else{
                    btnValue[i]="1";
                    widgetBtnValue = btnValue[i];   //REMEMBER to update widgetBtnValue before push on database
                    helperClass =new UserHelperClassGadget(widgetID, widgetName, widgetBtnValue);
//                    activity.findViewById(BtnID[i]).setBackgroundColor(Color.parseColor("#bcbbff"));//purple
                    activity.findViewById(BtnID[i]).setBackground(btnDraw);

                    reference.child(widgetID).setValue(helperClass);
                    System.out.println("gia tri button = 1");
                }
//                reference.child(widgetID).setValue(helperClass);
                System.out.println("new button "+i+" pushed");
            }
        }
    }
    public void gotoGesture(View view){
        Intent intent =new Intent(MainMenu.this,handGesture.class);
        startActivity(intent);
    }
}