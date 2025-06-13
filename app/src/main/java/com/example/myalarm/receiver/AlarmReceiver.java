package com.example.myalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.myalarm.service.RingtoneService;
import com.example.myalarm.util.AlarmUtils;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, RingtoneService.class);
        long alarmId = intent.getLongExtra("alarmId", -1);
        serviceIntent.putExtra("alarmId", alarmId);
        serviceIntent.putExtra("alarmName", intent.getStringExtra("alarmName"));
        serviceIntent.putExtra("ringtoneProgress", intent.getIntExtra("ringtoneProgress", 100));
        context.startForegroundService(serviceIntent);

        AlarmUtils.setNextAlarm(context,alarmId);
    }

}