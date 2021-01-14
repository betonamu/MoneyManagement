package com.example.doanjava;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doanjava.common.GlobalConst;
import com.example.doanjava.data.model.UserModel;
import com.example.doanjava.interfaces.ICallBackFireStore;
import com.example.doanjava.receivers.NotificationReceiver;
import com.example.doanjava.ui.authentication.LoginActivity;
import com.example.doanjava.ui.notifications.SettingActivity;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private UserModel user;
    private String currentUserId;

    SharedPreferences pref;
    String localeName;
    String currentLanguage = "en", currentLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetDailyNotification();
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
        currentLanguage = getIntent().getStringExtra(currentLang);
        pref = getSharedPreferences("PREF", MODE_PRIVATE);
        localeName = pref.getString("selected_locale", null);
        if (localeName != null) {
            setLocale(localeName);
        }
    }

    public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            Locale myLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);

            Intent refresh = new Intent(this, MainActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        }
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
                .update("balance", valueMoney, "isInputBalance", true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Input balance successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Set time to wake up device to create daily notification
    public void SetDailyNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, GlobalConst.HourWakeUpDailyNotification);
        calendar.set(Calendar.MINUTE, GlobalConst.MinuteWakeUpDailyNotification);
        calendar.set(Calendar.SECOND, GlobalConst.SecondsWakeUpDailyNotification);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        //Khởi chạy dịch vụ để tạo thông báo
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Đánh thức thiết bị hằng ngày vào thời gian đã gán ở calendar
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

}