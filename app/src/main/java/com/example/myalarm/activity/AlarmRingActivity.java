package com.example.myalarm.activity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myalarm.Constant;
import com.example.myalarm.R;
import com.example.myalarm.service.RingtoneService;
import com.example.myalarm.util.AlarmUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class AlarmRingActivity extends AppCompatActivity {

    private Button dismissOnceButton;
    private CountDownTimer countDownTimer;
    private boolean isShowdismissAllLinearLayout = false;
    private int dismissAllResult = -999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        WindowInsetsController controller = getWindow().getInsetsController();
        controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());

        setShowWhenLocked(true);
        setTurnScreenOn(true);

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (keyguardManager != null && keyguardManager.isKeyguardLocked()) {
            keyguardManager.requestDismissKeyguard(this, null);
        }

        setContentView(R.layout.activity_ring);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M月d日 E", Locale.CHINA);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        TextView ringTimeText = findViewById(R.id.ring_time_text);
        ringTimeText.setText(LocalTime.now().format(timeFormatter));
        TextView ringDateText = findViewById(R.id.ring_date_text);
        ringDateText.setText(LocalDate.now().format(dateFormatter));

        Context context = getApplicationContext();
        Intent intent = getIntent();
        long alarmId = intent.getLongExtra(Constant.INTENT_EXTRA_ALARM_ID, -1L);
        String alarmName = intent.getStringExtra(Constant.INTENT_EXTRA_ALARM_NAME);
        boolean isLastRing = intent.getBooleanExtra(Constant.INTENT_EXTRA_IS_LAST_RING, false);
        int ringInterval = intent.getIntExtra(Constant.INTENT_EXTRA_RING_INTERVAL, 5);

        TextView alarmLabel = findViewById(R.id.ring_alarm_label);
        alarmLabel.setText(alarmName == null || alarmName.isBlank() ? "闹钟" : alarmName);

        dismissOnceButton = findViewById(R.id.btn_dismiss_once);
        dismissOnceButton.setText(isLastRing ? "关闭闹钟" : ringInterval + "分钟后提醒");
        dismissOnceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(context, RingtoneService.class);
                stopService(serviceIntent);
                AlarmUtils.setNextAlarm(context, alarmId, isLastRing);
                finish();
            }
        });

        LinearLayout dismissAllLinearLayout = findViewById(R.id.ll_dismiss_all_calculator);

        TextView dismissAllTextView = findViewById(R.id.tv_dismiss_all);
        dismissAllTextView.setVisibility(isLastRing ? View.GONE : View.VISIBLE);
        dismissAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView dismissAllExpressionTextView = findViewById(R.id.tv_dismiss_all_expression);
                Object[] dismissAllExpression = getDismissAllExpression();
                dismissAllExpressionTextView.setText(dismissAllExpression[0].toString());
                dismissAllResult = Integer.valueOf(dismissAllExpression[1].toString());

                isShowdismissAllLinearLayout = !isShowdismissAllLinearLayout;
                dismissAllLinearLayout.setVisibility(isShowdismissAllLinearLayout ? View.VISIBLE : View.GONE);
            }
        });

        EditText dismissAllResultEditText = findViewById(R.id.et_dismiss_all_result);
        TextView dismissAllTextConfirm = findViewById(R.id.tv_dismiss_all_confirm);
        dismissAllTextConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean calculatorResultCorrect = false;
                try {
                    int calculatorResult = Integer.valueOf(dismissAllResultEditText.getText().toString());
                    if (calculatorResult != dismissAllResult) {
                        Toast.makeText(context, "计算结果不对！", Toast.LENGTH_SHORT).show();
                    } else {
                        calculatorResultCorrect = true;
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "计算结果不对！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                if (calculatorResultCorrect) {
                    Intent serviceIntent = new Intent(context, RingtoneService.class);
                    stopService(serviceIntent);
                    AlarmUtils.setNextAlarm(context, alarmId, true);
                    finish();
                }
            }
        });

        startAutoStopTimer();
    }

    private Object[] getDismissAllExpression() {
        Object[] dismissAllExpression = new Object[2];
        Random random = new Random();
        int number1 = random.nextInt(100);
        int number2 = random.nextInt(100);
        int number3 = random.nextInt(number1 + number2);
        dismissAllExpression[0] = number1 + " + " + number2 + " - " + number3 + " =";
        dismissAllExpression[1] = number1 + number2 - number3;
        return dismissAllExpression;
    }

    private void startAutoStopTimer() {
        countDownTimer = new CountDownTimer(2 * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 可在此处更新UI显示倒计时，暂未实现
            }

            @Override
            public void onFinish() {
                if (dismissOnceButton != null) {
                    dismissOnceButton.callOnClick();
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