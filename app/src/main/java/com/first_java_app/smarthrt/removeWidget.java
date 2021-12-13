package com.first_java_app.smarthrt;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class removeWidget extends AppCompatActivity implements View.OnClickListener{
    Button rmWidget;
    EditText widgetNameRm;
    boolean swap=false;
    public static int currentWidget = 0;
    String widgetName_s;

    int widgetId;
    ArrayList<String> list = new ArrayList<>();
    String[] userDataGetId = new String[8];
    String[] userDataGetName = new String[8];
    String[] userDataGetValue = new String[8];
    String[] userDataGetType = new String[8];

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removewidget);
        rmWidget = findViewById(R.id.removeWidget);
        widgetNameRm = findViewById(R.id.widgetNamerm);
        rmWidget.setOnClickListener(removeWidget.this);
        //--------------fetch data from previous activity----------
        reference = FirebaseDatabase.getInstance().getReference("users");
        Intent intent =getIntent();
        if(intent.getStringExtra("username")!=null){
            MainActivity.user_username_gadget =intent.getStringExtra("username");
        }
        //--------------end fetch data from previous activity----------
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("users").child(MainActivity.user_username_gadget).child("user's gadget");
        reference.addListenerForSingleValueEvent(new ValueEventListener() { //get data from firebase only once
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                int userNum = 1;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    list.add(snapshot.getValue().toString());
                    UserHelperClassGadget userget = snapshot.getValue(UserHelperClassGadget.class);//get data store to class
                    userDataGetId[userNum] = userget.getBtnID();
                    userDataGetName[userNum] = userget.getbtnName();
                    userDataGetValue[userNum] = userget.getbtnValue();
                    userDataGetType[userNum] = userget.getWidType();
                    userNum++;
                }
                currentWidget = list.size();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.removeWidget){   // remove button
            widgetName_s = widgetNameRm.getText().toString();
            for(int i=1; i<=currentWidget;i++){
                if(widgetName_s.equals(userDataGetName[i])){ //delete child based on its name
                    reference.child(userDataGetId[i]).removeValue();
                    swap=true;
                }
                if(swap){ // create another child and move next data to it
                    UserHelperClassGadget helperClass =new UserHelperClassGadget(userDataGetId[i], userDataGetName[i+1], userDataGetValue[i+1],userDataGetType[i+1]);
                    reference.child(String.valueOf(i)).setValue(helperClass);
                }
            }
            reference.child(String.valueOf(userDataGetId[currentWidget])).removeValue();// delete the last child
            gobackMainMenu(view);
        }
    }
    public void gobackMainMenu(View view){
        //--------------push data to MainMenu acctivity via username------------
        Intent intent =new Intent(getApplicationContext(),MainMenu.class);
        intent.putExtra("username",MainActivity.user_username_gadget);
        startActivity(intent);
        //--------------end push data to MainMenu acctivity via username------------
    }
}
