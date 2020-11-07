package com.example.crashdemo.javacrash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author JiangHao
 * @date 2020/11/7
 * @describe Java中的Thread定义了一个接口： UncaughtExceptionHandler ；
 * 用于处理未捕获的异常导致线程的终止（注意：catch了的是捕获不到的），当我们的应用crash的时候，就
 * 会走 UncaughtExceptionHandler.uncaughtException ，在该方法中可以获取到异常的信息，我们通
 * 过 Thread.setDefaultUncaughtExceptionHandler 该方法来设置线程的默认异常处理器，我们可以
 * 将异常信息保存到本地或者是上传到服务器，方便我们快速的定位问题。
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashHandler.class.getSimpleName();
    private static final String FILE_NAME_SUFFIX = ".log";
    private static Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private static Context mContext;

    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        return SingletonHolder.sInstance;
    }

    public static class SingletonHolder {
        private static final CrashHandler sInstance = new CrashHandler();
    }

    public void init(@NonNull Context context) {
        //默认为：RuntimeInit#KillApplicationHandler
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context;
    }

    /**
     * 当程序中有未被捕获的异常，系统将会调用这个方法
     *
     * @param thread    出现异常的线程
     * @param throwable 得到异常信息
     */
    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        try {
            //自行处理：保存本地
            Log.e(TAG, "uncaughtException: " + throwable.getMessage());
            File file = dealException(thread, throwable);
            //上传服务器 ...
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //交给系统默认程序处理
            mDefaultCrashHandler.uncaughtException(thread, throwable);
        }
    }

    /*** 导出异常信息到SD卡
     * @param t
     * @param e
     * @return
     */
    private File dealException(Thread t, Throwable e) throws Exception {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        File f = new File(mContext.getExternalCacheDir().getAbsoluteFile(), "crash_info");
        if (!f.exists()) {
            f.mkdirs();
        }
        File crashFile = new File(f, time + FILE_NAME_SUFFIX);
        File file = new File(f,File.separator + time + FILE_NAME_SUFFIX);
        //往文件中写入数据
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        pw.println(time);
        pw.println("Thread: " + t.getName());
        pw.println(getPhoneInfo());
        e.printStackTrace(pw);
        // 写入crash堆栈
        pw.close();
        return file;
    }

    private String getPhoneInfo() throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        StringBuilder sb = new StringBuilder();
        //App版本 sb.append("App Version: ");
        sb.append(pi.versionName);
        sb.append("_");
        sb.append(pi.versionCode + "\n");
        //Android版本号
        sb.append("OS Version: ");
        sb.append(Build.VERSION.RELEASE);
        sb.append("_");
        sb.append(Build.VERSION.SDK_INT + "\n");
        //手机制造商
        sb.append("Vendor: ");
        sb.append(Build.MANUFACTURER + "\n");
        // 手机型号
        sb.append("Model: ");
        sb.append(Build.MODEL + "\n");
        //CPU架构
        sb.append("CPU: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sb.append(Arrays.toString(Build.SUPPORTED_ABIS));
        } else {
            sb.append(Build.CPU_ABI);
        }
        return sb.toString();
    }

}
