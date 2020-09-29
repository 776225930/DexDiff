package com.example.dexdiff;

/**
 * @author JiangHao
 * @date 2020/9/29
 * @describe
 */
public class BsPatchUtils {

    static {
        System.loadLibrary("bspatch_utils");
    }

    /**
     * 调用bsPatch 工具生成pahch
     *
     * @param oldApk
     * @param newApk 由oldApk和patchFile合成
     * @param patchFile
     */
    public static native int patch(String oldApk, String newApk, String patchFile);
}
