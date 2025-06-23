package com.example.myalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.myalarm.Constant;
import com.example.myalarm.service.RingtoneService;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, RingtoneService.class);
        serviceIntent.putExtra(Constant.INTENT_EXTRA_ALARM_ID, intent.getLongExtra(Constant.INTENT_EXTRA_ALARM_ID, -1));
        serviceIntent.putExtra(Constant.INTENT_EXTRA_ALARM_NAME, intent.getStringExtra(Constant.INTENT_EXTRA_ALARM_NAME));
        serviceIntent.putExtra(Constant.INTENT_EXTRA_RINGTONE_PROGRESS, intent.getIntExtra(Constant.INTENT_EXTRA_RINGTONE_PROGRESS, 100));
        serviceIntent.putExtra(Constant.INTENT_EXTRA_IS_VIBRATOR, intent.getBooleanExtra(Constant.INTENT_EXTRA_IS_VIBRATOR, true));
        serviceIntent.putExtra(Constant.INTENT_EXTRA_IS_LAST_RING, intent.getBooleanExtra(Constant.INTENT_EXTRA_IS_LAST_RING, false));
        serviceIntent.putExtra(Constant.INTENT_EXTRA_RING_INTERVAL, intent.getIntExtra(Constant.INTENT_EXTRA_RING_INTERVAL, 5));
        serviceIntent.putExtra(Constant.INTENT_EXTRA_IS_OVERSLEEP_PREVENT, intent.getBooleanExtra(Constant.INTENT_EXTRA_IS_OVERSLEEP_PREVENT, false));
        serviceIntent.putExtra(Constant.INTENT_EXTRA_ALREADY_RING_TIMES, intent.getIntExtra(Constant.INTENT_EXTRA_ALREADY_RING_TIMES, 0));
        context.startForegroundService(serviceIntent);
    }

}