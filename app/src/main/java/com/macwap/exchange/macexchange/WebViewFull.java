package com.macwap.exchange.macexchange;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class WebViewFull extends AppCompatActivity implements AdvancedWebView.Listener{


    private AdvancedWebView mWebView;
    DatabaseHelper myDB;
    SwipeRefreshLayout mySwipeRefreshLayout;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_full);



        mWebView = (AdvancedWebView) findViewById(R.id.myWebView);
        mWebView.setGeolocationEnabled(true);
        mWebView.addHttpHeader("uc", "macwapcom");
        mWebView.setListener(this, this);
        // mWebView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        mWebView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mySwipeRefreshLayout = (SwipeRefreshLayout)this.findViewById(R.id.swipeContainer);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mWebView.reload();
                    }
                }
        );

        final ProgressBar Pbar;
        Pbar = (ProgressBar) findViewById(R.id.pB1);
        Pbar.setVisibility(ProgressBar.GONE);


        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if(progress < 100 && Pbar.getVisibility() == ProgressBar.GONE){
                    Pbar.setVisibility(ProgressBar.VISIBLE);
                }

                Pbar.setProgress(progress);
                if(progress == 100) {

                    mySwipeRefreshLayout.setRefreshing(false);
                    Pbar.setVisibility(ProgressBar.GONE);

                }

            }
        });









        Intent intent = getIntent();

        String id = intent.getStringExtra("url");
        if(id.equals(""))
        {}else
        {   mWebView.loadUrl(id);

        }




    }

    @Override
    public void onBackPressed() {

        if (!mWebView.onBackPressed()) {
            return;
        } else {
                super.onBackPressed();
            }

    }

    public void clicid(View view){

        String id= String.valueOf(view.getTag());
if(id.equals("close"))
{

    finish();

}

    }






    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

    }





    @Override
    public void onExternalPageRequest(String url) {

    }
}
