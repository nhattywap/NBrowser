package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private EditText edit_txt;
    private WebView web_browser;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    DownloadManager dManager;
    static final String google = "https://www.google.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar;
        bar = getSupportActionBar();
        ColorDrawable cd = new ColorDrawable(Color.parseColor("#222222"));
        bar.setBackgroundDrawable(cd);

        edit_txt = findViewById(R.id.url_txt);
        web_browser = findViewById(R.id.web_browser);
        progressBar = findViewById(R.id.progressBar);
        linearLayout = findViewById(R.id.linearLayout);

        web_browser.setWebChromeClient(new WebChromeClient(){});
        web_browser.getSettings().setJavaScriptEnabled(true);
        web_browser.getSettings().setBuiltInZoomControls(true);
        web_browser.canGoBack();
        web_browser.loadUrl(google);

        web_browser.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility((view.VISIBLE));
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                linearLayout.setVisibility(view.GONE);
                progressBar.setVisibility(view.GONE);
                edit_txt.setText(url);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String urlSubstring = url.substring(url.lastIndexOf(".")+1).toLowerCase();
                if (urlSubstring.equals("pdf")){
                    dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(url);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
                    long reference = dManager.enqueue(request);

                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        web_browser.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress){
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        menu.findItem(R.id.menu_home).setTitle(Html.fromHtml("<font color='#f1f1f1'>Home</font>"));
        menu.findItem(R.id.menu_downloads).setTitle(Html.fromHtml("<font color='#f1f1f1'>Downloads</font>"));
        menu.findItem(R.id.menu_about).setTitle(Html.fromHtml("<font color='#f1f1f1'>About</font>"));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_back:
                go_back();
                break;

            case R.id.menu_forward:
                go_forward();
                break;

            case R.id.menu_refresh:
                web_browser.reload();
                break;

            case R.id.menu_search:
                if (linearLayout.getVisibility() == web_browser.VISIBLE){
                    linearLayout.setVisibility(web_browser.GONE);
                }else{
                    linearLayout.setVisibility(web_browser.VISIBLE);
                }
                break;

            case R.id.menu_home:
                web_browser.loadUrl(google);
                break;

            case  R.id.menu_downloads:
                Intent i = new Intent();
                i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
                startActivity(i);
                break;

            case R.id.menu_about:
                Intent about = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(about);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void search_web(View view){
        String stringUrl = edit_txt.getText().toString();
        if (!(stringUrl.contains("http") || stringUrl.contains("https"))){
            stringUrl = "https://" + stringUrl;
        }
        web_browser.loadUrl(stringUrl);
    }
    public void go_forward(){
        if(web_browser.canGoForward()){
            web_browser.goForward();
        }
    }
    public void go_back(){
        if (web_browser.canGoBack()){
            web_browser.goBack();
        }
    }

}