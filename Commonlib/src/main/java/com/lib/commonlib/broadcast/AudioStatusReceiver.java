package com.lib.commonlib.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.lib.commonlib.utils.MLog;


/**
 * @author Administrator
 * @CreateDate 2016-09-30 15:21.
 */
public class AudioStatusReceiver extends BroadcastReceiver {
    public final static String VOLUME_CHANGE_ACTION = "android.media.VOLUME_CHANGED_ACTION";

    private Context mContext;
    private StatusCallback statusCallback;

    public AudioStatusReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())){
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) {
                    callbackStatus(StatusCallback.WIRED_HEADSET_OFF);
                } else if (intent.getIntExtra("state", 0) == 1) {
                    callbackStatus(StatusCallback.WIRED_HEADSET_ON);
                }
            }
        }
        if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(intent.getAction())) {		//蓝牙连接状态
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1);
            if (state == BluetoothAdapter.STATE_CONNECTED){
                //连接或失联，切换音频输出（到蓝牙、或者强制仍然扬声器外放）
                MLog.i("蓝牙连接，切换音频输出（到蓝牙、或者强制仍然扬声器外放）");
                callbackStatus(StatusCallback.BLUETOOTH_STATUE_ON);
            }else if (state == BluetoothAdapter.STATE_DISCONNECTED){
                MLog.i("蓝牙失联，切换音频输出（到蓝牙、或者强制仍然扬声器外放）");
                callbackStatus(StatusCallback.BLUETOOTH_STATUE_OFF);
            }
        } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){	//本地蓝牙打开或关闭
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF) {
                //断开，切换音频输出
                MLog.i("蓝牙断开，切换音频输出");
                callbackStatus(StatusCallback.BLUETOOTH_STATUE_OFF);
            }
        }
        if (VOLUME_CHANGE_ACTION.equals(intent.getAction())){
            callbackStatus(StatusCallback.VOLUME_CHANGE_ACTION);
        }
    }

    private void callbackStatus(int netStatus) {
        if (statusCallback != null) {
            statusCallback.onStatus(netStatus);
        }
    }


    public void register(Context context) {
        if (mContext == null && context != null) {
            mContext = context;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
            intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            intentFilter.addAction(VOLUME_CHANGE_ACTION);

            mContext.registerReceiver(this, intentFilter);
        }
    }

    public void unRegister() {
        if (mContext != null) {
            mContext.unregisterReceiver(this);
            mContext = null;
        }
    }

    public void setStatusCallback(StatusCallback netStatusCallback) {
        this.statusCallback = netStatusCallback;
    }

    public interface StatusCallback {
        int WIRED_HEADSET_OFF= 0;
        int WIRED_HEADSET_ON = 1;
        int BLUETOOTH_STATUE_ON = 2;
        int BLUETOOTH_STATUE_OFF = 3;
        int VOLUME_CHANGE_ACTION = 4;


        void onStatus(int status);
    }
}
