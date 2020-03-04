package com.lib.commonlib.utils;

import android.content.Context;

/**
 * Created by dengjun on 2019/2/18.
 */

public class SoLoadUtils {
    public static void loadLibrary(Context context, String soName) {
        if (context == null) {
            MLog.e("loadLibrary context is null");
            return;
        }
        String path = context.getApplicationInfo().dataDir + "/lib";
        try {
            System.loadLibrary(soName);
        } catch (UnsatisfiedLinkError e) {
            try {
                System.load(path + "/lib" + soName + ".so");
            } catch (Exception e1) {
                e.printStackTrace();
            }
        }
    }

    public static boolean loadLibraryR(Context context, String soName) {
        boolean loadFlag = false;
        if (context != null) {
            String path = context.getApplicationInfo().dataDir + "/lib";
            try {
                System.loadLibrary(soName);
                loadFlag = true;
            } catch (UnsatisfiedLinkError e) {
                try {
                    System.load(path + "/lib" + soName + ".so");
                    loadFlag = true;
                } catch (Exception e1) {
                }
            }
        }
        return loadFlag;
    }
}
