package com.example.doanjava.ui.notifications;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doanjava.R;
import com.example.doanjava.common.GlobalFuc;
import com.example.doanjava.interfaces.ICallBackFireStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ChangePasswordActivity extends AppCompatActivity {
    Button btnChangePassword;
    EditText txtOldPassword, txtNewPassword, txtConfirmPassword;
    private boolean isTruePassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtOldPassword = (EditText) findViewById(R.id.txt_old_password);
        txtNewPassword = (EditText) findViewById(R.id.txt_new_password);
        txtConfirmPassword = (EditText) findViewById(R.id.txt_confirm_new_password);
        btnChangePassword = (Button) findViewById(R.id.btn_change_password);

        firebaseAuth = FirebaseAuth.getInstance();

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePassword();
            }
        });
    }

    public void ChangePassword() {
        final String titleMessage = "Error!";
        String oldPassword = txtOldPassword.getText().toString().trim();
        String newPassword = txtNewPassword.getText().toString().trim();
        String confirmPassword = txtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword)) {
            GlobalFuc.DialogShowMessage(this, titleMessage, "Old password is required!");
            return;
        }

        IsCorrectOldPassword(oldPassword, new ICallBackFireStore() {
            @Override
            public void onCallBack(List lstObject, Object value) {
                isTruePassword = (boolean) value;
            }
        });

        if (!isTruePassword) {
            GlobalFuc.DialogShowMessage(ChangePasswordActivity.this, titleMessage, "Old password is incorrect!");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            GlobalFuc.DialogShowMessage(ChangePasswordActivity.this, titleMessage, "New password is required!");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            GlobalFuc.DialogShowMessage(ChangePasswordActivity.this, titleMessage, "Confirm password is required!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            GlobalFuc.DialogShowMessage(ChangePasswordActivity.this, titleMessage, "Confirm password is incorrect!");
            return;
        }

        firebaseAuth.getCurrentUser().updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    GlobalFuc.DialogShowMessage(ChangePasswordActivity.this, titleMessage, "Change password successfully");
                    txtOldPassword.setText("");
                    txtNewPassword.setText("");
                    txtConfirmPassword.setText("");
                }
            }
        });

    }

    public void IsCorrectOldPassword(String oldPassword, ICallBackFireStore callBack) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(firebaseAuth.getCurrentUser().getEmail(), oldPassword);
        // Prompt the user to re-provide their sign-in credentials
        String email = firebaseAuth.getCurrentUser().getEmail();
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    callBack.onCallBack(null, false);
                } else {
                    callBack.onCallBack(null, true);
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
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}