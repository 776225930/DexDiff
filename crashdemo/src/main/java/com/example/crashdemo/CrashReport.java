package com.example.crashdemo;

import android.content.Context;

import com.example.crashdemo.javacrash.CrashHandler;

import java.io.File;

/**
 * @author JiangHao
 * @date 2020/11/7
 * @describe
 */
class CrashReport {

    static {
        System.loadLibrary("bugly");
    }

    public static void init(Context context) {
        Context applicationContext = context.getApplicationContext();
//        CrashHandler.getInstance().init(applicationContext);
        File file = new File(applicationContext.getExternalCacheDir(), "native_crash");
        if (!file.exists()) {
            file.mkdirs();
        }
        initNativeCrash(file.getAbsolutePath());
    }

    public static native void initNativeCrash(String path);

    public static native void testNativeCrash();

    private static void testJavaCrash() {
        int i = 1 / 0;
    }


}
