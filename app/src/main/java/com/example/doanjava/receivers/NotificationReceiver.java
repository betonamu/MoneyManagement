package com.example.doanjava.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.doanjava.helpers.NotificationHelper;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //initialize NotificationHelper object
        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.createNotification();
    }
}
