package com.first_java_app.smarthrt;
//https://how2electronics.com/how-to-send-esp32-cam-captured-image-to-google-drive/
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.marcoscg.ipcamview.IPCamView;

public class ipCamere extends AppCompatActivity {
    WebView webView;
    String html;
    Button start;
    EditText URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_camere);
        start=findViewById(R.id.start);
        URL=findViewById(R.id.url);
        webView = (WebView) findViewById(R.id.webview);

        start.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String ipCamPath = URL.getText().toString();
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