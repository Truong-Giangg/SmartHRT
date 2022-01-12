package com.first_java_app.smarthrt;
import android.content.Intent;
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

public class removeWidget extends AppCompatActivity implements View.OnClickListener{
    Button rmWidget;
    EditText widgetNameRm;
    boolean swap=false;
    private int currentWidget;
    String widgetName_s;


    UserHelperClassGadget[] userGet;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removewidget);
        rmWidget = findViewById(R.id.removeWidget);
        widgetNameRm = findViewById(R.id.widgetNamerm);
        rmWidget.setOnClickListener(removeWidget.this);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("users").child(MainActivity.user_username_gadget).child("user's gadget");
        reference.addListenerForSingleValueEvent(new ValueEventListener() { //get data from firebase only once
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
                //Toast.makeText(removeWidget.this, "currentWidget: "+currentWidget, Toast.LENGTH_LONG).show();
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
            for(int i=0; i<currentWidget;i++){
                if(widgetName_s.equals(userGet[i].getbtnName())){ //delete child based on its name
                    //reference.child(userGet[i].getBtnID()).removeValue();
                    swap=true;
                }
                if(swap){ // create another child and move next data to it
                    if(i!=currentWidget-1){
                        UserHelperClassGadget movedChild =new UserHelperClassGadget(userGet[i].getBtnID(), userGet[i+1].getbtnName(), userGet[i+1].getbtnValue(),userGet[i+1].getWidType(),userGet[i+1].getGestureT(),userGet[i+1].getEspPin());
                        reference.child(String.valueOf(userGet[i].getBtnID())).setValue(movedChild);

                    }
                    else{
                        reference.child(String.valueOf(userGet[currentWidget-1].getBtnID())).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                gobackMainMenu(view);
                            }
                        });
                    }
                }
            }


        }
    }

    public void gobackMainMenu(View view){
        // hide virtual keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        Intent intent =new Intent(removeWidget.this,MainMenu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        removeWidget.this.finish();
    }
}
