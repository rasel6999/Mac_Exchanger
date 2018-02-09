package com.macwap.exchange.macexchange;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Toast;

public class settings extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void clickUrl(View view){

      String url= String.valueOf(view.getTag());


        Intent myIntent = new Intent(settings.this, MainActivity.class);

        myIntent.putExtra("url", url); //Optional parameters

        settings.this.startActivity(myIntent);
    }
}
