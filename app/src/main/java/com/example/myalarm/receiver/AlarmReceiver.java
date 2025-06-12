package com.example.myalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.myalarm.service.RingtoneService;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, RingtoneService.class);
        serviceIntent.putExtra("alarmId", intent.getLongExtra("alarmId", -1));
        serviceIntent.putExtra("alarmName", intent.getStringExtra("alarmName"));
        serviceIntent.putExtra("ringtoneProgress", intent.getIntExtra("ringtoneProgress", 100));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

}