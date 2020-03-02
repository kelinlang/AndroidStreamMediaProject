package com.lib.commonlib.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import com.lib.commonlib.utils.MLog;


/**
 * @author Administrator
 * @CreateDate 2016-09-30 15:21.
 */
public class NetworkStatusReceiver extends BroadcastReceiver {
    private Context mContext;
    private NetStatusCallback mNetStatusCallback;

    private volatile boolean enable = false;

    public NetworkStatusReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                MLog.i("mobileInfo : "+ mobileInfo + " , wifiInfo : "+ wifiInfo);

                if (wifiInfo != null && wifiInfo.isConnected()){
                    //关闭加速
                    callbackNetStatus(NetStatusCallback.WIFI_NET_CONNECT);
                }else{
                    if (mobileInfo != null && mobileInfo.isConnected()){
                        //开启加速
                        callbackNetStatus(NetStatusCallback.MOBILE_NET_CONNECT);
                    }else {
                        //关闭加速
                        callbackNetStatus(NetStatusCallback.NET_DISCONNECT);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            MLog.e("net status error ",e);
            callbackNetStatus(NetStatusCallback.NET_DISCONNECT);
        }
    }

    private void callbackNetStatus(int netStatus){
        if (mNetStatusCallback != null && enable){
            mNetStatusCallback.onStatus(netStatus);
        }
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void register(Context context){
        if (mContext == null && context != null){
            mContext = context;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            mContext.registerReceiver(this,intentFilter);
        }
    }

    public void unRegister(){
        if (mContext != null){
            mContext.unregisterReceiver(this);
            mContext = null;
        }
    }

    public void setNetStatusCallback(NetStatusCallback netStatusCallback) {
        this.mNetStatusCallback = netStatusCallback;
    }

    public interface NetStatusCallback {
        int NET_DISCONNECT = 0;
        int WIFI_NET_CONNECT = 1;
        int MOBILE_NET_CONNECT = 2;
        void onStatus(int status);
    }
}
