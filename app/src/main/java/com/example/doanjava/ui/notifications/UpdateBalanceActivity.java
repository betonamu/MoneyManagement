package com.example.doanjava.ui.notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doanjava.MainActivity;
import com.example.doanjava.R;
import com.example.doanjava.common.GlobalConst;
import com.example.doanjava.common.GlobalFuc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateBalanceActivity extends AppCompatActivity {
    Button btnUpdateBalance;
    EditText txtBalanceValue;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_balance);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnUpdateBalance = (Button) findViewById(R.id.btn_update_balance);
        txtBalanceValue = (EditText) findViewById(R.id.txt_update_balance);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        btnUpdateBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(txtBalanceValue.getText().toString().trim())){
                    GlobalFuc.DialogShowMessage(UpdateBalanceActivity.this,GlobalConst.AppTitle,"Vui long nhap so du de cap nhat!");
                    return;
                }
                String[] splitBalance = txtBalanceValue.getText().toString().split(" ");

                /*input value have ","
                need remove "," character to parse double*/
                double balanceUpdateValue = Double.parseDouble(splitBalance[1].replace(",",""));
                UpdateBalanceForCurrentUser(balanceUpdateValue);
            }
        });
    }

    public void UpdateBalanceForCurrentUser(Double valueMoney) {
        db.collection(GlobalConst.UsersTable).document(currentUserId)
                .update("balance", valueMoney,"isInputBalance",true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateBalanceActivity.this, "Update balance successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
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