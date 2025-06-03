package com.example.myalarm.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myalarm.R;
import com.example.myalarm.alarmtype.BaseAlarmType;
import com.example.myalarm.alarmtype.DateAlarmType;
import com.example.myalarm.alarmtype.EveryDayAlarmType;
import com.example.myalarm.alarmtype.HolidayAlarmType;
import com.example.myalarm.alarmtype.OnceAlarmType;
import com.example.myalarm.alarmtype.WeekAlarmType;
import com.example.myalarm.alarmtype.WorkingDayAlarmType;
import com.example.myalarm.data.DatabaseClient;
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.util.AlarmUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class NewAlarmActivity extends AppCompatActivity {
    private static final int REQUEST_SCHEDULE_EXACT_ALARM = 1;
    private static final List<SpinnerOption> alarmTypeList = new ArrayList<>();

    {
        alarmTypeList.clear();
        alarmTypeList.add(new SpinnerOption(OnceAlarmType.ALARM_TYPE, "响一次"));
        alarmTypeList.add(new SpinnerOption(EveryDayAlarmType.ALARM_TYPE, "每天"));
        alarmTypeList.add(new SpinnerOption(WorkingDayAlarmType.ALARM_TYPE, "法定工作日"));
        alarmTypeList.add(new SpinnerOption(HolidayAlarmType.ALARM_TYPE, "非工作日"));
        alarmTypeList.add(new SpinnerOption(WeekAlarmType.ALARM_TYPE, "按周"));
        alarmTypeList.add(new SpinnerOption(DateAlarmType.ALARM_TYPE, "按日期"));
    }

    private TimePicker timePicker;
    private Spinner ringRuleSpinner;
    private TextView repeatDatesTextView;
    private LinearLayout llDaysOfWeek;
    private ToggleButton[] dayButtons;
    private LinearLayout llSkip;
    private Switch skipWorkingDaysSwitch;
    private Switch skipHolidaysSwitch;
    private EditText alarmNameEditText;
    private TextView ringtoneTextView, vibrationTextView, snoozeTextView;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);

        Context context = getApplicationContext();

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAlarm();
            }
        });

        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                updateTimeUntilNextRing();
            }
        });

        ringRuleSpinner = findViewById(R.id.spinner_ring_rule);
        ArrayAdapter<SpinnerOption> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                alarmTypeList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ringRuleSpinner.setAdapter(adapter);
        ringRuleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerOption selectedOption = (SpinnerOption) parent.getItemAtPosition(position);
