package com.lib.commonlib.utils;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.WindowManager;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author dengjun
 * @CreateDate 2016-10-20 16:01.
 * Description :
 */

public class PowerManagerUtils {
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mCpuLock;

    private PowerManager.WakeLock mWakeLock;



    public PowerManagerUtils(Context context) {
        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    public void acquireCpuLock() {

        if (mCpuLock == null) {
            mCpuLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "cpu lock");
            mCpuLock.setReferenceCounted(false);
        }
        if (!mCpuLock.isHeld()) {
            mCpuLock.acquire();
        }
    }

    public void releaseCpuLock() {
        if (mCpuLock != null && mCpuLock.isHeld()) {
            mCpuLock.release();
        }
    }

    public void acquireWakeLock() {

        if (mWakeLock == null) {
            mWakeLock = mPowerManager.newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, "cpu lock");
            mWakeLock.setReferenceCounted(false);
        }
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
    }

    public void releaseWakeLock() {

        if (mWakeLock != null && mWakeLock.isHeld()) {

            mWakeLock.release();
        }
    }


    public boolean isScreenOn() {
        if (android.os.Build.VERSION.SDK_INT >= 20) {
            return mPowerManager.isInteractive();
        }
        return mPowerManager.isScreenOn();
    }

    /**
     * 模拟用户点击
     */
    public void doUserActivity() {

        Method method = null;
        try {
            method = Class.forName("android.os.PowerManager").getMethod("userActivity", new Class[]{long.class, int.class, int.class});
            method.invoke(mPowerManager, SystemClock.uptimeMillis(), 0, 0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
