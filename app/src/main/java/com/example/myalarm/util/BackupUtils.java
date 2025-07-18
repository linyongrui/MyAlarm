package com.example.myalarm.util;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.myalarm.alarmtype.BaseAlarmType;
import com.example.myalarm.dao.AlarmDao;
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.util.serializer.AlarmTypeAdapter;
import com.example.myalarm.util.serializer.LocalTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class BackupUtils {
    public static void exportToJson(Context context, Uri targetUri) {
        new Thread(() -> {
            try {
                AlarmDao dao = AlarmUtils.alarmDao;
                List<AlarmEntity> alarms = dao.getAllAlarms();

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                        .registerTypeAdapter(BaseAlarmType.class, new AlarmTypeAdapter())
                        .create();
                String json = gson.toJson(alarms);

//                Log.i("terry", "exportToJson:" + json);

                OutputStream os = context.getContentResolver().openOutputStream(targetUri);
                os.write(json.getBytes(StandardCharsets.UTF_8));
                os.close();

                showToast(context, "导出成功");
            } catch (Exception e) {
                e.printStackTrace();
                showToast(context, "导出失败");
            }
        }).start();
    }

    public static void importFromJson(Context context, Uri sourceUri) {
        new Thread(() -> {
            try (InputStream is = context.getContentResolver().openInputStream(sourceUri);
                 BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

                String json = br.lines().collect(Collectors.joining("\n"));

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                        .registerTypeAdapter(BaseAlarmType.class, new AlarmTypeAdapter())
                        .create();
                Type listType = new TypeToken<List<AlarmEntity>>() {
                }.getType();
                List<AlarmEntity> alarms = gson.fromJson(json, listType);

                AlarmUtils.deleteAllAlarms(context);
                for (AlarmEntity alarmEntity : alarms) {
                    alarmEntity.setId(0);
                    alarmEntity.setRequestCodeSeq(0);
                    alarmEntity.setAlreadyRingTimes(0);
                    alarmEntity.setNextTriggerTime(0);
                }
                AlarmUtils.saveAlarm(context, alarms);

                showToast(context, "导入成功"+alarms.size()+"个闹钟");
            } catch (Exception e) {
                e.printStackTrace();
                showToast(context, "导入失败");
            }
        }).start();
    }

    private static void showToast(Context context, String msg) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        );
    }

}
