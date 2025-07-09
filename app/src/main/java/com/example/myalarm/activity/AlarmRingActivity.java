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
    private boolean isShowCalculateLinearLayout = false;
    private int dismissAllResult = -999;
    private boolean isDismissAll = false;

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
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        long alarmId = intent.getLongExtra(Constant.INTENT_EXTRA_ALARM_ID, -1L);
        String alarmName = intent.getStringExtra(Constant.INTENT_EXTRA_ALARM_NAME);
        boolean isLastRing = intent.getBooleanExtra(Constant.INTENT_EXTRA_IS_LAST_RING, false);
        int ringInterval = intent.getIntExtra(Constant.INTENT_EXTRA_RING_INTERVAL, 5);
        boolean isOversleepPrevent = intent.getBooleanExtra(Constant.INTENT_EXTRA_IS_OVERSLEEP_PREVENT, false);
        int alreadRingTimes = intent.getIntExtra(Constant.INTENT_EXTRA_ALREADY_RING_TIMES, 0);

        TextView alarmNameTextView = findViewById(R.id.ring_alarm_name);
        alarmNameTextView.setText(alarmName == null || alarmName.isBlank() ? "闹钟" : alarmName);
        TextView alarmNoteTextView = findViewById(R.id.ring_alarm_note);
        alarmNoteTextView.setText(isLastRing ? "最后一次提醒" : "第" + (alreadRingTimes + 1) + "次提醒");

        LinearLayout calculateLinearLayout = findViewById(R.id.ll_calculate);
        TextView calculateNote = findViewById(R.id.tv_calculate_note);
        EditText calculateResultEditText = findViewById(R.id.et_calculate_result);

        dismissOnceButton = findViewById(R.id.btn_dismiss_once);
        dismissOnceButton.setText(isLastRing ? "关闭闹钟" : ringInterval + "分钟后提醒");
        dismissOnceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOversleepPrevent) {
                    TextView calculateExpressionTextView = findViewById(R.id.tv_calculate_expression);
                    Object[] dismissAllExpression = getDismissAllExpression();
                    calculateExpressionTextView.setText(dismissAllExpression[0].toString());
                    dismissAllResult = Integer.valueOf(dismissAllExpression[1].toString());

                    isShowCalculateLinearLayout = !isShowCalculateLinearLayout;
                    if (isShowCalculateLinearLayout) {
                        calculateLinearLayout.setVisibility(View.VISIBLE);
                        calculateNote.setVisibility(View.VISIBLE);
                        calculateResultEditText.setText("");
                        calculateNote.setText("请输入结果，点击确定");

                    } else {
                        calculateLinearLayout.setVisibility(View.INVISIBLE);
                        calculateNote.setVisibility(View.INVISIBLE);
                    }
                    isDismissAll = isLastRing;

                } else {
                    finishRing(context, alarmId, isLastRing);
                }
            }
        });

        TextView dismissAllTextView = findViewById(R.id.tv_dismiss_all);
        dismissAllTextView.setVisibility(isLastRing ? View.INVISIBLE : View.VISIBLE);
        dismissAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOversleepPrevent) {
                    TextView calculateExpressionTextView = findViewById(R.id.tv_calculate_expression);
                    Object[] dismissAllExpression = getDismissAllExpression();
                    calculateExpressionTextView.setText(dismissAllExpression[0].toString());
                    dismissAllResult = Integer.valueOf(dismissAllExpression[1].toString());

                    isShowCalculateLinearLayout = !isShowCalculateLinearLayout;
                    if (isShowCalculateLinearLayout) {
                        calculateLinearLayout.setVisibility(View.VISIBLE);
                        calculateNote.setVisibility(View.VISIBLE);
                        calculateResultEditText.setText("");
                        calculateNote.setText("请输入结果，点击确定");

                    } else {
                        calculateLinearLayout.setVisibility(View.INVISIBLE);
                        calculateNote.setVisibility(View.INVISIBLE);
                    }
                    isDismissAll = true;

                } else {
                    finishRing(context, alarmId, true);
                }
            }
        });

        TextView calculateConfirmTextView = findViewById(R.id.tv_calculate_confirm);
        calculateConfirmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean calculateResultCorrect = false;
                try {
                    int calculateResult = Integer.valueOf(calculateResultEditText.getText().toString());
                    if (calculateResult != dismissAllResult) {
                        calculateNote.setText("计算结果不对！");
                    } else {
                        calculateResultCorrect = true;
                    }
                } catch (Exception e) {
                    calculateNote.setText("计算结果不对！");
                    e.printStackTrace();
                }
                if (calculateResultCorrect) {
                    finishRing(context, alarmId, isDismissAll);
                }
            }
        });

        startAutoStopTimer();
    }

    private void finishRing(Context context, long alarmId, boolean isDismissAll) {
        Intent serviceIntent = new Intent(context, RingtoneService.class);
        stopService(serviceIntent);
        AlarmUtils.setNextAlarm(context, alarmId, isDismissAll);

//        Intent intent = new Intent(AlarmRingActivity.this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);

        finishAffinity();
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
                Context context = getApplicationContext();
                Intent intent = getIntent();
                long alarmId = intent.getLongExtra(Constant.INTENT_EXTRA_ALARM_ID, -1L);
                boolean isLastRing = intent.getBooleanExtra(Constant.INTENT_EXTRA_IS_LAST_RING, false);
                finishRing(context, alarmId, isLastRing);
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