package com.kirito.basic.security;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Log;

import com.jrummyapps.android.shell.CommandResult;
import com.jrummyapps.android.shell.Shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import de.robv.android.xposed.XposedBridge;

public class Protect {
    private static final int ALL_ALLOW = 0777;

    public static void APP_Secuity(Context context) {
        if (!checkApplication(context) || !isRawApk(context) /*|| isVpnConnected(context)*/ || isApkInDebug(context) || hasXposed(context)) {
            System.exit(0);
        }
    }


    private static boolean checkApplication(Context context) {
        return context.getPackageName().equals("com.mango.vpn") && context.getApplicationInfo().name.equals("com.mango.vpn.App");
    }

    private static boolean checkAppInstalled(Context context, String pkgName) {
        if (pkgName == null || pkgName.isEmpty()) {
            return false;
        }
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> info = packageManager.getInstalledPackages(0);
        Log.e("===========>", "apk " + info.size());
        if (info == null || info.isEmpty())
            return true;
        for (int i = 0; i < info.size(); i++) {
            if (pkgName.equals(info.get(i).packageName)) {
                Log.e("===========>", "安装了黄鸟");
                return true;
            }
        }
        return false;
    }

    private static boolean isVpnConnected(Context context) {
        //if(checkAppInstalled(context,"com.guoshi.httpcanary")){
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                        return true;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //}
        return false;
    }

    /**
     * 判断是否是原本的APK
     **/
    public static boolean isRawApk(Context context) {
        boolean isValidated = false;
        try {
            //得到签名
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            String signStr = encryptionMD5(sign.toByteArray());
            Log.e("============>",signStr+"   "+isApkInDebug(context));
            //将应用现在的签名MD5值和我们正确的MD5值对比
            if (isApkInDebug(context)) {
                return signStr.equals("ecbd24860692a34aab3c26defa39a753");
            } else {
                return signStr.equals("ecbd24860692a34aab3c26defa39a753");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return isValidated;
    }

    public static boolean hasXposed(Context context) {
/*        for (StackTraceElement ste : Thread.currentThread().getStackTrace())
            if (ste.getClassName().startsWith("de.robv.android.xposed"))
                return true;
        return false;*/
        int result = check1() + check2() + check3() + check4(context) + check5() + check6() + check7() + check8(context) + check9();
        return result != 0;
    }

    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }

    private static int check1() {
        return testClassLoader() || testUseClassDirectly() ? 1 : 0;
    }

    private static boolean testClassLoader() {
        try {
            ClassLoader.getSystemClassLoader()
                    .loadClass("de.robv.android.xposed.XposedHelpers");

            return true;
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }

    private static boolean testUseClassDirectly() {
        try {
            XposedBridge.log("fuck");
            return true;
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static int check2() {
        return checkContains("XposedBridge") ? 1 : 0;
    }

    private static int check3() {
        try {
            throw new Exception();
        } catch (Exception e) {
            StackTraceElement[] arrayOfStackTraceElement = e.getStackTrace();
            for (StackTraceElement s : arrayOfStackTraceElement) {
                if ("de.robv.android.xposed.XposedBridge".equals(s.getClassName())) {
                    return 1;
                }
            }
            return 0;
        }
    }

    private static int check4(Context context) {
        try {
            List<PackageInfo> list = context.getPackageManager().getInstalledPackages(0);
            for (PackageInfo info : list) {
                if ("de.robv.android.xposed.installer".equals(info.packageName)) {
                    return 1;
                }
                if ("io.va.exposed".equals(info.packageName)) {
                    return 1;
                }
            }
        } catch (Throwable ignored) {

        }
        return 0;
    }

    private static int check5() {
        try {
            Method method = Throwable.class.getDeclaredMethod("getStackTrace");
            return Modifier.isNative(method.getModifiers()) ? 1 : 0;
        } catch (NoSuchMethodException ignored) {
        }
        return 0;
    }

    private static int check6() {
        return System.getProperty("vxp") != null ? 1 : 0;
    }

    /**
     * @param paramString check string
     * @return whether check string is found in maps
     */
    public static boolean checkContains(String paramString) {
        try {
            HashSet<String> localObject = new HashSet<>();
            // 读取maps文件信息
            BufferedReader localBufferedReader =
                    new BufferedReader(new FileReader("/proc/" + android.os.Process.myPid() + "/maps"));
            while (true) {
                String str = localBufferedReader.readLine();
                if (str == null) {
                    break;
                }
                localObject.add(str.substring(str.lastIndexOf(" ") + 1));
            }
            //应用程序的链接库不可能是空，除非是高于7.0。。。
            if (localObject.isEmpty() && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                return true;
            }
            localBufferedReader.close();
            for (String aLocalObject : localObject) {
                if (aLocalObject.contains(paramString)) {
                    Log.i("check", aLocalObject);
                    return true;
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static int check7() {
        CommandResult commandResult = Shell.run("ls /system/lib");
        return commandResult.isSuccessful() ? commandResult.getStdout().contains("xposed") ? 1 : 0 : 0;
    }

    private static int check8(Context context) {
        try {
            if (!new File(context.getFilesDir().getAbsolutePath() + "/checkman").exists()) {
                InputStream inputStream = context.getAssets().open("checkman");
                OutputStream outputStream = context.openFileOutput("checkman", MODE_PRIVATE);
                int bit;
                while ((bit = inputStream.read()) != -1) {
                    outputStream.write(bit);
                }
            }
            setFilePermissions(context.getFilesDir(), ALL_ALLOW, -1, -1);
            setFilePermissions(context.getFilesDir().getAbsolutePath() + "/checkman", ALL_ALLOW, -1, -1);
        } catch (IOException ignored) {

        }

        CommandResult commandResult = Shell.run(context.getFilesDir().getAbsolutePath() + "/checkman " + android.os.Process.myPid());
        return commandResult.isSuccessful() ? 1 : 0;
    }

    static boolean setFilePermissions(File file, int chmod, int uid, int gid) {
        if (file != null) {
            Class<?> fileUtils;
            try {
                fileUtils = Class.forName("android.os.FileUtils");
                Method setPermissions = fileUtils.getMethod("setPermissions", File.class, int.class, int.class, int.class);
                int result = (Integer) setPermissions.invoke(null, file, chmod, uid, gid);

                return result == 0;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        } else {
            return false;
        }
    }

    static boolean setFilePermissions(String file, int chmod, int uid, int gid) {
        return setFilePermissions(new File(file), chmod, uid, gid);
    }

    private static int check9() {
        String env = System.getenv("CLASSPATH");
        if (env != null && !env.isEmpty()) {
            return env.contains("XposedBridge") ? 1 : 0;
        } else {
            return 0;
        }
    }


}
