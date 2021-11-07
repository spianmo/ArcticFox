//
// Created by Finger on 12/6/2020.
//

#ifndef THE_WITCH_NATIVE_LIB_H
#define THE_WITCH_NATIVE_LIB_H

#include <jni.h>
#include <string>
#include <android/log.h>
#include "md5.h"
#include "httplib.h"
#include <dlfcn.h>
#include <sys/mman.h>

#endif //THE_WITCH_NATIVE_LIB_H

#define LOG_TAG  "=======NATIVE========>"

#define LOG_SWITCH 0

#if(LOG_SWITCH == 1)

#define LOGV(format, ...)  __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, format, ##__VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG ,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG ,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,LOG_TAG ,__VA_ARGS__)

#else

#define LOGV(...) NULL
#define LOGD(...) NULL
#define LOGI(...) NULL
#define LOGW(...) NULL
#define LOGE(...) NULL
#define LOGF(...) NULL

#endif
#define JNIREG_CLASS "com/kirshi/protocol/jni/HostConf"

using namespace std;

std::string jstring2str(JNIEnv *env, jstring jstr) {
    char *rtn = nullptr;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("GB2312");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    auto barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    std::string stemp(rtn);
    free(rtn);
    return stemp;
}

jobject getGlobalContext(JNIEnv *env) {
    jclass activityThread = env->FindClass("android/app/ActivityThread");
    jmethodID currentActivityThread = env->GetStaticMethodID(activityThread,
                                                             "currentActivityThread",
                                                             "()Landroid/app/ActivityThread;");
    jobject at = env->CallStaticObjectMethod(activityThread, currentActivityThread);
    jmethodID getApplication = env->GetMethodID(activityThread, "getApplication",
                                                "()Landroid/app/Application;");
    jobject context = env->CallObjectMethod(at, getApplication);
    return context;
}

jclass clazz = NULL;

static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

void show(JNIEnv *env, string str) {
    jclass jc_Toast = env->FindClass("android/widget/Toast");
    jmethodID jm_makeText = env->GetStaticMethodID(jc_Toast, "makeText",
                                                   "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;");
    jobject jo_Toast = env->CallStaticObjectMethod(jc_Toast, jm_makeText, getGlobalContext(env),
                                                   env->NewStringUTF(str.c_str()), 0);
    jmethodID jm_Show = env->GetMethodID(jc_Toast, "show", "()V");
    env->CallVoidMethod(jo_Toast, jm_Show);
}


void rc4_init(unsigned char *s, unsigned char *key, unsigned long Len) {
    int i = 0, j = 0;
    char k[256] = {0};
    unsigned char tmp = 0;
    for (i = 0; i < 256; i++) {
        s[i] = i;
        k[i] = key[i % Len];
    }
    for (i = 0; i < 256; i++) {
        j = (j + s[i] + k[i]) % 256;
        tmp = s[i];
        s[i] = s[j];
        s[j] = tmp;
    }
}

void rc4_crypt(unsigned char *s, unsigned char *Data, unsigned long Len) {
    int i = 0, j = 0, t = 0;
    unsigned long k = 0;
    unsigned char tmp;
    for (k = 0; k < Len; k++) {
        i = (i + 1) % 256;
        j = (j + s[i]) % 256;
        tmp = s[i];
        s[i] = s[j];
        s[j] = tmp;
        t = (s[i] + s[j]) % 256;
        Data[k] ^= s[t];
    }
}

void jheap_crash(JNIEnv *env);

void verify(JNIEnv *env);

string _rc4(const string &input);

jstring rc4(JNIEnv *env, jstring str);

jstring decode(JNIEnv *env, jobject instance, jobject context, jstring str_);

jstring encode(JNIEnv *env, jobject instance, jobject context, jstring str_);

jstring getHost(JNIEnv *env, jclass);


string gen_superkey(JNIEnv *env);

static JNINativeMethod gMethods[] = {
        {"rc4",     "(Ljava/lang/String;)Ljava/lang/String;",                   (jstring *) rc4},
        //{"checkSignature", "(Ljava/lang/Object;)I",                                    (void *) check_jni},
        {"decode",  "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;", (void *) decode},
        {"encode",  "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;", (void *) encode},
        {"getHost", "()Ljava/lang/String;",                                     (void *) getHost},
};

#define ARRAY_LENGTH(array) (sizeof(array)/sizeof(array[0]))

static int registerNatives(JNIEnv *env) {
    if (!registerNativeMethods(env, JNIREG_CLASS, gMethods,
                               ARRAY_LENGTH(gMethods)))
        return JNI_FALSE;

    return JNI_TRUE;
}

string superkey;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    assert(env != nullptr);
    if (!registerNatives(env)) {
        return -1;
    }
    superkey = gen_superkey(env);
    verify(env);
    LOGI("pid = %d, tid = %d\n", getpid(), gettid());
    return JNI_VERSION_1_4;
}

JNIEnv *env;

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    env = NULL;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return;
    }
    env->UnregisterNatives(clazz);
}
