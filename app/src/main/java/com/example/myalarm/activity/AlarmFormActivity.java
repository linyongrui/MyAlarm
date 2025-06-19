package com.example.myalarm.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myalarm.R;
import com.example.myalarm.alarmtype.BaseAlarmType;
import com.example.myalarm.alarmtype.DateAlarmType;
import com.example.myalarm.alarmtype.EveryDayAlarmType;
import com.example.myalarm.alarmtype.HolidayAlarmType;
import com.example.myalarm.alarmtype.OnceAlarmType;
import com.example.myalarm.alarmtype.WeekAlarmType;
import com.example.myalarm.alarmtype.WorkingDayAlarmType;
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.util.AlarmUtils;
import com.example.myalarm.util.PermissionUtils;
import com.example.myalarm.viewmodel.AlarmViewModel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlarmFormActivity extends AppCompatActivity {

    private static final List<SpinnerOption> alarmTypeList = new ArrayList<>();
    private static final List<SpinnerOption> vibrationList = new ArrayList<>();
    private static final List<SpinnerOption> ringTimesList = new ArrayList<>();
    private static final List<SpinnerOption> ringIntervalList = new ArrayList<>();

    {
        alarmTypeList.clear();
        alarmTypeList.add(new SpinnerOption(OnceAlarmType.ALARM_TYPE, "响一次"));
        alarmTypeList.add(new SpinnerOption(EveryDayAlarmType.ALARM_TYPE, "每天"));
        alarmTypeList.add(new SpinnerOption(WorkingDayAlarmType.ALARM_TYPE, "法定工作日"));
        alarmTypeList.add(new SpinnerOption(HolidayAlarmType.ALARM_TYPE, "非工作日"));
        alarmTypeList.add(new SpinnerOption(WeekAlarmType.ALARM_TYPE, "按周"));
        alarmTypeList.add(new SpinnerOption(DateAlarmType.ALARM_TYPE, "按日期"));
        vibrationList.clear();
        vibrationList.add(new SpinnerOption("default", "默认震动"));
        vibrationList.add(new SpinnerOption("none", "无"));
        ringTimesList.clear();
        ringTimesList.add(new SpinnerOption("1", "1次"));
        ringTimesList.add(new SpinnerOption("2", "2次"));
        ringTimesList.add(new SpinnerOption("3", "3次"));
        ringTimesList.add(new SpinnerOption("5", "5次"));
        ringIntervalList.clear();
        ringIntervalList.add(new SpinnerOption("5", "5分钟"));
        ringIntervalList.add(new SpinnerOption("10", "10分钟"));
        ringIntervalList.add(new SpinnerOption("15", "15分钟"));
        ringIntervalList.add(new SpinnerOption("20", "20分钟"));
        ringIntervalList.add(new SpinnerOption("30", "30分钟"));
    }

    private AlarmViewModel viewModel;
    private AlarmEntity currentAlarm = null;

    private TimePicker timePicker;
    private Spinner ringRuleSpinner;
    private TextView repeatDatesTextView;
    private LinearLayout llDaysOfWeek;
    private ToggleButton[] dayButtons;
    private LinearLayout llSkip;
    private Switch skipWorkingDaysSwitch;
    private Switch skipHolidaysSwitch;
    private EditText alarmNameEditText;
    private SeekBar ringtoneSeekBar;
    private LinearLayout ringtoneSilent;
    private Spinner vibrationSpinner;
    private Spinner ringTimesSpinner;
    private Spinner ringIntervalSpinner;

    private void populateUiWithAlarm(AlarmEntity originalAlarm) {
        TextView tvFormAlarmTitle = findViewById(R.id.tv_form_alarm_title);
        tvFormAlarmTitle.setText("编辑闹钟");
        LocalTime time = originalAlarm.getTime();
        timePicker.setHour(time.getHour());
        timePicker.setMinute(time.getMinute());

        BaseAlarmType originalBaseAlarmType = originalAlarm.getBaseAlarmType();

        ArrayAdapter<SpinnerOption> adapter = (ArrayAdapter<SpinnerOption>) ringRuleSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).getOptionId().equals(originalBaseAlarmType.getType())) {
                ringRuleSpinner.setSelection(i);
                break;
            }
        }
        if (Objects.equals(originalBaseAlarmType.getType(), WeekAlarmType.ALARM_TYPE)) {
            WeekAlarmType originalWeekAlarmType = (WeekAlarmType) originalBaseAlarmType;
            int[] weekDayCheck = originalWeekAlarmType.getWeekDayCheck();
            for (int i = 0; i < dayButtons.length; i++) {
                dayButtons[i].setChecked(weekDayCheck[i] == 1);
            }
        }
        skipWorkingDaysSwitch.setChecked(originalBaseAlarmType.getSkipWorkingDay());
        skipHolidaysSwitch.setChecked(originalBaseAlarmType.getSkipHoliday());
        alarmNameEditText.setText(originalAlarm.getName());
        ringtoneSeekBar.setProgress(originalAlarm.getRingtoneProgress());

        ArrayAdapter<SpinnerOption> vibrationAdapter = (ArrayAdapter<SpinnerOption>) vibrationSpinner.getAdapter();
        String vibrationSpinnerId = originalAlarm.isVibrator() ? "default" : "none";
        for (int i = 0; i < vibrationAdapter.getCount(); i++) {
            if (vibrationSpinnerId.equals(vibrationAdapter.getItem(i).getOptionId())) {
                vibrationSpinner.setSelection(i);
                break;
            }
        }

        ArrayAdapter<SpinnerOption> ringTimesAdapter = (ArrayAdapter<SpinnerOption>) ringTimesSpinner.getAdapter();
        for (int i = 0; i < ringTimesAdapter.getCount(); i++) {
            if (Integer.valueOf(ringTimesAdapter.getItem(i).getOptionId()) == originalAlarm.getRingTimes()) {
                ringTimesSpinner.setSelection(i);
                break;
            }
        }

        ArrayAdapter<SpinnerOption> ringIntervalAdapter = (ArrayAdapter<SpinnerOption>) ringIntervalSpinner.getAdapter();
        for (int i = 0; i < ringIntervalAdapter.getCount(); i++) {
            if (Integer.valueOf(ringIntervalAdapter.getItem(i).getOptionId()) == originalAlarm.getRingInterval()) {
                ringIntervalSpinner.setSelection(i);
                break;
            }
        }
    }

    @SuppressLint({"ResourceAsColor", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_form_alarm);

        long alarmId = getIntent().getLongExtra("alarm_id", -1);
        viewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        viewModel.getAlarmById(alarmId).observe(this, originalAlarm -> {
            if (originalAlarm != null) {
                currentAlarm = originalAlarm;
                populateUiWithAlarm(originalAlarm);
            }
        });

        Context context = getApplicationContext();
        PermissionUtils.overlayPermissionCheck(this);

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

        ringRuleSpinner = spinnerInit(context, alarmTypeList, R.id.spinner_ring_rule);

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

        ringtoneSilent = findViewById(R.id.iv_ringtone_silent);
        ringtoneSeekBar = findViewById(R.id.sb_ringtone);
        ringtoneSeekBar.setProgress(90);
        ringtoneSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    ringtoneSilent.setVisibility(View.VISIBLE);
                } else {
                    ringtoneSilent.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 用户开始拖动 SeekBar（可选）
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 用户松手时调用（可选）
            }
        });

        vibrationSpinner = spinnerInit(context, vibrationList, R.id.spinner_vibration);
        ringTimesSpinner = spinnerInit(context, ringTimesList, R.id.spinner_ring_times);
        ringIntervalSpinner = spinnerInit(context, ringIntervalList, R.id.spinner_ring_interval);
    }

    private void ringRuleSpinnerSelectHandle(String optionId) {
        llDaysOfWeek.setVisibility(View.GONE);
        llSkip.setVisibility(View.GONE);

        String repeatStrNote = "";
        switch (optionId) {
            case DateAlarmType.ALARM_TYPE:
                llSkip.setVisibility(View.VISIBLE);
                break;
            case WeekAlarmType.ALARM_TYPE:
                llDaysOfWeek.setVisibility(View.VISIBLE);
                llSkip.setVisibility(View.VISIBLE);
                break;
            case HolidayAlarmType.ALARM_TYPE:
                repeatStrNote = "(包括节假日、周末、不包括补班)";
                break;
            case WorkingDayAlarmType.ALARM_TYPE:
                repeatStrNote = "(包括补班)";
                break;
            case EveryDayAlarmType.ALARM_TYPE:
            case OnceAlarmType.ALARM_TYPE:
                break;
            default:
                throw new IllegalArgumentException("Unknown alarm type: " + optionId);
        }

        repeatDatesTextView.setText(getNewAlarmEntity().getRepeatStr() + repeatStrNote);
        updateTimeUntilNextRing();
    }

    private void ringTimesSpinnerSelectHandle(String optionId) {
        if ("1".equals(optionId)) {
            ringIntervalSpinner.setVisibility(View.INVISIBLE);
        } else {
            ringIntervalSpinner.setVisibility(View.VISIBLE);
        }
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
        if (WeekAlarmType.ALARM_TYPE.equals(newAlarmEntity.getBaseAlarmType().getType())) {
            WeekAlarmType weekAlarmType = (WeekAlarmType) newAlarmEntity.getBaseAlarmType();
            int[] checkedArray = weekAlarmType.getWeekDayCheck();
            boolean isAllUnchecked = true;
            for (int i = 0; i < 7; i++) {
                if (checkedArray[i] == 1) {
                    isAllUnchecked = false;
                }
            }
            if (isAllUnchecked) {
                Toast.makeText(this.getApplicationContext(), "请选择周几", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (currentAlarm == null) {
                    AlarmUtils.saveAlarm(getApplicationContext(), newAlarmEntity);
                } else {
                    newAlarmEntity.setId(currentAlarm.getId());
                    newAlarmEntity.setDisabled(false);
                    newAlarmEntity.setTempDisabled(false);
                    newAlarmEntity.setRequestCodeSeq(currentAlarm.getRequestCodeSeq());
                    AlarmUtils.updateAlarm(getApplicationContext(), newAlarmEntity);
                }
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
        int ringtoneProgress = ringtoneSeekBar.getProgress();

        String vibrationId = ((SpinnerOption) vibrationSpinner.getSelectedItem()).getOptionId();
        boolean isVibrator = !"none".equals(vibrationId);
        int ringTimes = Integer.valueOf(((SpinnerOption) ringTimesSpinner.getSelectedItem()).getOptionId());
        int ringInterval = Integer.valueOf(((SpinnerOption) ringIntervalSpinner.getSelectedItem()).getOptionId());
        return new AlarmEntity(alarmName, baseAlarmType, time, ringtoneProgress, isVibrator, ringTimes, ringInterval);
    }

    private Spinner spinnerInit(Context context, List<SpinnerOption> spinnerOptionList, int spinnerId) {

        Spinner spinner = findViewById(spinnerId);
        ArrayAdapter<SpinnerOption> arrayAdapterdapter = new ArrayAdapter<>(
                context,
                R.layout.custom_spinner_item,
                spinnerOptionList
        );
        arrayAdapterdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapterdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerOption selectedOption = (SpinnerOption) parent.getItemAtPosition(position);
                if (spinnerId == R.id.spinner_ring_rule) {
                    ringRuleSpinnerSelectHandle(selectedOption.getOptionId());
                } else if (spinnerId == R.id.spinner_ring_times) {
                    ringTimesSpinnerSelectHandle(selectedOption.getOptionId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(context, "必选", Toast.LENGTH_SHORT).show();
            }
        });
        return spinner;
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