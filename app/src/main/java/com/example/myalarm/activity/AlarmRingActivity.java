package com.example.myalarm.activity;

import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myalarm.R;
import com.example.myalarm.service.RingtoneService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AlarmRingActivity extends AppCompatActivity {

    private Button stopButton;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M月d日 E", Locale.CHINA);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        TextView ringTimeText = findViewById(R.id.ring_time_text);
        ringTimeText.setText(LocalTime.now().format(timeFormatter));
        TextView ringDateText = findViewById(R.id.ring_date_text);
        ringDateText.setText(LocalDate.now().format(dateFormatter));

        TextView alarmLabel = findViewById(R.id.ring_alarm_label);
        String alarmName = getIntent().getStringExtra("alarmName");
        alarmLabel.setText(alarmName == null || alarmName.isBlank() ? "闹钟" : alarmName);

        stopButton = findViewById(R.id.ring_stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getApplicationContext(), RingtoneService.class);
                stopService(serviceIntent);
                finish();
            }
        });

        startAutoStopTimer();
    }

    private void startAutoStopTimer() {
        countDownTimer = new CountDownTimer(2 * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 可在此处更新UI显示倒计时，暂未实现
            }

            @Override
            public void onFinish() {
                if (stopButton != null) {
                    stopButton.callOnClick();
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}