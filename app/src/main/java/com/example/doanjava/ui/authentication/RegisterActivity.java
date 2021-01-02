package com.example.doanjava.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doanjava.MainActivity;
import com.example.doanjava.R;
import com.example.doanjava.data.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    EditText mEmail, mPassword, mFullName, mPhoneNumber,mRePassword;
    Button mBtnRegister;
    TextView signInTextLink;
    ProgressBar progressBar;

    //Firebase database
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    private String username, phoneNumber, password, fullName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Anh xa view
        mEmail = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mRePassword = (EditText) findViewById(R.id.re_password);
        mBtnRegister = (Button) findViewById(R.id.register);
        mFullName = (EditText) findViewById(R.id.full_name);
        mPhoneNumber = (EditText) findViewById(R.id.phone_number);
        signInTextLink = (TextView) findViewById(R.id.redirect_login);
        progressBar = (ProgressBar) findViewById(R.id.loading);
        mBtnRegister.setEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

        signInTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    public void Register() {
        username = mEmail.getText().toString().trim();
        password = mPassword.getText().toString().trim();
        phoneNumber = mPhoneNumber.getText().toString().trim();
        fullName = mFullName.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            mEmail.setError(getResources().getString(R.string.required_name));
            return;
        }
        if (TextUtils.isEmpty(username)) {
            mEmail.setError(getResources().getString(R.string.required_username));
            return;
        }else if(!isValidEmail(username)){
            mEmail.setError(getResources().getString(R.string.invalid_username));
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            mEmail.setError(getResources().getString(R.string.required_phone));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getResources().getString(R.string.required_password));
            return;
        }
        if (password.length() < 6) {
            mPassword.setError(getResources().getString(R.string.invalid_password));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //Authentication user
        firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isComplete()) {
                    //Add custom field to Firebase Realtime
                    UserModel userModel = new UserModel();
                    userModel.email = username;
                    userModel.fullName = fullName;
                    userModel.password = password;
                    userModel.phoneNumber = phoneNumber;

                    //Set the value for the node with uId = uId of the current user to firebase
                    db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .set(userModel);

                    Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(RegisterActivity.this, "Created with error " + task.getException(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
