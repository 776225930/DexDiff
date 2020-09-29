package com.example.dexdiff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("bspatch_utils");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
//        TextView tv = findViewById(R.id.textView);
//        tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public void diff_update(View view) {
        Log.e(TAG, "diff_update: ");
        //创建/storage/emulated/0/Android/data/pkg/files/apk目录,私有文件夹不需要权限
        File newFile = new File(getExternalFilesDir("apk"), "app.apk");
        File patchFile = new File(getExternalFilesDir("apk"), "patch.apk");
        int result = BsPatchUtils.patch(getApplicationInfo().sourceDir, newFile.getAbsolutePath(), patchFile.getAbsolutePath());
        if (result == 0) {
            install(newFile);
        }

    }

    private void install(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本
            Uri apkUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    /**
     * Android系统分为内部存储和外部存储，内部存储是手机系统自带的存储，
     * 一般空间都比较小，外部存储一般是SD卡的存储，空间一般都比较大，
     * 但不一定可用或者剩余空间可能不足。一般我们存储内容都会放在外部存储空间里
     * 1.先判断SD卡是否可用，可用时优先使用SD卡的存储，不可用时用内部存储
     * 2.存储在SD卡上时，可以在SD卡上新建任意一个目录存放，也可以存放在应用程序内部文件夹，
     * 区别是在SD卡的任意目录存放时内容不会随应用程序的卸载而消失，
     * 而在应用程序内部的内容会随应用程序卸载消失。
     * 3.一般缓存文件放在应用程序内部，用户主动保存的文件放在SD卡上的文件夹里。
     * 如果在SD卡上任意新建目录存放所有数据，用户卸载时会残存大量文件，招致用户反感。
     *
     * @param view
     */
    public void print_pathInfo(View view) {
        Log.e(TAG, "====内部存储====");
        // 应用程序目录($applicationDir) /data
        Log.e(TAG, "print_pathInfo: Environment.getDataDirectory()" + Environment.getDataDirectory());
        // /data/user/0/com.example.dexdiff/cache
        Log.e(TAG, "print_pathInfo: getCacheDir() :" + getCacheDir());
        // /data/user/0/com.example.dexdiff/files
        Log.e(TAG, "print_pathInfo: getFilesDir() :" + getFilesDir());
        // /data/user/0/com.example.dexdiff/files
        Log.e(TAG, "print_pathInfo: getFileStreamPath(\"\") :" + getFileStreamPath(""));
        // /data/user/0/com.example.dexdiff/files/test
        Log.e(TAG, "print_pathInfo: getFileStreamPath(\"test\") :" + getFileStreamPath("test"));

        Log.e(TAG, "====外部存储====");
        // /storage/emulated/0
        Log.e(TAG, "print_pathInfo: Environment.getExternalStorageDirectory() :" + Environment.getExternalStorageDirectory());
        // /storage/emulated/0/test
        Log.e(TAG, "print_pathInfo: Environment.getExternalStoragePublicDirectory() :" + Environment.getExternalStoragePublicDirectory("test"));
        // /storage/emulated/0/Android/data/com.example.dexdiff/cache
        Log.e(TAG, "print_pathInfo: Environment.getExternalStoragePublicDirectory() :" + getExternalCacheDir());
        // /storage/emulated/0/Android/data/com.example.dexdiff/files
        Log.e(TAG, "print_pathInfo: getExternalFilesDir() :" + getExternalFilesDir(""));
        // /storage/emulated/0/Android/data/com.example.dexdiff/files/test
        Log.e(TAG, "print_pathInfo: getExternalFilesDir() :" + getExternalFilesDir("test"));
        // /storage/emulated/0/Android/data/com.example.dexdiff/files/Pictures
        Log.e(TAG, "print_pathInfo: getExternalFilesDir() :" + getExternalFilesDir(Environment.DIRECTORY_PICTURES));

        Log.e(TAG, "====公共存储目录====");
        // /storage/emulated/0
        Log.e(TAG, "print_pathInfo: Environment.getExternalStorageDirectory() :" + Environment.getExternalStorageDirectory());
        // /storage/emulated/0
        Log.e(TAG, "print_pathInfo: Environment.getExternalStoragePublicDirectory(\"\") :" + Environment.getExternalStoragePublicDirectory(""));
        // /storage/emulated/0/Pictures
        Log.e(TAG, "print_pathInfo: " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));


    }
}
