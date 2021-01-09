package com.example.doanjava.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.doanjava.R;
import com.example.doanjava.ui.authentication.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class GlobalFuc {

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String CurrencyFormat(Double value) {
        NumberFormat formatter = new DecimalFormat("#,###");
        String formattedCurrency = formatter.format(value);
        return formattedCurrency;
    }

    public static void DialogShowMessage(Activity activity, String title, String mess) {
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(activity);
        passwordResetDialog.setTitle(title);
        passwordResetDialog.setMessage(mess);

        passwordResetDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        passwordResetDialog.create().show();
    }

}
