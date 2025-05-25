package com.example.myalarm;

import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myalarm.alarmtype.WeekAlarmType;
import com.example.myalarm.data.DatabaseClient;
import com.example.myalarm.entity.Alarm;

import java.util.Calendar;

public class NewAlarmActivity extends AppCompatActivity {

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

        // 初始化顶部栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 初始化各控件
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

        // 设置响铃规则下拉框选项
        String[] ringRuleOptions = {"响一次", "法定节假日"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ringRuleOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ringRuleSpinner.setAdapter(adapter);

        // 时间选择器监听器
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // 这里可处理时间选择后的逻辑
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                // 示例：更新距离下次响铃时间文本
                updateTimeUntilNextRing(calendar);
            }
        });

        // 响铃规则下拉框选择监听器
        ringRuleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRule = (String) parent.getItemAtPosition(position);
                // 这里可处理响铃规则选择后的逻辑
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 日期按钮点击监听器
        for (Button dayButton : dayButtons) {
            dayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button = (Button) v;
                    // 这里可处理日期选择后的逻辑，如改变按钮背景表示选中状态
                    if (isButtonSelected(button)) {
                        unselectButton(button);
                    } else {
                        selectButton(button);
                    }
                }
            });
        }

        // 法定节假日不响铃开关监听器
        skipHolidaysSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 这里可处理开关状态改变后的逻辑
            }
        });

        // 顶部栏取消按钮点击事件
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 顶部栏完成按钮点击事件
        findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 这里可处理完成新建闹钟的逻辑，如保存闹钟设置到数据库等
                saveAlarmSettings();
//                Intent intent = new Intent(NewAlarmActivity.this, MainActivity.class);
//                startActivity(intent);
            }
        });
    }

    private boolean isButtonSelected(Button button) {
        // 可根据按钮背景色或其他标识判断是否选中，这里简单示例
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
        // 这里简单示例更新距离下次响铃时间文本，实际需根据业务逻辑准确计算
        TextView timeUntilNextRingTextView = findViewById(R.id.tv_time_until_next_ring);
        timeUntilNextRingTextView.setText("距离下次响铃还有 " + getTimeDiff(calendar) + " 天");
    }

    private int getTimeDiff(Calendar calendar) {
        // 示例：计算与当前时间的天数差值，实际需更准确逻辑
        Calendar now = Calendar.getInstance();
        long diffInMillis = calendar.getTimeInMillis() - now.getTimeInMillis();
        return (int) (diffInMillis / (24 * 60 * 60 * 1000));
    }

    private void saveAlarmSettings() {
        // 这里需实现保存闹钟设置到数据库或其他存储方式的逻辑
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

        // 示例打印设置信息，实际应保存到合适位置
        System.out.println("设置的闹钟时间：" + hour + ":" + minute);
        System.out.println("响铃规则：" + ringRule);
        System.out.println("选择的日期：" + selectedDays.toString());
        System.out.println("法定节假日不响铃：" + skipHolidays);
        System.out.println("闹钟名称：" + alarmName);

        Alarm newAlarm = new Alarm(new WeekAlarmType(new int[]{1, 1, 1, 1, 1, 1, 0}), hour + ":" + minute, true);

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseClient.getInstance()
//                DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .alarmDao()
                        .insertAlarm(newAlarm);

                // 保存成功后关闭Activity
                finish();
            }
        }).start();
    }
}