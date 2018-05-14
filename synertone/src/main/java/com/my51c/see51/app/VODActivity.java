package com.my51c.see51.app;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.my51c.see51.BaseActivity;
import com.synertone.netAssistant.R;

import static com.my51c.see51.adapter.ViewFinder.findViewById;

public class VODActivity extends BaseActivity {

    WebView mWebView;
    private String url = "http://192.168.80.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.loadUrl(url);
    }


    public void onVodFinish(View view) {
        finish();
    }
}
