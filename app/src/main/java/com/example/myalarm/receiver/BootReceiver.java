package com.example.myalarm.receiver;

import static com.example.myalarm.util.AlarmUtils.alarmDao;
import static com.example.myalarm.util.AlarmUtils.getRequestCode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.util.AlarmUtils;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<AlarmEntity> activeAlarms = alarmDao.getAllAlarms();
                    for (AlarmEntity alarmEntity : activeAlarms) {
                        int originalRequestCode = getRequestCode(alarmEntity.getId(), alarmEntity.getRequestCodeSeq(), false);
                        AlarmUtils.setAlarm(context, alarmEntity, originalRequestCode);
                    }
                }
            }).start();
        }
    }
}
