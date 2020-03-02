package com.lib.commonlib.utils;

/**
 * Created by dengjun on 2019/2/28.
 */

public class ThreadUtils {
    public static  void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
