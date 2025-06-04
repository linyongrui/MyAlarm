package com.example.myalarm.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.myalarm.activity.AlarmRingActivity;
import com.example.myalarm.service.RingtoneService;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, RingtoneService.class);
        context.startService(serviceIntent);

        Intent ringIntent = new Intent(context, AlarmRingActivity.class);
        ringIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(ringIntent);
    }

    private PendingIntent getContentIntent(Context context, Intent intent) {
        Intent activityIntent = new Intent(context, AlarmRingActivity.class);
        activityIntent.putExtra("alarmId", intent.getIntExtra("alarmId", -1));
        return PendingIntent.getActivity(
                context,
                intent.getIntExtra("alarmId", -1),
                activityIntent,
                PendingIntent.FLAG_IMMUTABLE
        );
    }
}