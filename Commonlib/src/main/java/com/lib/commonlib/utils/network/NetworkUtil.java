package com.lib.commonlib.utils.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.lib.commonlib.utils.MLog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkUtil {
    private static final String TAG = "NetworkUtil";

    // 上网类型
    /**
     * 没有网络
     */
    public static final byte NETWORK_TYPE_INVALID = 0x0;
    /**
     * wifi网络
     */
    public static final byte NETWORK_TYPE_WIFI = 0x1;
    /**
     * 3G和3G以上网络，或统称为快速网络
     */
    public static final byte NETWORK_TYPE_3G = 0x2;
    /**
     * 2G网络
     */
    public static final byte NETWORK_TYPE_2G = 0x3;
    public static final byte XIAO_MI_SHARE_PC_NETWORK = 9;
    public static final byte NETWORK_TYPE_4G = 0x4;

    /**
     * 获取网络状态，wifi,3g,2g,无网络。
     *
     * @param context 上下文
     * @return byte 网络状态 {@link #NETWORK_TYPE_WIFI}, {@link #NETWORK_TYPE_3G},
     * {@link #NETWORK_TYPE_2G}, {@link #NETWORK_TYPE_INVALID}
     */
    public static byte getNetWorkType(Context context) {
//        return NETWORK_TYPE_INVALID;

        byte mNetWorkType = NETWORK_TYPE_INVALID;
        if (context == null) {
            return mNetWorkType;
        }

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                int nType = networkInfo.getType();
                MLog.i("NetworkUtil", nType + "");
                if (nType == ConnectivityManager.TYPE_WIFI) {
                    mNetWorkType = NETWORK_TYPE_WIFI;
                } else if (nType == ConnectivityManager.TYPE_MOBILE) {
                    // String proxyHost =
                    // android.net.Proxy.getDefaultHost();//TextUtils.isEmpty(proxyHost)=false为wap网络
//					mNetWorkType = (isFastMobileNetwork(context) ? NETWORK_TYPE_3G
//							: NETWORK_TYPE_2G);
                    int subType = networkInfo.getSubtype();
                    if (subType == TelephonyManager.NETWORK_TYPE_CDMA
                            || subType == TelephonyManager.NETWORK_TYPE_GPRS
                            || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                        mNetWorkType = NETWORK_TYPE_2G;
                    } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
                            || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                            || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
                            || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                            || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                        mNetWorkType = NETWORK_TYPE_3G;
                    } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
                        mNetWorkType = NETWORK_TYPE_4G;
                    }
                } else if (nType == XIAO_MI_SHARE_PC_NETWORK) {
                    mNetWorkType = NETWORK_TYPE_WIFI;
                }

            } else {
                mNetWorkType = NETWORK_TYPE_INVALID;

            }
        }

        return mNetWorkType;
    }

    /**
     * 获取网络类型
     *
     * @param context 上下文
     * @return 2G 3G 4G WIFI
     */
    public static String getNetworkType(Context context) {
//        return "";

        String strNetworkType = "";

        if (context == null) {
            MLog.e(TAG, "getNetworkType,context is null");
            return strNetworkType;
        }

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    strNetworkType = "WIFI";
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    String _strSubTypeName = networkInfo.getSubtypeName();
                    // TD-SCDMA   networkType is 17
                    int networkType = networkInfo.getSubtype();
                    switch (networkType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                            strNetworkType = "2G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                        case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                        case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                            strNetworkType = "3G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                            strNetworkType = "4G";
                            break;
                        default:
                            // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                            if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                strNetworkType = "3G";
                            } else {
                                strNetworkType = _strSubTypeName;
                            }

                            break;
                    }
                }
            }
        }
        if("LTE_CA".equals(strNetworkType)){
            MLog.i("nettype : "+ strNetworkType);
            strNetworkType = "4G";
        }
        return strNetworkType;
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context == null) {
            MLog.e(TAG, "isNetworkConnected,context is null");
            return false;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null) {
                return ni.isConnectedOrConnecting();
            }
        }

        return false;
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            MLog.e(TAG, "isNetworkAvailable,context is null");
            return false;
        }

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED && info.getExtraInfo() != null && (!"ssid>".contains(info.getExtraInfo()) && !"0x".contains(info.getExtraInfo()))) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是2G网络还是3G以上网络 false:2G网络;true:3G以上网络
     */
    private static boolean isFastMobileNetwork(Context context) {
//        return false;

        if (context == null) {
            MLog.e(TAG, "isFastMobileNetwork,context is null");
            return false;
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return false;
        }

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:// 0
                return false;
            case TelephonyManager.NETWORK_TYPE_GPRS:// 1
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:// 2
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_UMTS:// 3
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:// 4
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:// 5
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:// 6
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_1xRTT:// 7
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:// 8
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:// 9
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:// 10
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_IDEN:// 11
                return false; // ~25 kbps
            // SDK4.0才支持以下接口
            case 12:// TelephonyManager.NETWORK_TYPE_EVDO_B://12
                return true; // ~ 5 Mbps
            case 13:// TelephonyManager.NETWORK_TYPE_LTE://13
                return true; // ~ 10+ Mbps
            case 14:// TelephonyManager.NETWORK_TYPE_EHRPD://14
                return true; // ~ 1-2 Mbps
            case 15:// TelephonyManager.NETWORK_TYPE_HSPAP://15
                return true; // ~ 10-20 Mbps
            default:
                return false;
        }
    }

    /**
     * 获取本机ip
     *
     * @return 本机ip
     */
    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces != null) {
                List<NetworkInterface> interfaces = Collections.list(networkInterfaces);
                for (NetworkInterface ni : interfaces) {
                    Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                    if (inetAddresses != null) {
                        List<InetAddress> addresses = Collections.list(inetAddresses);
                        for (InetAddress address : addresses) {
                            if (address != null && address instanceof Inet4Address) {
                                return address.getHostAddress();
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return "";
    }

    /**
     * 获取运营商
     *
     * @param context
     * @return 运营商类型
     */
    public static String getOpreator(Context context) {
        if (context == null) {
            MLog.e(TAG, "getOpreator,context is null");
            return "";
        }

        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager != null) {
            String operator = telManager.getSimOperator();
            if (operator != null) {
                if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                    return "移动";
                } else if (operator.equals("46001")|| operator.equals("46005")) {
                    return "联通";
                } else if (operator.equals("46003")|| operator.equals("46006")||operator.equals("46011")) {
                    return "电信";
                }
            }
        }

        return "";
    }

    /**
     * 获取运营商
     *
     * @param context
     * @return 运营商类型
     */
    public static String getOpreatorCode(Context context) {
        if (context == null) {
            MLog.e(TAG, "getOpreator,context is null");
            return "";
        }

        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager != null) {
            String operator = telManager.getSimOperator();
            if (operator != null) {
                if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                    return "1";
                } else if (operator.equals("46001") || operator.equals("46005")) {
                    return "2";
                } else if (operator.equals("46003")|| operator.equals("46006")||operator.equals("46011")) {
                    return "0";
                }
            }
        }

        return "";
    }


    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    //根据IP获取本地Mac
    public static String getLocalMacAddressFromIp(Context context) {
        String mac_s= "";
        try {
            byte[] mac;
            NetworkInterface ne=NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
            mac = ne.getHardwareAddress();
            if (mac != null && mac.length > 0){
                mac_s = byte2hex(mac);
            }
        } catch (Exception e) {
            MLog.e("Exception " + e);
        }

        return mac_s;
    }

    public static  String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs.append("0").append(stmp);
            else {
                hs = hs.append(stmp);
            }
        }
        return String.valueOf(hs);
    }

    /**
     * 获取外网的IP(要访问Url，要放到后台线程里处理)
     *
     * @Title: GetNetIp
     * @Description:
     * @param @return
     * @return String
     * @throws
     */
    public static String getNetIp() {
        URL infoUrl = null;
        InputStream inStream = null;
        String ipLine = "";
        HttpURLConnection httpConnection = null;
        try {
            infoUrl = new URL("http://city.ip138.com/ip2city.asp");
            URLConnection connection = infoUrl.openConnection();
            httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    strber.append(line + "\n");

                Pattern pattern = Pattern
                        .compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
                Matcher matcher = pattern.matcher(strber.toString());
                if (matcher.find()) {
                    ipLine = matcher.group();
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null){
                    inStream.close();
                }
                httpConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ipLine;
    }

    public static String getNetIpByTaobo()
    {
        String IP = "";
        try
        {
            String address = "http://ip.taobao.com/service/getIpInfo.php?ip=myip";
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setUseCaches(false);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                InputStream in = connection.getInputStream();

                // 将流转化为字符串
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in));

                String tmpString = "";
                StringBuilder retJSON = new StringBuilder();
                while ((tmpString = reader.readLine()) != null)
                {
                    retJSON.append(tmpString + "\n");
                }

                JSONObject jsonObject = new JSONObject(retJSON.toString());
                String code = jsonObject.getString("code");
                if (code.equals("0"))
                {
                    JSONObject data = jsonObject.getJSONObject("data");
                    IP = data.getString("ip") + "(" + data.getString("country")
                            + data.getString("area") + "区"
                            + data.getString("region") + data.getString("city")
                            + data.getString("isp") + ")";

                    MLog.e("提示", "您的IP地址是：" + IP);

                    IP = data.getString("ip");
                }
                else
                {
                    IP = "";
                    MLog.e("提示", "IP接口异常，无法获取IP地址！");
                }
            }
            else
            {
                IP = "";
                MLog.e("提示", "网络连接异常，无法获取IP地址！");
            }
        }
        catch (Exception e)
        {
            IP = "";
            MLog.e("提示", "获取IP地址时出现异常，异常信息是：" + e.toString());
        }
        return IP;
    }
}
