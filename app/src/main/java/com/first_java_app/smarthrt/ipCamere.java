package com.first_java_app.smarthrt;
//https://how2electronics.com/how-to-send-esp32-cam-captured-image-to-google-drive/
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marcoscg.ipcamview.IPCamView;

import java.util.ArrayList;
import java.util.List;

public class ipCamere extends AppCompatActivity {
    WebView webView;
    String html;
    Button start;
    AutoCompleteTextView URL;
    List<String>arr=new ArrayList<>();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").child(MainActivity.user_username_gadget).child("IPlist");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_camere);
        start=findViewById(R.id.start);
        URL=findViewById(R.id.url);
        webView = (WebView) findViewById(R.id.webview);
        getArr();
        URL.setAdapter(getAdapter());
        start.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String ipCamPath = URL.getText().toString();
                 updateIPlist(ipCamPath);
                 String imgHtml = "<img src=\"http://"+ipCamPath+"/\">\n";
                 html = "<!DOCTYPE html>\n" +
                         "<html>\n" +
                         "<head>\n" +
                         "\t<title></title>\n" +
                         "</head>\n" +
                         "<body>\n" +
                         imgHtml+
                         "</body>\n" +
                         "</html>";
                 webView.getSettings().setJavaScriptEnabled(true);
                 openWebView();
                 // hide virtual keyboard
                 InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                 imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

             }
         });

    }
    private void updateIPlist(String newIp){
        for(String a:arr){
            if(a.equals(newIp)){
                return ;
            }
        }
        arr.add(newIp);
        myRef.setValue(arr);

    }
    private void getArr(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    arr.clear();
                    for(DataSnapshot dss:snapshot.getChildren()){
                        String ip=dss.getValue(String.class);
                        arr.add(ip);
                    }
                }
               /* 
                StringBuilder stringBuilder=new StringBuilder();
                for (int i=0;i<arr.size();i++){
                    stringBuilder.append(arr.get(i)+"\n");
                }
                Toast.makeText(getApplicationContext(),stringBuilder.toString(),Toast.LENGTH_LONG).show();

                */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private ArrayAdapter<String> getAdapter(){
        return new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arr);
    }
    @SuppressLint("NewApi")
    private void openWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        webView.loadDataWithBaseURL("file:///android_asset/", getHtmlData(html), "text/html", "utf-8", null);
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }
}