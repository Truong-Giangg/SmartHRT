package com.first_java_app.smarthrt;
//https://tutorialwing.com/create-android-scrollview-programmatically-android/
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

import java.io.IOException;
import java.io.InputStream;

public class gestureList extends AppCompatActivity implements View.OnClickListener{
    public static int[] gestureID = new int[25];
    public static String[] gestureType = new String[25];
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesturelist);
        ScrollView scrollView = new ScrollView(gestureList.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(layoutParams);

        LinearLayout linearLayout = new LinearLayout(gestureList.this);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(linearParams);

        scrollView.addView(linearLayout);
        int gId = 0;
        for(char c='A';c<='Y';++c){

            ImageView img = new ImageView(gestureList.this);

            Bitmap bm = getBitmapFromAsset("gesture/"+c+".jpg");

            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.setMargins(0, 30, 0, 30);
            params1.gravity = Gravity.CENTER;
            img.setLayoutParams(params1);
            linearLayout.addView(img);

            img.setImageBitmap(bm);


            img.setId(gId);
            gestureID[gId]=gId;
            gestureType[gId]=String.valueOf(c);
            img.setOnClickListener(gestureList.this);
            gId++;
        }
        LinearLayout linearLayout1 = findViewById(R.id.rootContainer);
        if (linearLayout1 != null) {
            linearLayout1.addView(scrollView);
        }
        Intent intent =getIntent();
        if(intent.getStringExtra("swgestureid")!=null){
            MainActivity.gestureChild =intent.getStringExtra("swgestureid");
            Toast.makeText(gestureList.this, "child:"+MainActivity.gestureChild, Toast.LENGTH_LONG).show();
        }
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("users").child(MainActivity.user_username_gadget).child("user's gadget");
    }
    @Override
    public void onClick(View view) {
        for(int i=0; i<25; i++){
            if(view.getId() == gestureID[i]){
                Toast.makeText(gestureList.this, "gesture ID: "+gestureID[i]+": "+gestureType[i], Toast.LENGTH_LONG).show();
                //reference.child(String.valueOf(MainActivity.gestureChild)).setValue(gestureType[i]);
            }
        }
    }
    private Bitmap getBitmapFromAsset(String paramString) {
        Object localObject = getResources().getAssets();
        try {
            Bitmap ret = BitmapFactory.decodeStream(((AssetManager)localObject).open(paramString));
            return ret;
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
//    public void goto(View view, int swID){
//        Intent intent =new Intent(getApplicationContext(),gestureList.class);
//        intent.putExtra("swgestureid", swID);
//        startActivity(intent);
//    }
}
