package com.example.myalarm.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.myalarm.R;
import com.example.myalarm.activity.AlarmRingActivity;

import java.io.IOException;

public class RingtoneService extends Service {
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.02F, 0.02F);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build());
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        }

        try {
            mediaPlayer.setDataSource(getApplicationContext(), defaultUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.i("terry", "onCreate");
        mediaPlayer.start();
//        mediaPlayer.start();
//        acquireWakeLock();

    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 创建通知 channel（Android 8+ 要求）
        String channelId = "alarm_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Alarm Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Alarm is ringing");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // 构建通知
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("闹钟响铃")
                .setContentText("正在响铃")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        startForeground(1, notification);

        // 启动响铃 Activity
        Intent activityIntent = new Intent(this, AlarmRingActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(activityIntent);

        Log.i("terry", "onStartCommand");
//        stopSelf(); // 启动后即可关闭服务
//        return START_NOT_STICKY;

        mediaPlayer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
//            releaseWakeLock();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 唤醒屏幕
     */
    PowerManager.WakeLock mWakelock;

    private void acquireWakeLock() {
        if (mWakelock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass()
                    .getCanonicalName());
            mWakelock.acquire();
        }
    }

    /**
     * 释放锁屏
     */
    private void releaseWakeLock() {
        if (mWakelock != null && mWakelock.isHeld()) {
            mWakelock.release();
            mWakelock = null;
        }
    }

}