package com.example.myalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.myalarm.data.DatabaseClient;
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.util.AlarmUtils;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // 重新设置所有闹钟
            List<AlarmEntity> activeAlarms = DatabaseClient.getInstance()
                    .getAlarmEntityDatabase()
                    .alarmDao().getAllActiveAlarms();
            for (AlarmEntity alarm : activeAlarms) {
                AlarmUtils.setAlarm(context, alarm);
            }
        }
    }
}
