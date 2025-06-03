package com.example.myalarm.util;

import android.util.Log;

import com.example.myalarm.dao.HolidayDao;
import com.example.myalarm.data.DatabaseClient;
import com.example.myalarm.dto.HolidayDto;
import com.example.myalarm.entity.HolidayEntity;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HolidayUtils {
    public static List<HolidayEntity> getHolidays() {
        List<HolidayEntity> holidayEntities = getHolidayEntities();
        Log.i("terry", "holidayEntities.isEmpty(): " + holidayEntities.isEmpty());

        if (holidayEntities.isEmpty()) {
            List<HolidayEntity> newHolidayEntities = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://unpkg.com/holiday-calendar@1.1.6/data/CN/2025.json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Log.e("terry", "请求失败: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();

                        Gson gson = new Gson();
                        HolidayDto holidayDto = gson.fromJson(responseData, HolidayDto.class);
                        if (holidayDto != null) {
                            int year = holidayDto.getYear();
                            String region = holidayDto.getRegion();
                            List<HolidayDto.DateDto> dateDtos = holidayDto.getDates();
                            for (HolidayDto.DateDto dateDto : dateDtos) {
                                newHolidayEntities.add(new HolidayEntity(year, region, dateDto.getDate(), dateDto.getName(),
                                        dateDto.getNameCn(), dateDto.getNameEn(), dateDto.getType()));
                                System.out.println("日期: " + dateDto.getDate() + ", 名称: " + dateDto.getName() + ", 类型: " + dateDto.getType());
                                System.out.println("newHolidayEntities: " + newHolidayEntities.size());
                            }
                            holidayEntities.addAll(newHolidayEntities);
                            saveNewHolidayEntities(newHolidayEntities);
                        }
                    } else {
                        Log.e("terry", "请求失败: " + response.code());
                    }
                }
            });
//            holidayEntities.addAll(newHolidayEntities);
        }
        return holidayEntities;
    }


    private static List<HolidayEntity> getHolidayEntities() {
        List<HolidayEntity> holidayEntities = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HolidayDao holidayDao = DatabaseClient.getInstance().getHolidayEntityDatabase().holidayDao();
                holidayEntities.addAll(holidayDao.getHolidaysByYear(LocalDate.now().getYear()));
            }
        }).start();
        return holidayEntities;
    }

    private static void saveNewHolidayEntities(List<HolidayEntity> newHolidayEntities) {
        HolidayEntity[] holidayEntitiesArray = newHolidayEntities.stream().toArray(HolidayEntity[]::new);
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseClient.getInstance().getHolidayEntityDatabase().holidayDao().insertAll(holidayEntitiesArray);
            }
        }).start();
    }
}
