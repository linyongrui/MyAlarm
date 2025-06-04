package com.example.myalarm.util;

import android.util.Log;

import com.example.myalarm.dto.HolidayDto;
import com.example.myalarm.entity.HolidayEntity;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Response;

public class HolidayUtils {

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
                    for (HolidayDto.DateDto dateDto : dateDtos) {
                        holidayEntities.add(new HolidayEntity(year, region, dateDto.getDate(), dateDto.getName(),
                                dateDto.getNameCn(), dateDto.getNameEn(), dateDto.getType()));
                        Log.i("terry", "日期: " + dateDto.getDate() + ", 名称: " + dateDto.getName() + ", 类型: " + dateDto.getType());
                        Log.i("terry", "newHolidayEntities: " + holidayEntities.size());
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

    public static Set[] getHolidayEntitySet(List<HolidayEntity> holidayEntityList) {
        Set holidatSet = new HashSet();
        Set transferWorkdaySet = new HashSet<>();
        for (HolidayEntity holidayEntity : holidayEntityList) {
            if ("public_holiday".equals(holidayEntity.getType())) {
                holidatSet.add(holidayEntity.getDate());
            } else if ("transfer_workday".equals(holidayEntity.getType())) {
                transferWorkdaySet.add(holidayEntity.getDate());
            }
        }
        return new Set[]{holidatSet, transferWorkdaySet};
    }
}
