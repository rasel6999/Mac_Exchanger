package com.macwap.exchange.macexchange;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,AdvancedWebView.Listener  {
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1,PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private AdvancedWebView mWebView;
    DatabaseHelper myDB;
    SwipeRefreshLayout mySwipeRefreshLayout;

    private WebView  xmWebView;
    Handler h = new Handler();
    int delay = 1; //.1 seconds
    int delay2 = 20; //.1 seconds
    Runnable runnable;
    String agent ="exchanger_app",site_url;
    ProgressBar Pbar;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        site_url= getString(R.string.site_url);
        mWebView = (AdvancedWebView) findViewById(R.id.myWebView);
        mWebView.setGeolocationEnabled(true);
        mWebView.addHttpHeader("uc", "macwapcom");
        mWebView.setListener(this, this);
        mWebView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        mWebView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mySwipeRefreshLayout = (SwipeRefreshLayout)this.findViewById(R.id.swipeContainer);
        mWebView.getSettings().setUserAgentString(agent);
//      mWebView.setBackgroundColor(0);
        mWebView.getSettings().setSaveFormData(false);
        load_user_data("",""); //user info receive
        CookieManager.getInstance().setCookie(site_url, "app=exchanger_app");
        Pbar = (ProgressBar) findViewById(R.id.pB1);
        Pbar.setVisibility(ProgressBar.GONE);
        myDB        =new DatabaseHelper(this);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mWebView.reload();
                    }
                }
        );





        //*************************************************************************************|||||||||||||||||||||||||||




        Cursor res = myDB.getsingleDataFromurl(site_url);
        if (res.getCount() == 0)
        {
            runupdate("",site_url);

        }
        else {

            StringBuffer buffer = new StringBuffer();

            while (res.moveToNext())
            {
                runupdate("",site_url);

                //load_data("http://exchange.macwap.com/",res.getString(2),true);
            };

        }



        if (getIntent().getExtras() != null) {

            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);

                if (key.equals("url") && value != null) {


                    runupdate("",value);

                }else {


                }
            }
        }














