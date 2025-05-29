package com.example.myalarm.activity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myalarm.R;
import com.example.myalarm.alarmtype.WeekAlarmType;
import com.example.myalarm.data.DatabaseClient;
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.util.AlarmUtils;

import java.util.Calendar;

public class NewAlarmActivity extends AppCompatActivity {
    private static final int REQUEST_SCHEDULE_EXACT_ALARM = 1;
    private TimePicker timePicker;
    private Spinner ringRuleSpinner;
    private Button[] dayButtons;
    private Switch skipHolidaysSwitch;
    private EditText alarmNameEditText;
    private TextView ringtoneTextView, vibrationTextView, snoozeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_alarm);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        timePicker = findViewById(R.id.timePicker);
        ringRuleSpinner = findViewById(R.id.spinner_ring_rule);
        dayButtons = new Button[]{
                findViewById(R.id.btn_sunday),
                findViewById(R.id.btn_monday),
                findViewById(R.id.btn_tuesday),
                findViewById(R.id.btn_wednesday),
                findViewById(R.id.btn_thursday),
                findViewById(R.id.btn_friday),
                findViewById(R.id.btn_saturday)
        };
        skipHolidaysSwitch = findViewById(R.id.switch_skip_holidays);
        alarmNameEditText = findViewById(R.id.et_alarm_name);
        ringtoneTextView = findViewById(R.id.tv_ringtone);
        vibrationTextView = findViewById(R.id.tv_vibration);
        snoozeTextView = findViewById(R.id.tv_snooze);

        String[] ringRuleOptions = {"响一次", "法定节假日"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ringRuleOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ringRuleSpinner.setAdapter(adapter);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateTimeUntilNextRing(calendar);
            }
        });

        ringRuleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRule = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        for (Button dayButton : dayButtons) {
            dayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button = (Button) v;
                    if (isButtonSelected(button)) {
                        unselectButton(button);
                    } else {
                        selectButton(button);
                    }
                }
            });
        }

        skipHolidaysSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAlarmSettings();
//                Intent intent = new Intent(NewAlarmActivity.this, MainActivity.class);
//                startActivity(intent);
            }
        });
    }

    private boolean isButtonSelected(Button button) {
        return button.getTextColors().getDefaultColor() == getResources().getColor(android.R.color.white);
    }

    private void selectButton(Button button) {
        button.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        button.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void unselectButton(Button button) {
        button.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        button.setTextColor(getResources().getColor(android.R.color.black));
    }

    private void updateTimeUntilNextRing(Calendar calendar) {
        TextView timeUntilNextRingTextView = findViewById(R.id.tv_time_until_next_ring);
        timeUntilNextRingTextView.setText("距离下次响铃还有 " + getTimeDiff(calendar) + " 天");
    }

    private int getTimeDiff(Calendar calendar) {
        Calendar now = Calendar.getInstance();
        long diffInMillis = calendar.getTimeInMillis() - now.getTimeInMillis();
        return (int) (diffInMillis / (24 * 60 * 60 * 1000));
    }

    private void saveAlarmSettings() {
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        String ringRule = (String) ringRuleSpinner.getSelectedItem();
        StringBuilder selectedDays = new StringBuilder();
        for (Button dayButton : dayButtons) {
            if (isButtonSelected(dayButton)) {
                selectedDays.append(dayButton.getText()).append(" ");
            }
        }
        boolean skipHolidays = skipHolidaysSwitch.isChecked();
        String alarmName = alarmNameEditText.getText().toString();

        System.out.println("设置的闹钟时间：" + hour + ":" + minute);
        System.out.println("响铃规则：" + ringRule);
        System.out.println("选择的日期：" + selectedDays.toString());
        System.out.println("法定节假日不响铃：" + skipHolidays);
        System.out.println("闹钟名称：" + alarmName);

        AlarmEntity newAlarmEntity = new AlarmEntity(new WeekAlarmType(new int[]{1, 1, 1, 1, 1, 1, 0}), hour + ":" + minute, true);

//        alarmList.add(new Alarm(new WeekAlarm(new int[]{1, 1, 1, 1, 1, 1, 0}), "07:20", true));
//        alarmList.add(new Alarm("07:24", true));
//        alarmList.add(new Alarm(new EveryDayAlarm(), "07:30", true));
//        alarmList.add(new Alarm(new DateAlarm(2025, 5, 23), "07:40", true));
//        alarmList.add(new Alarm(new DateAlarm(2025, 5, 23, true), "07:41", true));
//
//        CustomTypeAlarm customTypeAlarm = new DateAlarm(2025, null, 23);
//        customTypeAlarm.setWorkingDay(true);
//        alarmList.add(new Alarm(customTypeAlarm, "07:42", true));
//
//        CustomTypeAlarm customTypeAlarm1 = new DateAlarm(2025, 5, null);
//        customTypeAlarm1.setHoliday(true);
//        alarmList.add(new Alarm(customTypeAlarm1, "07:43", true));
//
//        alarmList.add(new Alarm(new HolidayAlarm(), "07:50", false));
//        alarmList.add(new Alarm(new WorkingDayAlarm(), "07:51", true));

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
                Log.i("terry", "insertAlarmEntity:" + id);
                newAlarmEntity.setId(id);
                AlarmUtils.setAlarm(getApplicationContext(), newAlarmEntity);
                finish();
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestScheduleExactAlarmPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.SCHEDULE_EXACT_ALARM)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SCHEDULE_EXACT_ALARM},
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
}