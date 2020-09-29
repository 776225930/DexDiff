#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_dexdiff_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello World from C++";
    return env->NewStringUTF(hello.c_str());
}
//导入方法 在某个地方有实现该方法
extern "C" {//兼容c++调用c
extern int executePatch(int argc, char *argv[]);
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_dexdiff_BsPatchUtils_patch(JNIEnv *env, jclass clazz, jstring old_apk,
                                            jstring new_apk, jstring patch_file) {
    int args = 4;
    char *argv[args];

    argv[0] = "bspatch";
    //java 字符串转成 c++的字符串
    argv[1] = (char *) (env->GetStringUTFChars(old_apk, 0));
    argv[2] = (char *) (env->GetStringUTFChars(new_apk, 0));
    argv[3] = (char *) (env->GetStringUTFChars(patch_file, 0));
    //
    int result = executePatch(args, argv);
    //释放
    env->ReleaseStringUTFChars(old_apk, argv[1]);
    env->ReleaseStringUTFChars(new_apk, argv[2]);
    env->ReleaseStringUTFChars(patch_file, argv[3]);
    __android_log_print(ANDROID_LOG_ERROR, "diff ", "--%s--%s--%s--%s", argv[0], argv[1], argv[2],
                        argv[3]);
    return result;
}