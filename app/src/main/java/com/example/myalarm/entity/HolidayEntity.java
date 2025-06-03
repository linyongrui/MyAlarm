package com.example.myalarm.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "holidayEntity")
public class HolidayEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private int year;
    private String region;
    private String date;
    private String name;
    private String nameCn;
    private String nameEn;
    private String type;

    public HolidayEntity(int year, String region, String date, String name, String nameCn, String nameEn, String type) {
        this.year = year;
        this.region = region;
        this.date = date;
        this.name = name;
        this.nameCn = nameCn;
        this.nameEn = nameEn;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
