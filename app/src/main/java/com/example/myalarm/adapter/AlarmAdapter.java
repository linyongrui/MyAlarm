package com.example.myalarm.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.util.AlarmUtils;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;


public class AlarmAdapter extends ListAdapter<AlarmEntity, AlarmAdapter.AlarmViewHolder> {
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
                    oldItem.isEnabled() == newItem.isEnabled();
        }
    };

    public interface OnItemClickListener {
        void onItemClick(AlarmEntity alarm);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }

    public interface OnMultiSelectStartListener {
        void onMultiSelectStart(boolean multiSelectMode);
    }

    private OnMultiSelectStartListener multiSelectListener;

    public void setOnMultiSelectStartListener(OnMultiSelectStartListener listener) {
        this.multiSelectListener = listener;
    }

    private boolean multiSelectMode = false;

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

    public boolean isInMultiSelectMode() {
        return multiSelectMode;
    }

    private Set<Long> selectedIds = new HashSet<>();

    public Set<Long> getSelectedIds() {
        return selectedIds;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_alarm_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        AlarmEntity alarmEntity = getItem(position);
        holder.bind(alarmEntity);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeStr = alarmEntity.getTime().format(formatter);
        holder.timeTextView.setText(timeStr);
        holder.repeatTextView.setText(alarmEntity.getRepeatStr());

        holder.alarmSwitch.setOnCheckedChangeListener(null);
        holder.alarmSwitch.setChecked(alarmEntity.isEnabled());
        holder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    alarmEntity.setEnabled(true);
                    AlarmUtils.updateAlarmEnabled(holder.alarmSwitch.getContext(), alarmEntity);
                } else {
                    showConfirmationDialog(holder.alarmSwitch, alarmEntity);
                }
            }
        });
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView repeatTextView;
        Switch alarmSwitch;
        CheckBox checkBox;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            repeatTextView = itemView.findViewById(R.id.repeatTextView);
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

    private String alertDialogDismissByWho = null;

    private void showConfirmationDialog(Switch alarmSwitch, AlarmEntity alarmEntity) {
        alertDialogDismissByWho = null;
        Context context = alarmSwitch.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("要关闭此重复闹钟吗？")
                .setPositiveButton("仅明天关闭一次", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "仅明天关闭一次闹钟", Toast.LENGTH_SHORT).show();
                        alertDialogDismissByWho = "once";
                        alarmEntity.setEnabled(false);
                        alarmSwitch.setChecked(false);
                        AlarmUtils.updateAlarmEnabled(context, alarmEntity);
                    }
                })
                .setNegativeButton("关闭此重复闹钟", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "关闭此重复闹钟", Toast.LENGTH_SHORT).show();
                        alertDialogDismissByWho = "close";
                        alarmEntity.setEnabled(false);
                        alarmSwitch.setChecked(false);
                        AlarmUtils.updateAlarmEnabled(context, alarmEntity);
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialogDismissByWho = "cancel";
                        alarmEntity.setEnabled(true);
                        alarmSwitch.setChecked(true);
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (alertDialogDismissByWho == null) {
                            alarmEntity.setEnabled(true);
                            alarmSwitch.setChecked(true);
                        }
                    }
                })
                .show();
    }
}