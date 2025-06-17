package com.example.myalarm.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myalarm.R;
import com.example.myalarm.alarmtype.OnceAlarmType;
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.util.AlarmUtils;
import com.example.myalarm.util.DateTimeUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;


public class AlarmAdapter extends ListAdapter<AlarmEntity, AlarmAdapter.AlarmViewHolder> {

    private OnItemClickListener listener;
    private OnMultiSelectStartListener multiSelectListener;
    private boolean multiSelectMode = false;
    private Set<Long> selectedIds = new HashSet<>();
    private String alertDialogDismissByWho = null;

    public AlarmAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<AlarmEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<AlarmEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull AlarmEntity oldItem, @NonNull AlarmEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull AlarmEntity oldItem, @NonNull AlarmEntity newItem) {
            return oldItem.getTime().equals(newItem.getTime()) &&
                    oldItem.getRepeatStr().equals(newItem.getRepeatStr()) &&
                    oldItem.isDisabled() == newItem.isDisabled() &&
                    oldItem.isTempDisabled() == newItem.isTempDisabled();
        }
    };

    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }

    public void setOnMultiSelectStartListener(OnMultiSelectStartListener listener) {
        this.multiSelectListener = listener;
    }

    public Set<Long> getSelectedIds() {
        return selectedIds;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMultiSelectMode(boolean multiSelectMode) {
        this.multiSelectMode = multiSelectMode;
        if (multiSelectListener != null) {
            multiSelectListener.onMultiSelectStart(multiSelectMode);
            notifyDataSetChanged();
            if (!multiSelectMode) {
                selectedIds.clear();
            }
        }
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_alarm_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        AlarmEntity alarmEntity = getItem(position);
        holder.bind(alarmEntity);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeStr = alarmEntity.getTime().format(timeFormatter);
        holder.timeTextView.setText(timeStr);
        holder.repeatTextView.setText(alarmEntity.getRepeatStr());
        holder.alarmNameTextView.setText(alarmEntity.getName());
        alarmSwitchStyleHandle(holder, alarmEntity, false);

        holder.alarmSwitch.setOnCheckedChangeListener(null);
        holder.alarmSwitch.setChecked(!alarmEntity.isDisabled() && !alarmEntity.isTempDisabled());
        holder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    alarmEntity.setDisabled(false);
                    alarmEntity.setTempDisabled(false);
                    AlarmUtils.updateAlarmEnabled(holder.alarmSwitch.getContext(), alarmEntity);
                    alarmSwitchStyleHandle(holder, alarmEntity, true);

                } else if (OnceAlarmType.ALARM_TYPE.equals(alarmEntity.getBaseAlarmType().getType())) {
                    alarmEntity.setTempDisabled(false);
                    alarmEntity.setDisabled(true);
                    AlarmUtils.updateAlarmEnabled(holder.alarmSwitch.getContext(), alarmEntity);
                    alarmSwitchStyleHandle(holder, alarmEntity, true);

                } else {
                    showConfirmationDialog(holder, holder.alarmSwitch, alarmEntity);
                }
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void alarmSwitchStyleHandle(AlarmViewHolder holder, AlarmEntity alarmEntity, boolean recalculate) {
        boolean disabled = alarmEntity.isDisabled();
        boolean tempDisabled = alarmEntity.isTempDisabled();
        long preTriggerTime = alarmEntity.getPreTriggerTime();
        long nextTriggerTime = alarmEntity.getNextTriggerTime();
        int colorGray = holder.alarmSwitch.getContext().getResources().getColor(R.color.gray, null);
        int colorBlack = holder.alarmSwitch.getContext().getResources().getColor(R.color.black, null);
        int colorGrayBlack = holder.alarmSwitch.getContext().getResources().getColor(R.color.gray_black, null);
        StringBuilder nextTriggerDateStr = new StringBuilder();
        if (disabled) {
            holder.timeTextView.setTextColor(colorGray);
            holder.alarmNameTextView.setTextColor(colorGray);
            nextTriggerDateStr.append("闹钟已关闭");
        } else {
            if (recalculate) {
                if (tempDisabled) {
                    preTriggerTime = nextTriggerTime;
                    nextTriggerTime = AlarmUtils.getNextTriggerTime(alarmEntity);
                } else {
                    preTriggerTime = 0;
                    nextTriggerTime = AlarmUtils.getNextTriggerTime(alarmEntity);
                }
            }
            LocalDate preTriggerDate = DateTimeUtils.long2LocalDateTime(preTriggerTime).toLocalDate();
            LocalDate nextTriggerDate = DateTimeUtils.long2LocalDateTime(nextTriggerTime).toLocalDate();
            String preDateStr = DateTimeUtils.getDateStr(preTriggerDate);
            String nextDateStr = DateTimeUtils.getDateStr(nextTriggerDate);
            if (tempDisabled) {
                holder.timeTextView.setTextColor(colorGray);
                holder.alarmNameTextView.setTextColor(colorGray);
                if (LocalDate.now().isAfter(preTriggerDate)) {
                    alarmEntity.setDisabled(false);
                    alarmEntity.setTempDisabled(false);
                    AlarmUtils.updateAlarmEnabled(holder.alarmSwitch.getContext(), alarmEntity);
                    alarmSwitchStyleHandle(holder, alarmEntity, true);
                } else {
                    nextTriggerDateStr.append(preDateStr + "已关闭，");
                    nextTriggerDateStr.append("下次响铃将在" + nextDateStr);
                }
            } else {
                holder.timeTextView.setTextColor(colorBlack);
                holder.alarmNameTextView.setTextColor(colorGrayBlack);
                nextTriggerDateStr.append("下次响铃将在" + nextDateStr);
            }
        }
        holder.nextTriggerTextView.setText(nextTriggerDateStr);
    }

    private void showConfirmationDialog(AlarmViewHolder holder, Switch alarmSwitch, AlarmEntity alarmEntity) {
        alertDialogDismissByWho = null;
        long nextTriggerTime = AlarmUtils.getNextTriggerTime(alarmEntity);
        LocalDate nextTriggerDate = DateTimeUtils.long2LocalDateTime(nextTriggerTime).toLocalDate();
        StringBuilder tempDisableDesc = new StringBuilder();
        tempDisableDesc.append("仅");
        tempDisableDesc.append(DateTimeUtils.getDateStr(nextTriggerDate));
        tempDisableDesc.append("关闭一次");

        Context context = holder.alarmNameTextView.getContext();

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogCustom);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null);
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (alertDialogDismissByWho == null) {
                    alarmEntity.setTempDisabled(false);
                    alarmEntity.setDisabled(false);
                    alarmSwitch.setChecked(true);
                }
            }
        });

        TextView closeOnceButton = dialogView.findViewById(R.id.btn_close_once);
        TextView closeRepeatButton = dialogView.findViewById(R.id.btn_close_repeat);
        TextView cancelButton = dialogView.findViewById(R.id.btn_cancel);

        closeOnceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, tempDisableDesc, Toast.LENGTH_SHORT).show();
                alertDialogDismissByWho = "once";
                alarmEntity.setTempDisabled(true);
                alarmEntity.setDisabled(false);
                alarmSwitch.setChecked(false);
                AlarmUtils.updateAlarmEnabled(context, alarmEntity);
                alarmSwitchStyleHandle(holder, alarmEntity, true);
                bottomSheetDialog.dismiss();
            }
        });

        closeRepeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "关闭此重复闹钟", Toast.LENGTH_SHORT).show();
                alertDialogDismissByWho = "close";
                alarmEntity.setTempDisabled(false);
                alarmEntity.setDisabled(true);
                alarmSwitch.setChecked(false);
                AlarmUtils.updateAlarmEnabled(context, alarmEntity);
                alarmSwitchStyleHandle(holder, alarmEntity, true);
                bottomSheetDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogDismissByWho = "cancel";
                alarmEntity.setTempDisabled(false);
                alarmEntity.setDisabled(false);
                alarmSwitch.setChecked(true);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView repeatTextView;
        TextView alarmNameTextView;
        TextView nextTriggerTextView;
        Switch alarmSwitch;
        CheckBox checkBox;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            repeatTextView = itemView.findViewById(R.id.repeatTextView);
            alarmNameTextView = itemView.findViewById(R.id.alarmNameTextView);
            nextTriggerTextView = itemView.findViewById(R.id.nextTriggerTextView);
            alarmSwitch = itemView.findViewById(R.id.alarmSwitch);
            checkBox = itemView.findViewById(R.id.checkbox);
        }

        @SuppressLint("NotifyDataSetChanged")
        public void bind(AlarmEntity alarm) {
            itemView.setOnLongClickListener(v -> {
                if (multiSelectListener != null) {
                    multiSelectListener.onMultiSelectStart(true);
                    notifyDataSetChanged();
                }
                multiSelectMode = true;
                selectedIds.add(alarm.getId());
                return true;
            });

            itemView.setOnClickListener(v -> {
                if (multiSelectMode) {
                    if (selectedIds.contains(alarm.getId())) {
                        selectedIds.remove(alarm.getId());
                    } else {
                        selectedIds.add(alarm.getId());
                    }
                    notifyItemChanged(getAdapterPosition());
                } else {
                    if (listener != null) listener.onItemClick(alarm);
                }
            });

            checkBox.setVisibility(multiSelectMode ? View.VISIBLE : View.GONE);
            checkBox.setChecked(selectedIds.contains(alarm.getId()));
            alarmSwitch.setVisibility(multiSelectMode ? View.GONE : View.VISIBLE);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(AlarmEntity alarm);
    }

    public interface OnMultiSelectStartListener {
        void onMultiSelectStart(boolean multiSelectMode);
    }
}