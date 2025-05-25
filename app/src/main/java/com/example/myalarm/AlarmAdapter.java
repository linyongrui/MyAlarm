package com.example.myalarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myalarm.entity.Alarm;


public class AlarmAdapter extends ListAdapter<Alarm, AlarmAdapter.AlarmViewHolder> {
    public AlarmAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Alarm> DIFF_CALLBACK = new DiffUtil.ItemCallback<Alarm>() {
        @Override
        public boolean areItemsTheSame(@NonNull Alarm oldItem, @NonNull Alarm newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Alarm oldItem, @NonNull Alarm newItem) {
            return oldItem.getTime().equals(newItem.getTime()) &&
                    oldItem.getRepeatStr().equals(newItem.getRepeatStr()) &&
                    oldItem.isEnabled() == newItem.isEnabled();
        }
    };

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = getItem(position);
        holder.timeTextView.setText(alarm.getTime());
        holder.repeatTextView.setText(alarm.getRepeatStr());
        holder.alarmSwitch.setChecked(alarm.isEnabled());

        // 设置开关状态变化监听器
        holder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 更新闹钟状态
                alarm.setEnabled(isChecked);
                // 可以在这里添加闹钟启用/禁用的逻辑
                String status = isChecked ? "启用" : "禁用";
                String message = "闹钟 " + alarm.getTime() + " 已" + status;
                android.widget.Toast.makeText(buttonView.getContext(), message, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView repeatTextView;
        Switch alarmSwitch;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            repeatTextView = itemView.findViewById(R.id.repeatTextView);
            alarmSwitch = itemView.findViewById(R.id.alarmSwitch);
        }
    }
}