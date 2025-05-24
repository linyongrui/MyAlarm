package com.example.myalarm.alarmtype;

public abstract class CustomTypeAlarm {
    private String name;
    private String type;

    public abstract String getRepeatDesc();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
