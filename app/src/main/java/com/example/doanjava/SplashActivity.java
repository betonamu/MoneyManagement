package com.example.doanjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.doanjava.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Start Main activity
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        //Close Splash Activity
        finish();
    }
}