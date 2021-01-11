package com.example.doanjava.ui.notifications;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doanjava.R;
import com.google.firebase.auth.FirebaseAuth;

public class InformationUserActivity extends AppCompatActivity {
    Button btnChangePassword,btnUpdateBalance;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnChangePassword = (Button) findViewById(R.id.btn_change_password_user_activity);
        btnUpdateBalance = (Button) findViewById(R.id.btn_update_balance_user_activity);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationUserActivity.this,ChangePasswordActivity.class));
            }
        });

        btnUpdateBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationUserActivity.this,UpdateBalanceActivity.class));
            }
        });
    }

    //Back to previous fragment when press back button in Actionbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
