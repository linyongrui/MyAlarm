package com.example.myalarm.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.myalarm.R;
import com.example.myalarm.activity.AlarmRingActivity;

import java.io.IOException;

public class RingtoneService extends Service {
    private static final Uri RINGTONE_DEFAULT_URI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    private static final long[] VIBRATOR_PATTERN = {500, 1000, 500, 1000};

    PowerManager.WakeLock mWakelock;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        mediaPlayer = createMediaPlayer(context);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        acquireWakeLock();
        Intent fullScreenIntent = foregroundNotification(intent);

        float ringtoneVolume = (float) intent.getIntExtra("ringtoneProgress", 100) / 100;
        mediaPlayer.setVolume(ringtoneVolume, ringtoneVolume);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        boolean isVibrator = intent.getBooleanExtra("isVibrator", true);
        if (isVibrator) {
            VibrationEffect vibrationEffect = VibrationEffect.createWaveform(VIBRATOR_PATTERN, 0);
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(vibrationEffect);
            }
        }

        startActivity(fullScreenIntent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.cancel();
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        releaseWakeLock();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private MediaPlayer createMediaPlayer(Context context) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build());

        try {
            mediaPlayer.setDataSource(context, RINGTONE_DEFAULT_URI);
            mediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return mediaPlayer;
    }

    private void acquireWakeLock() {
        if (mWakelock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass()
                    .getCanonicalName());
            mWakelock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (mWakelock != null && mWakelock.isHeld()) {
            mWakelock.release();
            mWakelock = null;
        }
    }

    private Intent foregroundNotification(Intent intent) {
        String channelId = "alarm_channel";

        NotificationChannel channel = new NotificationChannel(
                channelId, "Alarm Notifications", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Alarm full screen");
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) manager.createNotificationChannel(channel);

        Intent fullScreenIntent = new Intent(this, AlarmRingActivity.class);
        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        fullScreenIntent.putExtra("alarmName", intent.getStringExtra("alarmName"));

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                this, 0, fullScreenIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("闹钟响铃")
                .setContentText("正在响铃...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setAutoCancel(true)
                .build();
        startForeground(1, notification);

        return fullScreenIntent;
    }
}