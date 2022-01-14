package com.first_java_app.smarthrt;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{
    private static final String TAG="MainActivity";

    private Mat mRgba;
    private Mat mGray;
    private CameraBridgeViewBase mOpenCvCameraView;
    private objectDetectorClass objectDetectorClass;
    private String preAlpha, alpha;
    final Handler handler = new Handler();
    final Handler mHandler = new Handler();
    final int delay = 2000; //milliseconds
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    UserHelperClassGadget[] userGet;
    int size;
    TextView textPredict;

    final Runnable mshowPredict = new Runnable() {
        public void run() {
            showPredict();
        }
    };
    private BaseLoaderCallback mLoaderCallback =new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface
                        .SUCCESS:{
                    Log.i(TAG,"OpenCv Is loaded");
                    mOpenCvCameraView.enableView();
                }
                default:
                {
                    super.onManagerConnected(status);

                }
                break;
            }
        }
    };

    public CameraActivity(){
        Log.i(TAG,"Instantiated new "+this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int MY_PERMISSIONS_REQUEST_CAMERA=0;
        // if camera permission is not given it will ask for it on device
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(CameraActivity.this, new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }

        setContentView(R.layout.activity_main_gesture);
        textPredict=findViewById(R.id.showPredict);
        mOpenCvCameraView=(CameraBridgeViewBase) findViewById(R.id.frame_Surface);
        CameraActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCameraPermissionGranted();// must have since opencv version 4.xx or higher
        mOpenCvCameraView.setCvCameraViewListener(this);
        try{
            objectDetectorClass=new objectDetectorClass(getAssets(),"hand_model.tflite","hand_label.txt",300, "hand_gesture_model.tflite", 96);
            Log.d("MainActivity","Model is successfully loaded");
        }
        catch (IOException e){
            Log.d("MainActivity","Getting some error");
            e.printStackTrace();
        }

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("users").child(MainActivity.user_username_gadget).child("user's gadget");
        reference.addListenerForSingleValueEvent(new ValueEventListener() { //get data from firebase only once
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int userNum = 0;
                size = (int) dataSnapshot.getChildrenCount();
                userGet = new UserHelperClassGadget[size];
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    userGet[userNum] = snapshot.getValue(UserHelperClassGadget.class);//get data store to class
                    userNum++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        handler.postDelayed(new Runnable() {
            public void run() {
                //Toast.makeText(CameraActivity.this, "handle loop:"+alpha, Toast.LENGTH_SHORT).show();
                if(preAlpha!=null&&alpha!=null){
                    if(preAlpha.equals(alpha)){
                        //Toast.makeText(CameraActivity.this, "du 2 s:"+alpha, Toast.LENGTH_SHORT).show();
                        checkAndPushData();
                    }
                }
                preAlpha=alpha;
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()){
            //if load success
            Log.d(TAG,"Opencv initialization is done");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else{
            //if not loaded
            Log.d(TAG,"Opencv is not loaded. try again");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION,this,mLoaderCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView !=null){
            mOpenCvCameraView.disableView();
        }
    }

    public void onDestroy(){
        super.onDestroy();
        if(mOpenCvCameraView !=null){
            mOpenCvCameraView.disableView();
        }

    }

    public void onCameraViewStarted(int width ,int height){
        mRgba=new Mat(height,width, CvType.CV_8UC4);
        mGray =new Mat(height,width,CvType.CV_8UC1);
    }
    public void onCameraViewStopped(){
        mRgba.release();
    }
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        mRgba=inputFrame.rgba();
        mGray=inputFrame.gray();

        //call recognizeImage
        Mat out=new Mat();
        out=objectDetectorClass.recognizeImage(mRgba);
//        System.out.println("bien"+objectDetectorClass.alphaOut);
        alpha = objectDetectorClass.alphaOut;
        mHandler.post(mshowPredict);
        return out;
    }
    public void showPredict(){
        textPredict.setText("chưa thêm cử chỉ!!");
        for(int i =0; i<size;i++){
            if(userGet[i].getWidType().equals("button")){
                if(alpha!=null){

                    if(alpha.equals(String.valueOf(userGet[i].getGestureT().charAt(0)))){
                        textPredict.setText("giữ 2 giây để bật "+userGet[i].btnName);
                    }else if(alpha.equals(String.valueOf(userGet[i].getGestureT().charAt(1)))){
                        textPredict.setText("giữ 2 giây để tắt "+userGet[i].btnName);
                    }
                }
                else textPredict.setText("chưa phát hiện tay!!");
            }
        }
    }
    public void checkAndPushData(){
        for(int i =0; i<size;i++){
            if(userGet[i].getWidType().equals("button")){
                if(alpha.equals(String.valueOf(userGet[i].getGestureT().charAt(0)))){
                    Toast.makeText(CameraActivity.this, "đã bật: "+userGet[i].btnName, Toast.LENGTH_SHORT).show();
                    userGet[i].btnValue = "1";
                    reference.child(String.valueOf(i)).setValue(userGet[i]);
                }
                else if(alpha.equals(String.valueOf(userGet[i].getGestureT().charAt(1)))){
                    Toast.makeText(CameraActivity.this, "đã tắt: "+userGet[i].btnName, Toast.LENGTH_SHORT).show();
                    userGet[i].btnValue = "0";
                    reference.child(String.valueOf(i)).setValue(userGet[i]);
                }
            }
        }
    }
    public void backHome(View view) {
        Intent intent =new Intent(CameraActivity.this,MainMenu.class);
        startActivity(intent);
    }
    public void goAddGesture(View view) {
        Intent intent =new Intent(CameraActivity.this,addGesture.class);
        startActivity(intent);
    }
    public void goSwitchGesture(View view) {
        Intent intent =new Intent(CameraActivity.this,switchGesture.class);
        startActivity(intent);
    }
}