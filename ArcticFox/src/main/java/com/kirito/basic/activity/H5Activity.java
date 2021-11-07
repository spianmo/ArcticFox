package com.kirito.basic.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kirito.basic.App;
import com.kirito.basic.base.BaseActivity;
import com.kirito.basic.databinding.ActivityH5Binding;

import java.net.URISyntaxException;

/**
 * @author Finger
 */
public class H5Activity extends BaseActivity<ActivityH5Binding> {
    private String title;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        v.toolbar.setTitle(title);
        setSupportActionBar(v.toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initWebView();
    }

    void initWebView(){
        v.webview.getSettings().setDefaultTextEncodingName("utf-8");
        v.webview.getSettings().setJavaScriptEnabled(true);
        v.webview.getSettings().setSavePassword(true);
        v.webview.getSettings().setSaveFormData(true);
        v.webview.getSettings().setSupportZoom(false);
        v.webview.getSettings().setSupportMultipleWindows(true);
        v.webview.addJavascriptInterface(new JavaScriptObject(this), "myObj");
        v.webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (TextUtils.isEmpty(v.toolbar.getTitle())) {
                    v.toolbar.setTitle(title);
                }
            }
        });
        v.webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(handleSchemeUrl(url)){
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        try {
            LOGE(url);
            v.webview.loadUrl(url);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    boolean handleSchemeUrl(String schemeUrl){
        if (schemeUrl.startsWith("http") || schemeUrl.startsWith("https") || schemeUrl.startsWith("ftp")) {
            return false;
        }
        Intent intent;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException e) {
            return false;
        }
        intent.setComponent(null);
        try {
            this.startActivity(intent);
        }catch (ActivityNotFoundException e){
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (v.webview.canGoBack()) {
                v.webview.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    class JavaScriptObject {
        Context mContxt;

        public JavaScriptObject(Context mContxt) {
            this.mContxt = mContxt;
        }

        @JavascriptInterface
        public void exampleFunction(String arg) {

        }

    }
}