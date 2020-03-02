package com.lib.commonlib.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
/*import android.support.v7.content.ContextCompat;*/

/**
 * Created by dengjun on 2018/4/24.
 */

public class PermssionUtils {
    public static boolean checkPermision(Context context, String perssion) {
        PackageManager pm = context.getPackageManager();
        String packagename = context.getPackageName();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(perssion, packagename));
        return permission;
    }

    /**
     * check permission is need ?
     *
     * @return true: need permission  false: don't need permission
     */
    public static boolean isNeedAddPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isHadRecordPermision(Context context) {
        PackageManager pm = context.getPackageManager();
        String packagename = context.getPackageName();
        boolean permission = (
                PackageManager.PERMISSION_GRANTED == pm.checkPermission(Manifest.permission.RECORD_AUDIO, packagename));
        return permission;
    }
}
