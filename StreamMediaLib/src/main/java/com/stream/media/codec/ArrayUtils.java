package com.stream.media.codec;

/**
 * Created by dengjun on 2018/6/28.
 */

public class ArrayUtils {
    public static boolean isArrayContain(int[] src, int target) {
        for (int color : src) {
            if (color == target) {
                return true;
            }
        }
        return false;
    }
}
