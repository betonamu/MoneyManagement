package com.example.doanjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Start Main activity
        startActivity(new Intent(SplashActivity.this,MainActivity.class));

        //close Splash Activity
        finish();
    }
}