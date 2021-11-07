package com.kirshi.protocol.jni;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.security.MessageDigest;
import java.util.Locale;

/**
 * Copyright (c) 2021
 *
 * @Project:Freya
 * @Author:Finger
 * @FileName:HostConf.java
 * @LastModified:2021-04-12T22:23:56.603+08:00
 */

public class HostConf {
    static {
        try {
            System.loadLibrary("HostConf");
        } catch (UnsatisfiedLinkError ignored) {
            System.exit(1);
        }
    }

    public static native String rc4(String str);

    public static native String getHost();

    /**
     * AES加密
     *
     * @param context
     * @param str
     * @return
     */
    public static native String encode(Object context, String str);


    /**
     * AES 解密
     *
     * @param context
     * @param str
     * @return UNSIGNATURE ： sign not pass .
     */
    public static native String decode(Object context, String str);


    public static int getSignature(Context context) {

        try {
            PackageInfo packageInfo =
                    context.
                            getPackageManager().
                            getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return sign.hashCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getSha1Value(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
            }
            String result = hexString.toString();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
