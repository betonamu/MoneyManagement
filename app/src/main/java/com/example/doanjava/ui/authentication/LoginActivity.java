package com.example.doanjava.ui.authentication;

import androidx.annotation.NonNull;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.*;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.example.doanjava.MainActivity;
import com.example.doanjava.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText mEmail, mPassword;
    Button mBtnLogin;
    public static boolean btnError = false;
    TextView registerTextLink, forgotTextLink;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Anh xa variable with view
        mEmail = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mBtnLogin = findViewById(R.id.login);
        registerTextLink = findViewById(R.id.redirect_register);
        forgotTextLink = findViewById(R.id.forget_password);
        progressBar = findViewById(R.id.loading);
        mBtnLogin.setEnabled(true);

        //Get a copy of data from firebase
        firebaseAuth = FirebaseAuth.getInstance();

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        //redirect to Register activity when click TextView "You don't have a account?"
        registerTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPassword(v);
            }
        });
    }

    public void Login() {
        String username = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            mEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            mEmail.setError("Password have to be >= 6 characters");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        //Authentication user
        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Logged in Successfully.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Username or password incorrect", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void ForgotPassword(View v){
        EditText resetMail = new EditText(v.getContext());
        String email = mEmail.getText().toString();
        if (email != null && email != "") {
            resetMail.setText(email);
        }
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
        passwordResetDialog.setTitle("Reset password?");
        passwordResetDialog.setMessage("Enter your Email to received reset link.");
        resetMail.setBackgroundResource(R.drawable.edit_text_style);
        resetMail.setHint("Enter your mail");
        passwordResetDialog.setView(resetMail);

        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail = resetMail.getText().toString();
                if (!TextUtils.isEmpty(mail)) {
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast toast = Toast.makeText(LoginActivity.this, "Reset link sent to email.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }).addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast toast = Toast.makeText(LoginActivity.this, "Error! Reset link is not sent.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    });
                } else {
                    Toast toast = Toast.makeText(LoginActivity.this, "Error! Enter your email.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        passwordResetDialog.create().show();
    }
}