package com.example.myalarm.util;

import android.util.Log;

import com.example.myalarm.dto.HolidayDto;
import com.example.myalarm.entity.HolidayEntity;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Response;

public class HolidayUtils {
    public static final String GET_HOLIDAY_URL = "https://unpkg.com/holiday-calendar@1.1.6/data/CN/{year}.min.json";
    private static Set<String> holidaySet = new HashSet();
    private static Set<String> transferWorkdaySet = new HashSet<>();
    private static final Set<String> UNMODIFIABLE_HOLIDAY_SET = Collections.unmodifiableSet(holidaySet);
    private static final Set<String> UNMODIFIABLE_TRANSFER_WORKDAY_SET = Collections.unmodifiableSet(transferWorkdaySet);

    public static List<HolidayEntity> getHolidayEntityList(Response response) {
        List<HolidayEntity> holidayEntities = new ArrayList<>();
        try {
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                Gson gson = new Gson();
                HolidayDto holidayDto = gson.fromJson(responseData, HolidayDto.class);
                if (holidayDto != null) {
                    int year = holidayDto.getYear();
                    String region = holidayDto.getRegion();
                    List<HolidayDto.DateDto> dateDtos = holidayDto.getDates();
                    if (dateDtos != null) {
                        for (HolidayDto.DateDto dateDto : dateDtos) {
                            holidayEntities.add(new HolidayEntity(year, region, dateDto.getDate(), dateDto.getName(),
                                    dateDto.getNameCn(), dateDto.getNameEn(), dateDto.getType()));
//                            Log.i("terry", "日期: " + dateDto.getDate() + ", 名称: " + dateDto.getName() + ", 类型: " + dateDto.getType());
//                            Log.i("terry", "newHolidayEntities: " + holidayEntities.size());
                        }
                    }
                }
            } else {
                Log.e("terry", "请求失败: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return holidayEntities;
    }

    public static void setHolidayEntity(List<HolidayEntity> holidayEntityList, boolean isAppend) {
        if (!isAppend) {
            holidaySet.clear();
            transferWorkdaySet.clear();
        }
        for (HolidayEntity holidayEntity : holidayEntityList) {
            if ("public_holiday".equals(holidayEntity.getType())) {
                holidaySet.add(holidayEntity.getDate());
            } else if ("transfer_workday".equals(holidayEntity.getType())) {
                transferWorkdaySet.add(holidayEntity.getDate());
            }
        }
//        Log.i("terry", "transferWorkdaySet size: " + transferWorkdaySet.size());
//        for(String a: transferWorkdaySet){
//            Log.i("terry", a);
//        }
    }

    public static Set<String> getHolidaySet() {
        return UNMODIFIABLE_HOLIDAY_SET;
    }

    public static Set<String> getTransferWorkdaySet() {
        return UNMODIFIABLE_TRANSFER_WORKDAY_SET;
    }

    public static Set<Integer> getYearInitialized() {
        Set<Integer> yearInitialized = new HashSet<>();
        if (transferWorkdaySet != null) {
            for (String date : transferWorkdaySet) {
                int year = Integer.valueOf(date.substring(0, 4));
                if (!yearInitialized.contains(year)) {
                    yearInitialized.add(year);
                }
            }
        }
//        Log.i("terry", "transferWorkdaySet size:" + transferWorkdaySet.size());
        Log.i("terry", "yearInitialized:" + yearInitialized);
        return yearInitialized;
    }
}
