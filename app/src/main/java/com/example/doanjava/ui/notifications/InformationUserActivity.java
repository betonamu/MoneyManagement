package com.example.doanjava.ui.notifications;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doanjava.R;
import com.example.doanjava.common.GlobalConst;
import com.example.doanjava.data.model.UserModel;
import com.example.doanjava.interfaces.ICallBackFireStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;
import java.util.List;

public class InformationUserActivity extends AppCompatActivity {
    Button btnChangePassword, btnUpdateBalance;
    ImageView imgAccount;
    List<UserModel> lstUser = new LinkedList<>();

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnChangePassword = (Button) findViewById(R.id.btn_change_password_user_activity);
        btnUpdateBalance = (Button) findViewById(R.id.btn_update_balance_user_activity);
        imgAccount = (ImageView) findViewById(R.id.img_account_user_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationUserActivity.this, ChangePasswordActivity.class));
            }
        });

        btnUpdateBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationUserActivity.this, UpdateBalanceActivity.class));
            }
        });

        GetUserInformation(new ICallBackFireStore<UserModel>() {
            @Override
            public void onCallBack(List<UserModel> lstObject, Object value) {
                if (lstObject.size() != 0) {
                    if (lstObject.get(0).photoUrl != null && lstObject.get(0).photoUrl != "") {
                        Uri photoUri = Uri.parse(lstObject.get(0).photoUrl);
                        Glide.with(InformationUserActivity.this)
                                .load(photoUri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(imgAccount);
                    }
                }
            }
        });

    }

    public void GetUserInformation(ICallBackFireStore callBack) {
        db.collection(GlobalConst.UsersTable).document(firebaseAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    UserModel userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null) {
                        lstUser.add(userModel);
                    }
                }
                callBack.onCallBack(lstUser, null);
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
