package com.first_java_app.smarthrt;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class objectDetectorClass {
    // should start from small letter

    // this is used to load hand model and predict
    private Interpreter interpreter;
    // create another interpreter for sign_language_model
    private Interpreter interpreter2;
    // this will load sign_language_model

    // store all label in array
    private List<String> labelList;
    private int INPUT_SIZE;
    private int PIXEL_SIZE=3; // for RGB
    private int IMAGE_MEAN=0;
    private  float IMAGE_STD=255.0f;
    // use to initialize gpu in app
    private GpuDelegate gpuDelegate;
    private int height=0;
    private  int width=0;
    private int Classification_Input_Size;
    private String defaultGesture = "chưa cài đặt";
    public String alphaOut;

    objectDetectorClass(AssetManager assetManager, String modelPath, String labelPath, int inputSize, String classification_model, int classification_input_size) throws IOException{
        INPUT_SIZE=inputSize;
        Classification_Input_Size = classification_input_size;
        // use to define gpu or cpu // no. of threads
        Interpreter.Options options=new Interpreter.Options();
        gpuDelegate=new GpuDelegate();
        options.addDelegate(gpuDelegate);
        options.setNumThreads(4); // set it according to your phone
        // loading model
        interpreter=new Interpreter(loadModelFile(assetManager,modelPath),options);
        // load labelmap
        labelList=loadLabelList(assetManager,labelPath);

        // code for load model
        Interpreter.Options options2 = new Interpreter.Options();
        // add 2 thread into this interpreter
        options2.setNumThreads(2);
        // load model
        interpreter2=new Interpreter(loadModelFile(assetManager, classification_model),options2);


    }

    private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
        // to store label
        List<String> labelList=new ArrayList<>();
        // create a new reader
        BufferedReader reader=new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
        String line;
        // loop through each line and store it to labelList
        while ((line=reader.readLine())!=null){
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private ByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        // use to get description of file
        AssetFileDescriptor fileDescriptor=assetManager.openFd(modelPath);
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset =fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declaredLength);
    }
    // create new Mat function
    public Mat recognizeImage(Mat mat_image){
        // Rotate original image by 90 degree get get portrait frame
        // This will fix crashing problem of the app
        Mat rotated_mat_image=new Mat();
        //Mat a=mat_image.t();
        //Core.flip(a,rotated_mat_image,1);
        // Release mat
        //a.release();
        rotated_mat_image = mat_image; // not rotate for landscape

        // if you do not do this process you will get improper prediction, less no. of object
        // now convert it to bitmap
        Bitmap bitmap=null;
        bitmap=Bitmap.createBitmap(rotated_mat_image.cols(),rotated_mat_image.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rotated_mat_image,bitmap);
        // define height and width
        height=bitmap.getHeight();
        width=bitmap.getWidth();

        // scale the bitmap to input size of model
         Bitmap scaledBitmap=Bitmap.createScaledBitmap(bitmap,INPUT_SIZE,INPUT_SIZE,false);

         // convert bitmap to bytebuffer as model input should be in it
        ByteBuffer byteBuffer=convertBitmapToByteBuffer(scaledBitmap);

        // defining output
        // 10: top 10 object detected
        // 4: there coordinate in image
      //  float[][][]result=new float[1][10][4];
        Object[] input=new Object[1];
        input[0]=byteBuffer;

        Map<Integer,Object> output_map=new TreeMap<>();
        // we are not going to use this method of output
        // instead we create treemap of three array (boxes,score,classes)

        float[][][]boxes =new float[1][10][4];
        // 10: top 10 object detected
        // 4: there coordinate in image
        float[][] scores=new float[1][10];
        // stores scores of 10 object
        float[][] classes=new float[1][10];
        // stores class of object

        // add it to object_map;
        output_map.put(0,boxes);
        output_map.put(1,classes);
        output_map.put(2,scores);

        // now predict
        interpreter.runForMultipleInputsOutputs(input,output_map);
        //      1. Loading tensorflow lite model
        //      2. Predicting object
        // we will draw boxes and label it with it's name

        Object value=output_map.get(0);
        Object Object_class=output_map.get(1);
        Object score=output_map.get(2);

        // loop through each object
        // as output has only 10 boxes
        alphaOut=null;
        for (int i=0;i<10;i++){
            // for here we are looping through each hand which is detected
            float class_value=(float) Array.get(Array.get(Object_class,0),i);
            float score_value=(float) Array.get(Array.get(score,0),i);
            // define threshold for score

            // Here you can change threshold according to your model
            // Now we will do some change to improve app

            if(score_value>0.5){
                Object box1=Array.get(Array.get(value,0),i);
                // we are multiplying it with Original height and width of frame

                // (x1,y1) is the starting point of hand
                // (x2,y2) is the end point of hand
                float y1=(float) Array.get(box1,0)*height;
                float x1=(float) Array.get(box1,1)*width;
                float y2=(float) Array.get(box1,2)*height;
                float x2=(float) Array.get(box1,3)*width;

                // set boundary limit
                if(x1<0) x1=0;
                if(y1<0) y1=0;
                if(x2>width) x2=width;
                if(y2>height) y2=height;
                // set height and width of box
                float w1=x2-x1;
                float h1=y2-y1;
                // crop hand image from original frame
                Rect cropped_roi=new Rect((int)x1,(int)y1,(int)w1,(int)h1);
                Mat cropped=new Mat(rotated_mat_image, cropped_roi).clone();
                // now convert this cropped Mat to Bitmap
                Bitmap bitmap1=null;
                bitmap1=Bitmap.createBitmap(cropped.cols(),cropped.rows(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(cropped,bitmap1);
                // resize bitmap1 to classification input size = 96
                Bitmap scaledBitmap1 = Bitmap.createScaledBitmap(bitmap1, Classification_Input_Size, Classification_Input_Size,false);
                // convert scaledBitmap1 to byte buffer
                ByteBuffer byteBuffer1 = convertBitmapToByteBuffer1(scaledBitmap1);
                // create an array for output of interpreter2
                float[][] output_class_value = new float[1][1];

                // predict output for byteBuffer1
                interpreter2.run(byteBuffer1, output_class_value);
                Log.d("objectDetectionClass", "output_class_value:" +output_class_value[0][0]);

                // convert output_class_value to alphabet
                String sign_val = getAlphabets(output_class_value[0][0]);
                alphaOut = sign_val;
                //              input/output        text            starting point              font size
                //Imgproc.putText(rotated_mat_image,""+sign_val,new Point(x1+10,y1+40),2,1.5,new Scalar(255, 255, 255, 255),2);
                Imgproc.rectangle(rotated_mat_image,new Point(x1,y1),new Point(x2,y2),new Scalar(0, 255, 0, 255),2);
            }

        }

        // before returning rotate back by -90 degree
        // Do same here
        //Mat b=rotated_mat_image.t();
        //Core.flip(b,mat_image,0);
        //b.release();
        //return mat_image;
        return rotated_mat_image;
    }

    private String getAlphabets(float sign_v) {
        String val="";
        if(sign_v>=-0.5 & sign_v<0.5){
            val = "A";
        }
        else if(sign_v>=0.5 & sign_v<1.5){
            val = "B";
        }
        else if(sign_v>=1.5 & sign_v<2.5){
            val = "C";
        }
        else if(sign_v>=2.5 & sign_v<3.5){
            val = "D";
        }
        else if(sign_v>=3.5 & sign_v<4.5){
            val = "E";
        }
        else if(sign_v>=4.5 & sign_v<5.5){
            val = "F";
        }
        else if(sign_v>=5.5 & sign_v<6.5){
            val = "G";
        }
        else if(sign_v>=6.5 & sign_v<7.5){
            val = "H";
        }
        else if(sign_v>=7.5 & sign_v<8.5){
            val = "I";
        }
        else if(sign_v>=8.5 & sign_v<9.5){
            val = "J";
        }
        else if(sign_v>=9.5 & sign_v<10.5){
            val = "K";
        }
        else if(sign_v>=10.5 & sign_v<11.5){
            val = "L";
        }
        else if(sign_v>=11.5 & sign_v<12.5){
            val = "M";
        }
        else if(sign_v>=12.5 & sign_v<13.5){
            val = "N";
        }
        else if(sign_v>=13.5 & sign_v<14.5){
            val = "O";
        }
        else if(sign_v>=14.5 & sign_v<15.5){
            val = "P";
        }
        else if(sign_v>=15.5 & sign_v<16.5){
            val = "Q";
        }
        else if(sign_v>=16.5 & sign_v<17.5){
            val = "R";
        }
        else if(sign_v>=17.5 & sign_v<18.5){
            val = "S";
        }
        else if(sign_v>=18.5 & sign_v<19.5){
            val = "T";
        }
        else if(sign_v>=19.5 & sign_v<20.5){
            val = "U";
        }
        else if(sign_v>=20.5 & sign_v<21.5){
            val = "V";
        }
        else if(sign_v>=21.5 & sign_v<22.5){
            val = "W";
        }
        else if(sign_v>=22.5 & sign_v<23.5){
            val = "X";
        }
        else val = "Y";
        return val;
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer;
        // some model input should be quant=0  for some quant=1
        // for this quant=0
        // Change quant=1
        // As we are scaling image from 0-255 to 0-1
        int quant=1;
        int size_images=INPUT_SIZE;
        if(quant==0){
            byteBuffer=ByteBuffer.allocateDirect(1*size_images*size_images*3);
        }
        else {
            byteBuffer=ByteBuffer.allocateDirect(4*1*size_images*size_images*3);
        }
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues=new int[size_images*size_images];
        bitmap.getPixels(intValues,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        int pixel=0;

        // some error
        //now run
        for (int i=0;i<size_images;++i){
            for (int j=0;j<size_images;++j){
                final  int val=intValues[pixel++];
                if(quant==0){
                    byteBuffer.put((byte) ((val>>16)&0xFF));
                    byteBuffer.put((byte) ((val>>8)&0xFF));
                    byteBuffer.put((byte) (val&0xFF));
                }
                else {
                    // paste this
                    byteBuffer.putFloat((((val >> 16) & 0xFF))/255.0f);
                    byteBuffer.putFloat((((val >> 8) & 0xFF))/255.0f);
                    byteBuffer.putFloat((((val) & 0xFF))/255.0f);
                }
            }
        }
    return byteBuffer;
    }
    private ByteBuffer convertBitmapToByteBuffer1(Bitmap bitmap) {
        ByteBuffer byteBuffer;
        int quant=1;
        int size_images=Classification_Input_Size;
        if(quant==0){
            byteBuffer=ByteBuffer.allocateDirect(1*size_images*size_images*3);
        }
        else {
            byteBuffer=ByteBuffer.allocateDirect(4*1*size_images*size_images*3);
        }
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues=new int[size_images*size_images];
        bitmap.getPixels(intValues,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        int pixel=0;
        // remove 255.0f as we did not scale the image
        for (int i=0;i<size_images;++i){
            for (int j=0;j<size_images;++j){
                final  int val=intValues[pixel++];
                if(quant==0){
                    byteBuffer.put((byte) ((val>>16)&0xFF));
                    byteBuffer.put((byte) ((val>>8)&0xFF));
                    byteBuffer.put((byte) (val&0xFF));
                }
                else {
                    byteBuffer.putFloat((((val >> 16) & 0xFF)));
                    byteBuffer.putFloat((((val >> 8) & 0xFF)));
                    byteBuffer.putFloat((((val) & 0xFF)));
                }
            }
        }
        return byteBuffer;
    }
}