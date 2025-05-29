package com.example.myalarm.receiver;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.example.myalarm.activity.AlarmRingActivity;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1001;

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("terry", "AlarmReceiver.onReceive");

        try {
            // 构建通知
          /*  NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("闹钟提醒")
                    .setContentText(intent.getStringExtra("title"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(getContentIntent(context, intent))
                    .setAutoCancel(true);

            // 获取通知管理器
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // 创建通知渠道（API 26+）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "CHANNEL_ID",
                        "闹钟通知",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(NOTIFICATION_ID, builder.build());
*/
//        // 处理重复闹钟
//        if (intent.getBooleanExtra("isRepeat", false)) {
//            setNextAlarm(context, intent);
//        }

            // 启动AlarmRingActivity
            Intent ringIntent = new Intent(context, AlarmRingActivity.class);
            ringIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(ringIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("terry", "AlarmReceiver.onReceive end");
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

    /*private void setNextAlarm(Context context, Intent intent) {
        // 计算下一次触发时间
        long currentTime = System.currentTimeMillis();
        long triggerTime = intent.getLongExtra("triggerTime", currentTime);
        int repeatType = intent.getIntExtra("repeatType", 0);

        switch (repeatType) {
            case 0: // 每天
                triggerTime += 24 * 60 * 60 * 1000;
                break;
            case 1: // 每周
                triggerTime += 7 * 24 * 60 * 60 * 1000;
                break;
            case 2: // 每月
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(triggerTime);
                calendar.add(Calendar.MONTH, 1);
                triggerTime = calendar.getTimeInMillis();
                break;
        }

        // 更新数据库中的触发时间
        AlarmEntity alarm = new AlarmEntity();
        alarm.setId(intent.getIntExtra("alarmId", -1));
        alarm.setTriggerTime(triggerTime);
        // 这里需要调用你的数据库更新方法
        // alarmRepository.updateAlarm(alarm);

        // 重新设置闹钟
        AlarmUtils.setAlarm(context, alarm);
    }*/
}