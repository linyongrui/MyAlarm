package com.example.myalarm.util;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.example.myalarm.dao.AlarmDao;
import com.example.myalarm.entity.AlarmEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class BackupUtils {
    public static void exportToJson(Context context, Uri targetUri) {
        new Thread(() -> {
            try {
                AlarmDao dao = AlarmUtils.alarmDao;
                List<AlarmEntity> alarms = dao.getAllAlarms(); // 用 blocking 方法

                Gson gson = new Gson();
                String json = gson.toJson(alarms);

                OutputStream os = context.getContentResolver().openOutputStream(targetUri);
                os.write(json.getBytes(StandardCharsets.UTF_8));
                os.close();

//                showToast(context, "导出成功");
            } catch (Exception e) {
                e.printStackTrace();
//                showToast(context, "导出失败");
            }
        }).start();
    }

    public static void importFromJson(Context context, Uri sourceUri) {
        new Thread(() -> {
            try {
                InputStream is = context.getContentResolver().openInputStream(sourceUri);
                String json = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                is.close();

                Gson gson = new Gson();
                Type listType = new TypeToken<List<AlarmEntity>>() {}.getType();
                List<AlarmEntity> alarms = gson.fromJson(json, listType);

//                AlarmDao dao = AlarmUtils.alarmDao;
                for(AlarmEntity alarmEntity:alarms) {
                    AlarmUtils.saveAlarm(context ,alarmEntity); // 覆盖旧记录前可先 dao.clear()
                }

//                showToast(context, "导入成功");
            } catch (Exception e) {
                e.printStackTrace();
//                showToast(context, "导入失败");
            }
        }).start();
    }

    private static void showToast(Context context, String msg) {

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
