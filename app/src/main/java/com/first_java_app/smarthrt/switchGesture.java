package com.first_java_app.smarthrt;

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
import android.widget.SeekBar;
import android.widget.Switch;
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

public class switchGesture extends AppCompatActivity{
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    public static int[] swID;
    public static int size;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switchgesture);


        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("users").child(MainActivity.user_username_gadget).child("user's gadget");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                size = (int) dataSnapshot.getChildrenCount();
                swID = new int[size];
                int userNum = 0;

                ScrollView scrollView = new ScrollView(switchGesture.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                scrollView.setLayoutParams(layoutParams);
                LinearLayout linearLayout = new LinearLayout(switchGesture.this);
                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setLayoutParams(linearParams);
                scrollView.addView(linearLayout);

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserHelperClassGadget userget = snapshot.getValue(UserHelperClassGadget.class);//get data store to class
                    if(userget.getWidType().equals("button")){
                        TextView gadgetList = new TextView(switchGesture.this);
                        gadgetList.setText(userget.getbtnName());
                        gadgetList.setId(Integer.parseInt(userget.getBtnID()));
                        swID[userNum] = Integer.parseInt(userget.getBtnID());

                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        params1.setMargins(0, 30, 0, 30);
                        params1.gravity = Gravity.CENTER;
                        gadgetList.setGravity(Gravity.CENTER);
                        gadgetList.setLayoutParams(params1);
                        linearLayout.addView(gadgetList);

                        for(int i =0; i<2;i++){
                            ImageView img = new ImageView(switchGesture.this);
                            Bitmap bm = getBitmapFromAsset("gesture/"+userget.getGestureT().charAt(i)+".jpg");
                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params2.setMargins(0, 30, 0, 30);
                            params2.gravity = Gravity.CENTER;
                            img.setLayoutParams(params2);
                            linearLayout.addView(img);
                            img.setImageBitmap(bm);
                        }
                        userNum++;
                    }
                }
                LinearLayout linearLayout1 = findViewById(R.id.rootContainer);
                if (linearLayout1 != null) {
                    linearLayout1.addView(scrollView);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
//    public void gotoGestureList(View view, int swID){
//        Intent intent =new Intent(getApplicationContext(),gestureList.class);
//        MainActivity.gestureChild = String.valueOf(swID);
//        intent.putExtra("swgestureid", MainActivity.gestureChild);
//        startActivity(intent);
//    }
}