//                Toast.makeText(context, "选中的选项: " + selectedOption.getOptionText() + ", ID: " + selectedOption.getOptionId(), Toast.LENGTH_SHORT).show();
                ringRuleSpinnerSelectHandle(selectedOption.getOptionId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(context, "必选", Toast.LENGTH_SHORT).show();
            }
        });

        repeatDatesTextView = findViewById(R.id.tv_ring_repeat_dates);

        llDaysOfWeek = findViewById(R.id.ll_days_of_week);
        dayButtons = new ToggleButton[]{
                findViewById(R.id.btn_sunday),
                findViewById(R.id.btn_monday),
                findViewById(R.id.btn_tuesday),
                findViewById(R.id.btn_wednesday),
                findViewById(R.id.btn_thursday),
                findViewById(R.id.btn_friday),
                findViewById(R.id.btn_saturday)
        };
        for (ToggleButton dayButton : dayButtons) {
            dayButton.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    ToggleButton toggleButton = (ToggleButton) v;
                    dayButtonsClickHandle(toggleButton.isChecked(), toggleButton, context);
                }
            });
        }

        llSkip = findViewById(R.id.ll_skip);
        skipHolidaysSwitch = findViewById(R.id.switch_skip_holidays);
        skipHolidaysSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (skipHolidaysSwitch.isChecked()) {
                    skipWorkingDaysSwitch.setChecked(false);
                }
                repeatDatesTextView.setText(getNewAlarmEntity().getRepeatStr());
                updateTimeUntilNextRing();
            }
        });

        skipWorkingDaysSwitch = findViewById(R.id.switch_skip_workingDay);
        skipWorkingDaysSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (skipWorkingDaysSwitch.isChecked()) {
                    skipHolidaysSwitch.setChecked(false);
                }
                repeatDatesTextView.setText(getNewAlarmEntity().getRepeatStr());
                updateTimeUntilNextRing();
            }
        });

        alarmNameEditText = findViewById(R.id.et_alarm_name);
        ringtoneTextView = findViewById(R.id.tv_ringtone);
        vibrationTextView = findViewById(R.id.tv_vibration);
        snoozeTextView = findViewById(R.id.tv_snooze);
    }

    private void ringRuleSpinnerSelectHandle(String optionId) {
        llDaysOfWeek.setVisibility(View.GONE);
        llSkip.setVisibility(View.GONE);

        switch (optionId) {
            case DateAlarmType.ALARM_TYPE:
                llSkip.setVisibility(View.VISIBLE);
                break;
            case WeekAlarmType.ALARM_TYPE:
                llDaysOfWeek.setVisibility(View.VISIBLE);
                llSkip.setVisibility(View.VISIBLE);
                break;
            case EveryDayAlarmType.ALARM_TYPE:
            case HolidayAlarmType.ALARM_TYPE:
            case OnceAlarmType.ALARM_TYPE:
            case WorkingDayAlarmType.ALARM_TYPE:
                break;
            default:
                throw new IllegalArgumentException("Unknown alarm type: " + optionId);
        }

        repeatDatesTextView.setText(getNewAlarmEntity().getRepeatStr());
        updateTimeUntilNextRing();
    }

    private void dayButtonsClickHandle(boolean isSelected, ToggleButton toggleButton, Context context) {
        toggleButton.setTextColor(
                isSelected ? ContextCompat.getColorStateList(context, R.color.white) : ContextCompat.getColorStateList(context, R.color.black)
        );

        repeatDatesTextView.setText(getNewAlarmEntity().getRepeatStr());
        updateTimeUntilNextRing();
    }

    private void updateTimeUntilNextRing() {
        StringBuilder timeLeftBuilder = new StringBuilder();
        int[] timeLeft = AlarmUtils.getNextTriggerTimeLeft(getNewAlarmEntity());
        timeLeftBuilder.append("距离下次响铃还有");
        if (timeLeft[0] > 0) {
            timeLeftBuilder.append(timeLeft[0] + "天");
        }
        if (timeLeft[1] > 0) {
            timeLeftBuilder.append(timeLeft[1] + "小时");
        }
        if (timeLeft[2] > 0) {
            timeLeftBuilder.append(timeLeft[2] + "分钟");
        }
        TextView timeUntilNextRingTextView = findViewById(R.id.tv_time_until_next_ring);
        timeUntilNextRingTextView.setText(timeLeftBuilder);
    }

    private void saveAlarm() {
        AlarmEntity newAlarmEntity = getNewAlarmEntity();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestScheduleExactAlarmPermission();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                long id = DatabaseClient.getInstance()
                        .getAlarmEntityDatabase()
                        .alarmDao()
                        .insertAlarmEntity(newAlarmEntity);
                newAlarmEntity.setId(id);
                AlarmUtils.setAlarm(getApplicationContext(), newAlarmEntity);
                finish();
            }
        }).start();
    }

    private AlarmEntity getNewAlarmEntity() {

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        LocalTime time = LocalTime.of(hour, minute).withSecond(0).withNano(0);

        String ringRule = ((SpinnerOption) ringRuleSpinner.getSelectedItem()).getOptionId();
        BaseAlarmType baseAlarmType;
        switch (ringRule) {
            case DateAlarmType.ALARM_TYPE:
                baseAlarmType = new DateAlarmType(null, null, null);
                break;
            case EveryDayAlarmType.ALARM_TYPE:
                baseAlarmType = new EveryDayAlarmType();
                break;
            case HolidayAlarmType.ALARM_TYPE:
                baseAlarmType = new HolidayAlarmType();
                break;
            case OnceAlarmType.ALARM_TYPE:
                baseAlarmType = new OnceAlarmType();
                break;
            case WeekAlarmType.ALARM_TYPE:
                int[] weekCheck = new int[7];
                for (int i = 0; i < 7; i++) {
                    weekCheck[i] = dayButtons[i].isChecked() ? 1 : 0;
                }
                baseAlarmType = new WeekAlarmType(weekCheck);
                break;
            case WorkingDayAlarmType.ALARM_TYPE:
                baseAlarmType = new WorkingDayAlarmType();
                break;
            default:
                throw new IllegalArgumentException("Unknown alarm type: " + ringRule);
        }
        baseAlarmType.setSkipWorkingDay(skipWorkingDaysSwitch.isChecked());
        baseAlarmType.setSkipHoliday(skipHolidaysSwitch.isChecked());
        String alarmName = alarmNameEditText.getText().toString();

        Log.i("terry", "=========getNewAlarmEntity========");
        Log.i("terry", "设置的闹钟时间：" + hour + ":" + minute);
        Log.i("terry", "响铃规则：" + ringRule);
//        Log.i("terry", "选择的日期：" + weekCheck.toString());
        Log.i("terry", "除法定工作日：" + skipWorkingDaysSwitch.isChecked());
        Log.i("terry", "除法定节假日：" + skipHolidaysSwitch.isChecked());
        Log.i("terry", "闹钟名称：" + alarmName);

        return new AlarmEntity(alarmName, baseAlarmType, time);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestScheduleExactAlarmPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.SCHEDULE_EXACT_ALARM)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM},
                    REQUEST_SCHEDULE_EXACT_ALARM);
        } else {
            Toast.makeText(this, "权限已授予，可以设置精确闹钟", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SCHEDULE_EXACT_ALARM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限已授予，可以设置精确闹钟", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "权限被拒绝，无法设置精确闹钟", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SpinnerOption {
        private String optionId;
        private String optionText;

        public SpinnerOption(String optionId, String optionText) {
            this.optionId = optionId;
            this.optionText = optionText;
        }

        public String getOptionId() {
            return optionId;
        }

        public String getOptionText() {
            return optionText;
        }

        @Override
        public String toString() {
            return optionText;
        }
    }
}