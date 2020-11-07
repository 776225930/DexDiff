package com.example.crashdemo;

import android.app.Application;

import com.example.crashdemo.javacrash.CrashHandler;

/**
 * @author JiangHao
 * @date 2020/11/7
 * @describe
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.init(this);
    }
}
