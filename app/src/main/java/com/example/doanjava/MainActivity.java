package com.example.doanjava;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doanjava.common.GlobalConst;
import com.example.doanjava.data.model.UserModel;
import com.example.doanjava.interfaces.ICallBackFireStore;
import com.example.doanjava.ui.authentication.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private UserModel user;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_add)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        FirebaseApp.initializeApp(this);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        GetIsInputBalance(new ICallBackFireStore() {
            @Override
            public void onCallBack(List lstObject, Object value) {
                if (user.isInputBalance == false) {
                    InputFirstBalance();
                }
            }
        });
    }

    //Create dialog for the user to enter first balance
    public void InputFirstBalance() {
        EditText inputBalance = new EditText(MainActivity.this);

        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(MainActivity.this);
        passwordResetDialog.setTitle("Nhập số dư");
        passwordResetDialog.setMessage("Hãy nhập số dư đầu tiên sau khi đăng ký tài khoản.");
        inputBalance.setBackgroundResource(R.drawable.edit_text_style);
        inputBalance.setHint("Nhập số dư đầu tiên");
        passwordResetDialog.setView(inputBalance);

        passwordResetDialog.setPositiveButton("Luu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Double balanceValue = Double.parseDouble(inputBalance.getText().toString());
                UpdateBalanceForCurrentUser(balanceValue);
            }
        });
        passwordResetDialog.create().show();
    }

    //check current user has entered balance or not
    public void GetIsInputBalance(ICallBackFireStore callBack) {
        db.collection(GlobalConst.UsersTable).document(currentUserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    //Converts response data to UserModel
                    user = task.getResult().toObject(UserModel.class);
                }
                //callback value when load data successfully from firebase
                callBack.onCallBack(null, user);
            }
        });
    }

    //Update balance for current user at collection "Users"
    public void UpdateBalanceForCurrentUser(Double valueMoney) {
        db.collection(GlobalConst.UsersTable).document(currentUserId)
                .update("balance", valueMoney,"isInputBalance",true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Input balance successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}