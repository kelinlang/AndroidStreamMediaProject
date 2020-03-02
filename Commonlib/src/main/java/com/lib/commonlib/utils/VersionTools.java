package com.lib.commonlib.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;


import java.io.File;


public class VersionTools {

    public static final String FILE_NAME = "file://";
    public static final String APPLICATION_NAME = "application/vnd.android.package-archive";

    /**
     * Gets app version.
     *
     * @param context the context
     * @return the app version
     */
    public static String getAppVersion(Context context) {
        PackageInfo packageInfo = null;
        String versionName = "";
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * Gets app version code.
     *
     * @param context the context
     * @return the app version code
     */
    public static int getAppVersionCode(Context context) {
        PackageInfo packageInfo = null;
        int versionName = 0;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionName = packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getAppVersionCode(Context context, String packageName) {
        PackageInfo packageInfo = null;
        int versionName = 0;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
            versionName = packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * Install app.
     *
     * @param context the context
     * @param file    the file
     *
     *  这个"android.intent.action.VIEW.HIDE" action需要修改系统源码，非原生自带。
     */
    public static void installApp(Context context, File file) {
        try
        {
            Intent intent = new Intent("android.intent.action.VIEW.HIDE");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse(FILE_NAME + file.toString()), APPLICATION_NAME);
            context.startActivity(intent);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    //判断当前应用是否是debug状态
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}