/* this file for load data*/
        mWebView.addJavascriptInterface(new Object()
        {
            @JavascriptInterface           // For API 17+
            public void performClick(final String html,final String xurl)
            {
                h.postDelayed(new Runnable() {
                    public void run() {
                        Cursor res = myDB.getsingleDataFromurl(xurl);
                        if (res.getCount() == 0) {
                            String rj = String.valueOf(insartdata(xurl, html, "", "", "", String.valueOf(System.currentTimeMillis()), "", "macwap.com", "", xurl, "0"));
                        }else{
                            String rj = String.valueOf(myDB.updateDoc(xurl,html));

                        }
                        load_data(xurl,html,false);

                    }
                }, delay);
            }
        }, "ok");






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






        mWebView.setWebViewClient(new WebViewClient()
        {
                /*
                @Override
                public boolean shouldOverrideUrlLoading(final WebView xview, final String xurl) {


                    URL url = null;
                    try {
                        url = new URL(xurl);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    String host = url.getHost();

                    if(host.equals("exchange.macwap.com"))
                         {

                         }
                    else {
                          return false;
                         }






                    Cursor res = myDB.getsingleDataFromurl(xurl);




                if (!DetectConnection.checkInternetConnection(MainActivity.this)) {

                    if (res.getCount() == 0) {

                        Toast.makeText(getApplicationContext(), "No data for  Offline !", Toast.LENGTH_SHORT).show();
                       }   else {
                        StringBuffer buffer = new StringBuffer();

                        while (res.moveToNext()) {
                            load_data(xurl,res.getString(2),true);
                        };

                    }


                } else { //for online work



                    if (!DetectConnection.checkInternetConnection(MainActivity.this)) {
                        Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_SHORT).show();
                    } else {


                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... voids) {
                                String html = "error on loding data ! please contact with @rdxrasel";
                                try {
                                    Document document = Jsoup.connect(xurl)
                                            .followRedirects(true)
                                            .userAgent(agent)
                                            .cookie("username","1")
                                            .get();
                                    /// Element elements=document.select("div.news-list").first();
                                    html = document.toString();
                                } catch (IOException e) {
                                    Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                                return html;
                            }

                            @Override
                            protected void onPostExecute(String html) {
                                String mime = "text/html; charset=utf-8";
                                String encoding = "utf-8";

                                Cursor res = myDB.getsingleDataFromurl(xurl);
                                if (res.getCount() == 0) {

                                    //insart data
                                    String rj = String.valueOf(insartdata(xurl, html, "", "", "", String.valueOf(System.currentTimeMillis()), "", "macwap.com", "", xurl, "0"));
                                   // Toast.makeText(getApplicationContext(), "1"+rj, Toast.LENGTH_SHORT).show();
                                }else{


                                    String rj = String.valueOf(myDB.updateDoc(xurl,html));
                                   // Toast.makeText(getApplicationContext(), "2"+rj, Toast.LENGTH_SHORT).show();

                                    }

                           //load_data(xurl, html,false);

                            }
                        }.execute();

                        return false;

                    }
                }

               // return super.shouldOverrideUrlLoading(xview, xurl);
                return true;


            }

     */   });












        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {

        if (!mWebView.onBackPressed()) { return; }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private boolean insartdata(String s_id, String s_message,String s_by, String s_type, String s_value ,String s_time, String  s_likes,String s_title , String s_category,String s_url,String s_remove) {

        myDB  =new DatabaseHelper(this);
        boolean isInserted = myDB.insertData(s_id, s_message, s_by, s_type, s_value, s_time, s_likes, s_title, s_category, s_url, s_remove);


        return isInserted;
    }



    public void load_data(String url,String webData,boolean isview ) {

        TextView titlemain = (TextView) findViewById(R.id.toolbar_title);
        Document    doc       = Jsoup.parse(webData);
        Elements    text      = doc.select("div.notice1");
        Elements    username  = doc.select("div.user_name");
        Elements    useremail  = doc.select("div.user_email");
        Elements    userprofile  = doc.select("div.user_profile");
        Elements    userpadmin = doc.select("div.user_admin");
        Elements    usercover = doc.select("div.user_cover");

        URL hosturl = null;
        try {
            hosturl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String host = hosturl.getHost();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ImageView   xprofile   = (ImageView)    headerView.findViewById(R.id.profile_image);
        TextView    sEtname    = (TextView)    headerView.findViewById(R.id.userID);
        TextView    sEtemail   = (TextView)    headerView.findViewById(R.id.userEmail);
        ImageView cover_image  = (ImageView)    headerView.findViewById(R.id.cover_image);




        //Toast.makeText(getApplicationContext(),useremail.text(), Toast.LENGTH_SHORT).show();


        //Macwap_DB.setString();

        //  webData =  webData.replaceAll("<nav class=\"navbar navbar-default navbar-static-top ex_navbar\">", "<nav class=\"navbar navbar-default navbar-static-top ex_navbar\" style=\"display:none;\"> ");
        // webData =  webData.replaceAll("<div class=\"ex_header\">", "<div class=\"ex_header\" style=\"display:none;\"> ");
        webData =  webData.replaceAll(site_url+"assets/", "file:///android_asset/assets/");
        // webData =  webData.replaceAll("<script type=\"text/javascript\" src=\"https://cdn.ywxi.net/js/1.js\" async></script>", "");
        //  webData =  webData.replaceAll("https://embed.tawk.to/5a34cfa2bbdfe97b137fbd7a/default", "");
        // webData =  webData.replaceAll("<script type=\"text/javascript\" src=\"https://cdn.trustedsite.com/js/1.js\" async></script>", "");
        //  webData =  webData.replaceAll("<div class=\"notice1\" id=\"notice1\">", "<div class=\"notice1\" style=\"display:none;\">");













        if(isview){
            mWebView.loadDataWithBaseURL(site_url, webData , "text/html", "utf-8", url);
        }else if(host.equals("exchange.macwap.com")) {


            if(userpadmin.text().equals("1"))
            {
                IsAdmin(true);
                Macwap_DB.setString(this,"admin","true");

            } else{
                IsAdmin(false);
                Macwap_DB.setString(this,"admin","false");

            }
            if(text.text().equals(""))
            {

            } else{

                String      title     = text.text();
                titlemain.setText(title.replaceAll("Alert :", " "));
                Macwap_DB.setString(this,"title",(String) title.replaceAll("Alert :", " "));

            }


            if(usercover.text().equals("")) {

            }else{

                Macwap_DB.setString(this,"cover",(String) usercover.text().replaceAll("./", ""));
               setImg(xprofile,site_url+""+usercover.text());

            }



            if(userprofile.text().equals("")) {
            }else{
                Macwap_DB.setString(this,"profile",(String) userprofile.text());
              setImg(xprofile,site_url+""+userprofile.text());
            }

            if(username.text().equals("")) {
                mdrasel(false,url);


            }else{
                Macwap_DB.setString(this,"name",(String) username.text());

                mdrasel(true,url);
                sEtname.setText(username.text());
            }



            if(useremail.text().equals("")) {} else{
                Macwap_DB.setString(this,"email",(String) useremail.text());
                sEtemail.setText(useremail.text());
            }


        }


    }

    public static class DetectConnection {
        public static boolean checkInternetConnection(Context context) {

            ConnectivityManager con_manager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            return (con_manager.getActiveNetworkInfo() != null
                    && con_manager.getActiveNetworkInfo().isAvailable()
                    && con_manager.getActiveNetworkInfo().isConnected());
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return        super.onPrepareOptionsMenu(menu);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.signin) {

            runupdate("",site_url+"login");

        }else if  (id == R.id.signup)
        {
            runupdate("",site_url+"register");


        }else if  (id == R.id.profile)
        {
            runupdate("",site_url+"account/exchanges");


        }else if  (id == R.id.home)
        {
            runupdate("",site_url);


        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.nav_camera) {

            runupdate("",site_url);

        }  else if (id == R.id.nav_manage) {

            runupdate("","https://tawk.to/chat/5a34cfa2bbdfe97b137fbd7a/default/?$_tawk_popout=true");


        } else if (id == R.id.nav_settings) {
            Intent myIntent = new Intent(MainActivity.this, settings.class);
            MainActivity.this.startActivity(myIntent);

        } else if (id == R.id.freebitcoin) {
            Intent myIntent = new Intent(MainActivity.this, WebViewFull.class);
            myIntent.putExtra("url", "https://freebitco.in/?r=5488959"); //Optional parameters
            MainActivity.this.startActivity(myIntent);

        } else if (id == R.id.freedogicoin) {

            Intent myIntent = new Intent(MainActivity.this, WebViewFull.class);
            myIntent.putExtra("url", "http://freedoge.co.in/?r=1025471"); //Optional parameters
            MainActivity.this.startActivity(myIntent);

        } else if (id == R.id.mchat) {

            runupdate("","https://dashboard.tawk.to/login");
        }  else if (id == R.id.admin) {

            runupdate("",site_url+"admin/");

        } else if (id == R.id.nav_logout) {

            runupdate("",site_url+"logout");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }



    @Override
    public void onPageStarted(String url, Bitmap favicon) {


        if(url.matches("(.*)/account/settings(.*)")) {

            if (Build.VERSION.SDK_INT >= 23) {


                List<String> listPermissionsNeeded = new ArrayList<>();

                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }


                if (!listPermissionsNeeded.isEmpty()) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                }




            }

        }



    }


    @Override
    public void onPageFinished(String url) {

        mWebView.loadUrl("javascript:ok.performClick('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>' ,'"+url+"');");


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

    public void mdrasel(boolean istrue,String url) {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu xmenu = navigationView.getMenu();





        MenuItem aitem = menu.findItem(R.id.signin);
        MenuItem bitem = menu.findItem(R.id.signup);
        MenuItem citem = menu.findItem(R.id.profile);
        MenuItem ditem = menu.findItem(R.id.home);
        MenuItem eitem = xmenu.findItem(R.id.nav_logout);

        if(istrue){


            if(url.matches("(.*)/account/(.*)"))
            {
                ditem.setVisible(true);
                citem.setVisible(false);


            }else {
                citem.setVisible(true);
                ditem.setVisible(false);


            }

            aitem.setVisible(false);
            bitem.setVisible(false);
            eitem.setVisible(true);

        } else{


            aitem.setVisible(true);
            bitem.setVisible(true);
            citem.setVisible(false);
            ditem.setVisible(false);
            eitem.setVisible(false);

        }
    }
    public void toast (String title,String url ) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.cred_menu_like_popup, (ViewGroup) findViewById(R.id.like_popup_layout));


        ImageView imageView = (ImageView) layout.findViewById(R.id.like_popup_iv);
        int src =  getResources().getIdentifier("com.macwap.exchange.macexchange:drawable/"+url, null, null);
        imageView.setImageResource(src);
        TextView text = (TextView) layout.findViewById(R.id.like_popup_tv);
        text.setText(title);

        Toast toast = new Toast( getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        toast.show();


    }

    public  void runupdate (String title,String url ) {
        Pbar = (ProgressBar) findViewById(R.id.pB1);
        Pbar.setVisibility(ProgressBar.VISIBLE);

        mWebView.loadUrl("javascript:window.location.href = \""+url+"\";");

    }

    public  void IsAdmin (boolean isvisible ) {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu xmenu = navigationView.getMenu();
        MenuItem aitem = xmenu.findItem(R.id.admin);
        MenuItem bitem = xmenu.findItem(R.id.mchat);


        if (isvisible) {

            aitem.setVisible(true);
            bitem.setVisible(true);

        }else{
            aitem.setVisible(false);
            bitem.setVisible(false);

        }
    }




    private void setImg (ImageView id,String url ) {
/*
        Picasso.with(this)
                .load(url)
                .placeholder(R.drawable.preload)
                .error(R.drawable.cover)
                .into(id);





        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);



        ImageLoader.getInstance().displayImage(url,
                id, new ImageLoadingListener() {


                    @Override
                    public void onLoadingStarted(String s, View view) {

                        toast("start","green_favorite");
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        toast(String.valueOf(failReason),"error");

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                        toast("Permission Granted","green_favorite");

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
*/

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            toast("Permission Granted","green_favorite");

        } else {

            toast("Permission denied","error");

        }
        return;
    }

    public void load_user_data (String a, String b) {


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ImageView   xprofile   = (ImageView)    headerView.findViewById(R.id.profile_image);
        TextView    sEtname    = (TextView)    headerView.findViewById(R.id.userID);
        TextView    sEtemail   = (TextView)    headerView.findViewById(R.id.userEmail);
        ImageView cover_image  = (ImageView)    headerView.findViewById(R.id.cover_image);
        TextView titlemain = (TextView) findViewById(R.id.toolbar_title);





        String title       = Macwap_DB.getString(this,"title");
        String name       = Macwap_DB.getString(this,"name");
        String email       = Macwap_DB.getString(this,"name");
        String profile       = Macwap_DB.getString(this,"profile");
        String cover       = Macwap_DB.getString(this,"cover");
        String admin       = Macwap_DB.getString(this,"admin");

        if(title==null||title.equals("")){}else{
            titlemain.setText(title);
        }   if(name==null||name.equals("")){}else{
            sEtname.setText(name);
        }   if(email==null||email.equals("")){}else{
            sEtemail.setText(email);
        }   if(profile==null||profile.equals("")){}else{
          setImg(xprofile,site_url+""+profile);
        }   if(cover==null||cover.equals("")){

            setImg(cover_image,"file:///android_asset/cover.jpg");

        }else{
          setImg(cover_image,site_url+""+cover);

        }   if(admin.equals("true")){
            IsAdmin(true);

        }



    }

}


