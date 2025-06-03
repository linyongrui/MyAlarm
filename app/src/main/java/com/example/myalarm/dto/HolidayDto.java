package com.example.myalarm.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HolidayDto {
    private int year;
    private String region;
    @SerializedName("dates")
    private List<DateDto> dates;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<DateDto> getDates() {
        return dates;
    }

    public void setDates(List<DateDto> dates) {
        this.dates = dates;
    }

    public static class DateDto {
        private String date;
        private String name;
        @SerializedName("name_cn")
        private String nameCn;
        @SerializedName("name_en")
        private String nameEn;
        private String type;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNameCn() {
            return nameCn;
        }

        public void setNameCn(String nameCn) {
            this.nameCn = nameCn;
        }

        public String getNameEn() {
            return nameEn;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
