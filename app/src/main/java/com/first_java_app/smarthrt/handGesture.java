package com.first_java_app.smarthrt;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;
import org.opencv.utils.Converters;

import java.io.File;

public class handGesture extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;

    private TextView numberOfFingersText = null;
    int numberOfFingers = 0;
    float p=0;

    private Mat                    mRgba;
    private Mat                    mGray;
    private Mat                    samples;
    private Mat                    skinRegionHSV;
    private Mat                    blurred;
    private Mat                    thresh;
    private Mat                     trainData;
    private Mat                     testData;

    private Mat                     res;
    private Mat                     hsvO;
    private Scalar               	upper;
    private Scalar               	lower;
    private List<Integer> trainLabs;

    private KNearest                knn;
    final Handler mHandler = new Handler();

    final Runnable mUpdateFingerCountResults = new Runnable() {
        public void run() {
            updateNumberOfFingers();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //----------read and write permission----------------
        if (ContextCompat.checkSelfPermission(handGesture.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(handGesture.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(handGesture.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(handGesture.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(handGesture.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(handGesture.this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        //----------END read and write permission----------------
        setContentView(R.layout.activity_main_gesture);
        numberOfFingersText = (TextView) findViewById(R.id.numberOfFingers);
        handGesture.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        numberOfFingersText = (TextView) findViewById(R.id.numberOfFingers);
        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.myCameraView);
        cameraBridgeViewBase.setMaxFrameSize(640, 480);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCameraPermissionGranted();// must have since opencv version 4.xx or higher
        cameraBridgeViewBase.setCvCameraViewListener(handGesture.this);


        baseLoaderCallback = new BaseLoaderCallback(handGesture.this) {
            @Override
            public void onManagerConnected(int status) {

                switch (status){
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
        if(OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(), "OpenCV loaded succesfully",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Couldn't load OpenCV",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();


        //----------SkinMask processing------------
        Imgproc.cvtColor(mRgba, samples, Imgproc.COLOR_RGB2HSV);
        Core.inRange(samples, lower, upper, skinRegionHSV);
        Imgproc.blur(skinRegionHSV, blurred, new org.opencv.core.Size(2, 2));
        Imgproc.threshold(blurred, thresh, 0, 255, Imgproc.THRESH_BINARY);
        hsvO = thresh;
        //----------END SkinMask processing------------
        // we need float data for knn:
        thresh.convertTo(thresh, CvType.CV_32F);
        // for opencv ml, each feature has to be a single row:
        //testData.push_back(thresh.reshape(1,1));


        p = knn.findNearest(thresh.reshape(1,1), 1, res);
//        System.out.println(p + " " + res.dump()); // p or res.dump() = 1 -> 1 finger
        this.numberOfFingers = (int)p;
        mHandler.post(mUpdateFingerCountResults);
//        return mRgba;
        return skinRegionHSV;
    }
    public void updateNumberOfFingers(){
        numberOfFingersText.setText(String.valueOf(handGesture.this.numberOfFingers));
    }
    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat(width,height, CvType.CV_8UC4);
        mRgba = new Mat(width,height, CvType.CV_8UC4);
        samples = new Mat();
        skinRegionHSV = new Mat();
        blurred = new Mat();
        thresh = new Mat();
        res = new Mat();
        lower = new Scalar(0, 0.02*255, 0);  // to pick hands color range
        upper = new Scalar(30, 0.68*255, 255);
        hsvO = new Mat();


        trainData = new Mat();
        trainLabs = new ArrayList<Integer>();
        testData = new Mat();
        for(int r=0; r<2;r++){  // 5 signs  , r<2 for testing purpose, r<5 is the origin
            for(int c=0; c<40;c++){ // 40 samples per sign, sample 40 for test purpose
//                Scalar lower = new Scalar(0, 0.28*255, 0);  // to pick hands color range
//                Scalar upper = new Scalar(25, 0.68*255, 255);
                String imgInput = "/storage/emulated/0/Pictures/"+r+"/"+c+".jpg";

                //----------SkinMask processing------------
                Imgproc.cvtColor(Imgcodecs.imread(imgInput), samples, Imgproc.COLOR_BGR2HSV);
                Core.inRange(samples, lower, upper, skinRegionHSV);
                Imgproc.blur(skinRegionHSV, blurred, new org.opencv.core.Size(2, 2));
                Imgproc.threshold(blurred, thresh, 0, 255, Imgproc.THRESH_BINARY);
                //----------END SkinMask processing------------

                //----------prepare to train using knn----------
                // we need float data for knn:
                thresh.convertTo(thresh, CvType.CV_32F);

                // for opencv ml, each feature has to be a single row:
                trainData.push_back(thresh.reshape(1,1));
                // add a label for that feature (the digit number):
                trainLabs.add(r);
                //----------END prepare to train using knn----------
            }
        }
        //------------train knn object----------
        knn = KNearest.create();   // make a Mat of the train labels, and train knn:
        knn.train(trainData, Ml.ROW_SAMPLE, Converters.vector_int_to_Mat(trainLabs));
        //------------END train knn object----------
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"there is a problem in opencv!!",Toast.LENGTH_SHORT).show();
        }else{
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }
    public void backHome(View view) {
        Intent intent =new Intent(handGesture.this,MainMenu.class);
        startActivity(intent);
    }
}