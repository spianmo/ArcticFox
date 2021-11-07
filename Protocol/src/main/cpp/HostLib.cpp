#include <jni.h>
#include "aes.h"
#include "checksignature.h"
#include "check_emulator.h"
#include "md5.h"
#include <sys/ptrace.h>
#include <android/log.h>
#include <cstring>
#include <iosfwd>
#include <assert.h>
#include "HostLib.h"

#define CBC 1
#define ECB 1


char *UNSIGNATURE = "UNSIGNATURE";

jstring charToJstring(JNIEnv *env, char *src) {
    jsize len = strlen(src);
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("UTF-8");
    jmethodID mid = env->GetMethodID(clsstring, "<init>",
                                     "([BLjava/lang/String;)V");
    jbyteArray barr = env->NewByteArray(len);
    env->SetByteArrayRegion(barr, 0, len, (jbyte *) src);

    return (jstring) env->NewObject(clsstring, mid, barr, strencode);
}

char *getKey() {
    char *encode_str = "MTIyMzMzNDQ0NFRyYWNrZXI";
    return reinterpret_cast<char *>(b64_decode(encode_str, strlen(encode_str)));
}

JNIEXPORT jstring JNICALL encode(JNIEnv *env, jobject instance, jobject context, jstring str_) {

    //先进行apk被 二次打包的校验
    if (check_signature(env, context) != 1 || check_is_emulator(env) != 1) {
        char *str = UNSIGNATURE;
        return charToJstring(env, str);
    }

    uint8_t *AES_KEY = (uint8_t *) getKey();
    const char *in = env->GetStringUTFChars(str_, JNI_FALSE);
    char *baseResult = AES_128_ECB_PKCS5Padding_Encrypt(in, AES_KEY);
    env->ReleaseStringUTFChars(str_, in);
//    return env->NewStringUTF(env, baseResult);
    jstring result = env->NewStringUTF(baseResult);
    free(baseResult);
    free(AES_KEY);
    return result;
}


string gen_superkey(JNIEnv *env) {

    jobject context = getGlobalContext(env);

    jclass native_clazz = env->GetObjectClass(context);

    jmethodID methodID_func = env->GetMethodID(native_clazz, "getPackageManager",
                                               "()Landroid/content/pm/PackageManager;");

    jobject package_manager = env->CallObjectMethod(context, methodID_func);

    jclass pm_clazz = env->GetObjectClass(package_manager);

    jmethodID methodID_pm = env->GetMethodID(pm_clazz,
                                             "getPackageInfo",
                                             "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");


    jmethodID methodID_packagename = env->GetMethodID(native_clazz, "getPackageName",
                                                      "()Ljava/lang/String;");
    auto name_str = (jstring) env->CallObjectMethod(context, methodID_packagename);

    const char *packageName = env->GetStringUTFChars(name_str, nullptr);

    LOGI("包名 %s", packageName);

    jobject package_info = env->CallObjectMethod(package_manager,
                                                 methodID_pm, name_str, 64);

    jclass pi_clazz = env->GetObjectClass(package_info);

    jfieldID fieldID_applicationInfo = env->GetFieldID(pi_clazz, "applicationInfo",
                                                       "Landroid/content/pm/ApplicationInfo;");

    jobject applicationInfo = env->GetObjectField(package_info, fieldID_applicationInfo);

    jclass applicationInfo_clazz = env->GetObjectClass(applicationInfo);

    jfieldID fieldID_sourceDir = env->GetFieldID(applicationInfo_clazz, "sourceDir",
                                                 "Ljava/lang/String;");

    auto sourceDir = (jstring) env->GetObjectField(applicationInfo, fieldID_sourceDir);
    const char *sourceDir_Path = env->GetStringUTFChars(sourceDir, nullptr);

    LOGI("APK路径 %s", sourceDir_Path);

    MD5 md5;
    md5.reset();
    ifstream stream = ifstream(sourceDir_Path);
    md5.update(stream);
    LOGI("APK MD5 %s", md5.toString().c_str());
    return md5.toString();

}


JNIEXPORT jstring JNICALL decode(JNIEnv *env, jobject instance, jobject context, jstring str_) {


    //先进行apk被 二次打包的校验
    if (check_signature(env, context) != 1 || check_is_emulator(env) != 1) {
        char *str = UNSIGNATURE;
        return charToJstring(env, str);
    }

    uint8_t *AES_KEY = (uint8_t *) getKey();
    const char *str = env->GetStringUTFChars(str_, JNI_FALSE);
    char *desResult = AES_128_ECB_PKCS5Padding_Decrypt(str, AES_KEY);
    env->ReleaseStringUTFChars(str_, str);
//    return env->NewStringUTF(env, desResult);
    //不用系统自带的方法NewStringUTF是因为如果desResult是乱码,会抛出异常
//    return charToJstring(env,desResult);
    jstring result = charToJstring(env, desResult);
    free(desResult);
    free(AES_KEY);
    return result;
}

JNIEXPORT jstring JNICALL getHost(JNIEnv *env, jclass) {
    string host = "https://login.mangoml.com";
    return env->NewStringUTF(host.c_str());
}


jstring rc4(JNIEnv *env, jstring str) {
    string input = jstring2str(env, str);
    return env->NewStringUTF(_rc4(input).c_str());
}

string _rc4(const string &input) {
    const char *key = superkey.c_str();
    LOGI("RC4密钥 %s", key);

    unsigned char sbox[256] = {0};
    unsigned char *buffer;
    buffer = (unsigned char *) input.c_str();
    rc4_init(sbox, (unsigned char *) key, strlen(key));
    rc4_crypt(sbox, (unsigned char *) buffer, strlen(reinterpret_cast<const char *>(buffer)));
    LOGI("RC4计算结果 %s", reinterpret_cast<const char *>(buffer));
    return string(reinterpret_cast<const char *>(buffer));
}

void jheap_crash(JNIEnv *env) {
    jclass systemClass = env->FindClass("java/lang/System");
    jmethodID exitMethodId = env->GetStaticMethodID(systemClass, "exit", "(I)V");
    env->CallStaticVoidMethod(systemClass, exitMethodId, 1);
}


void verify(JNIEnv *env) {
    if (check_signature(env, getGlobalContext(env)) != 1) {
        LOGE("签名HashCode验证失败");
        show(env, "签名HashCode验证失败");
    }
    HTTP::Client http("http://login.mangoml.com");
    if (auto res = http.Get("/app_api/sig.php?version=1.4")) {
        if (res->status == 200) {
            LOGI("校验 %s", res->body.c_str());
            if (res->body == superkey) {
                _rc4(res->body);
            } else {
                LOGE("MD5验证失败");
                //show(env, "MD5验证失败");
                jheap_crash(env);
            }
        } else{
            jheap_crash(env);
        }
    } else {
        auto err = res.error();
        jheap_crash(env);
        LOGE("错误 %u", err);
    }
}




