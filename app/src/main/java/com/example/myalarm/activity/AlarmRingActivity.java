package com.example.myalarm.activity;

import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myalarm.R;
import com.example.myalarm.service.RingtoneService;

public class AlarmRingActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "alarm_channel";
    private static final int NOTIFICATION_ID = 1;
//    View floatingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置窗口标志
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Android 12+：请求解除锁屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);

            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (keyguardManager != null && keyguardManager.isKeyguardLocked()) {
                keyguardManager.requestDismissKeyguard(this, null);
            }
        }

        setContentView(R.layout.activity_ring);
//        Context context = getApplicationContext();

//        ActivityResultLauncher mDrawOverAppsLauncher = PermissionUtils.getActivityResultLauncher(this);
//        View floatingView = PermissionUtils.overlayPermissionCheck(context, mDrawOverAppsLauncher, R.layout.activity_ring);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + getPackageName()));
//            startActivityForResult(intent, REQUEST_CODE_DRAW_OVER_OTHER_APPS_PERMISSION);
//        }
//        PermissionUtils.isOverlayPermission(context);

       /* ActivityResultLauncher mDrawOverAppsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() != RESULT_OK) {
                            jumpToPermission();
                        } else {
                            showFloatingWindow();
                        }
                    }
                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                // 发起请求
                mDrawOverAppsLauncher.launch(intent);
            } else {
                showFloatingWindow();
            }
        }*/
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&!Settings.canDrawOverlays(MainActivity.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            mDrawOverAppsLauncher.launch(intent);
        } else {
            createFloatingView();
        }*/

       /* createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("闹钟提醒")
                .setContentText("该起床啦！")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
*/
        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PermissionUtils.removeFloatingWindow(context, floatingView);
//                removeFloatingWindow(floatingView);
                Intent serviceIntent = new Intent(getApplicationContext(), RingtoneService.class);
                stopService(serviceIntent);
                finish();
            }
        });
    }
/*
    @Override
    protected void onResume() {
        super.onResume();
        //唤醒屏幕
        acquireWakeLock();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //释放锁屏
        releaseWakeLock();
    }

    *//**
     * 唤醒屏幕
     *//*
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

    */

    /**
     * 释放锁屏
     *//*
    private void releaseWakeLock() {
        if (mWakelock != null && mWakelock.isHeld()) {
            mWakelock.release();
            mWakelock = null;
        }
    }*/
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "闹钟通知";
            String description = "闹钟触发的通知";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_DRAW_OVER_OTHER_APPS_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                jumpToPermission();
            } else {
                showFloatingWindow();
            }
        }
    }*/

    /*
    public void jumpToPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void showFloatingWindow() {
        WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        // 设置悬浮窗的位置
        params.gravity = Gravity.TOP | Gravity.START; // 例如，设置在屏幕左上角
        params.x = 100; // x坐标
        params.y = 100; // y坐标

        // 创建浮动窗口视图
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        floatingView = inflater.inflate(R.layout.activity_ring, null);

        // 获取WindowManager
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        try {
            // 添加悬浮窗到WindowManager
            windowManager.addView(floatingView, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFloatingWindow(View floatingView) {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (floatingView != null && windowManager != null) {
            windowManager.removeView(floatingView);
        }
    }*/

}