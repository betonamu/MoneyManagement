package com.example.doanjava.ui.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.doanjava.MainActivity;
import com.example.doanjava.R;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity {
    Spinner spinnerLanguage;

    String currentLanguage, currentLang;
    private int index;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinnerLanguage = (Spinner) findViewById(R.id.spinner_language);
        currentLanguage = getIntent().getStringExtra(currentLang);

        pref = getSharedPreferences("PREF", MODE_PRIVATE);
        int position = pref.getInt("position_language", 0);
        currentLanguage = pref.getString("selected_locale", null);
        spinnerLanguage.setSelection(position);

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        index = 0;
                        setLocale("vi");
                        break;
                    case 1:
                        index = 1;
                        setLocale("en");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            Locale myLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);

            SharedPreferences p = getSharedPreferences("PREF", MODE_PRIVATE);
            p.edit().putString("selected_locale", localeName).commit();
            p.edit().putInt("position_language", index).commit();

            Intent refresh = new Intent(this, SettingActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
            finish();
        } else {
            Toast.makeText(this, "Language already selected!", Toast.LENGTH_SHORT).show();
        }
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