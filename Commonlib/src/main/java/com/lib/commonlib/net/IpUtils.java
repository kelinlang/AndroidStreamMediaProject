package com.lib.commonlib.net;

import android.text.TextUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by dengjun on 2017/11/29.
 */

public class IpUtils {
    public static String getInetAddress(String host) {//通过域名获取IP
        if (checkIsIpv4(host) || checkIsIpv6(host)) {
            return host;
        }

        String IPAddress = null;
        InetAddress ReturnStr = null;
        try {
            ReturnStr = InetAddress.getByName(host);
            IPAddress = ReturnStr.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }

        return IPAddress;
    }

    //验证是否是ipv4
    public static boolean checkIsIpv4(String ip) {
        String[] ss = ip.split("\\.");
        if (ss.length != 4) {
            return false;
        }

        for (String s : ss) {
            if (!TextUtils.isDigitsOnly(s)) {
                return false;
            }
        }
        return true;
    }

    //验证是否是ipv6
    private static boolean checkIsIpv6(String ip) {
        String[] ss = ip.split(":");
        return ss.length > 4;
    }
}
