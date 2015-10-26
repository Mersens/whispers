package com.zzuli.whispers.main;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class QR_Code_Activity extends Activity {
	private WebView webView;
	private  ProgressBar progressBar;
	private String path;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.qr_code_activity);
		progressBar=(ProgressBar) findViewById(R.id.progressBar1);
		webView=(WebView) findViewById(R.id.schedule);
		path=getIntent().getStringExtra("path");
		initEvents();
	}
	private void initEvents() {
        WebSettings settings = webView.getSettings();  
        settings.setSupportZoom(true);       
        settings.setUseWideViewPort(true); 
        settings.setLoadWithOverviewMode(true); 
        webView.loadUrl(path);
        webView.setWebViewClient(new WebViewClient() {  
            @Override  
            public boolean shouldOverrideUrlLoading(WebView view, String url) {  
                view.loadUrl(url); 
                return true;    
            } 
        }); 
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    progressBar.setProgress(newProgress);
                    if (newProgress == 100) {
                     progressBar.setVisibility(View.GONE);
             }
            }
    });
	}
	
	
}
