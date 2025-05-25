package com.example.myalarm;


import android.app.Application;

import com.example.myalarm.data.DatabaseClient;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化数据库
        DatabaseClient.initialize(this);
        // 其他初始化...
    }
}
