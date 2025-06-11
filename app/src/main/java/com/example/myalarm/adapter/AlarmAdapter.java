package com.example.myalarm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myalarm.R;
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.util.AlarmUtils;

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

    private Set<Long> selectedIds = new HashSet<>();
    private boolean multiSelectMode = false;

    public void setMultiSelectMode(boolean enabled) {
        multiSelectMode = enabled;
        notifyDataSetChanged();
    }

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
        String timeStr = alarmEntity.getTime().toString();
        holder.timeTextView.setText(timeStr);
        holder.repeatTextView.setText(alarmEntity.getRepeatStr());

        holder.alarmSwitch.setOnCheckedChangeListener(null);
        holder.alarmSwitch.setChecked(alarmEntity.isEnabled());
        holder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarmEntity.setEnabled(isChecked);
                String status = isChecked ? "启用" : "禁用";
                String message = "闹钟 " + timeStr + " 已" + status;
                android.widget.Toast.makeText(buttonView.getContext(), message, android.widget.Toast.LENGTH_SHORT).show();
                AlarmUtils.updateAlarmEnabled(holder.alarmSwitch.getContext(), alarmEntity);
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

        public void bind(AlarmEntity alarm) {
            itemView.setOnLongClickListener(v -> {
                multiSelectMode = true;
                selectedIds.add(alarm.getId());
                notifyDataSetChanged();
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

//            parent.findViewById(R.id.main_delete_header).setVisibility(multiSelectMode ? View.VISIBLE : View.GONE);
//            parent.findViewById(R.id.toolbar).setVisibility(multiSelectMode ? View.GONE : View.VISIBLE);
//            parent.findViewById(R.id.addAlarmButton).setVisibility(multiSelectMode ? View.GONE : View.VISIBLE);
        }
    }
}