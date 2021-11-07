package com.kirito.basic.net;

import android.util.Base64;

import com.google.gson.Gson;
import com.kirshi.protocol.jni.HostConf;
import com.tencent.mmkv.MMKV;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;

/**
 * @author Finger
 */
public class NetInterface {

    public static String baseURL = HostConf.getHost();

    public static String baseURLEx = MMKV.defaultMMKV().decodeString(decode("YmFzZVVSTA=="),HostConf.getHost());

    public static String buildParams(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    public static String buildDefaultParams() {
        return buildParams(new LinkedHashMap<String, String>() {
            {
/*                put("app_key", appKey);
                if (App.isLogin) {
                    put("username", App.currentUserInfo.getAccount());
                    put("password", App.currentUserInfo.getPassword());
                }*/
            }
        });
    }

    public static void httpGet(String path, StringCallback callback) {
        OkHttpUtils
                .get()
                .url(baseURLEx + path + buildDefaultParams())
                .build()
                .execute(callback);
    }

    public static void httpGetBase(String url, StringCallback callback) {
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(callback);
    }

    public static void httpGet(String path, Map<String, String> params, StringCallback callback) {
        OkHttpUtils
                .get()
                .url(baseURL + path)
                .params(params)
                .build()
                .execute(callback);
    }

    public static void httpPost(String path, Map<String, String> params, StringCallback callback) {
        OkHttpUtils
                .post()
                .url(baseURLEx + path)
                .params(params)
                .build()
                .execute(callback);
    }

    public static void httpPost(String path, Object entity, StringCallback callback) {
        OkHttpUtils
                .postString()
                .url(baseURL +path)
                .content(new Gson().toJson(entity))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(callback);
    }

    public static void httpPost(String path, File file, StringCallback callback){
        OkHttpUtils
                .postFile()
                .url(baseURL + path)
                .file(file)
                .build()
                .execute(callback);
    }

    public static void httpPost(String path, String fileKey, Map<String, File> files, StringCallback callback, Map<String, String> params) {
        OkHttpUtils.post()//
                .files(fileKey, files)
                .url(baseURL + path)
                .params(params)
                .build()
                .execute(callback);

    }

    public static String decode(String text) {
        return new String(Base64.decode(text.getBytes(), Base64.DEFAULT));
    }

    public static String encode(String text) {
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
    }

    public static String subString(String str, String strStart, String strEnd) {

        /* 找出指定的2个字符在 该字符串里面的 位置 */
        int strStartIndex = str.indexOf(strStart);
        int strEndIndex = str.indexOf(strEnd);

        /* index 为负数 即表示该字符串中 没有该字符 */
        if (strStartIndex < 0) {
            return "字符串 :---->" + str + "<---- 中不存在 " + strStart + ", 无法截取目标字符串";
        }
        if (strEndIndex < 0) {
            return "字符串 :---->" + str + "<---- 中不存在 " + strEnd + ", 无法截取目标字符串";
        }
        /* 开始截取 */
        return str.substring(strStartIndex, strEndIndex).substring(strStart.length());
    }
}
