package com.lib.commonlib.http;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by dengjun on 2017/10/31.
 */

public class PrimaryHttpClient {
    private Executor mExecutor;
    private Handler mHandler;

    private int mTimeOut = 10*1000;

    public PrimaryHttpClient() {
        initExecutor();
    }

    public PrimaryHttpClient(Executor mExecutor, Handler mHandler) {
        this.mExecutor = mExecutor;
        this.mHandler = mHandler;
    }

    public PrimaryHttpClient(Handler handler) {
        this.mHandler = handler;
    }

    private void initExecutor(){
        if (mExecutor ==  null){
            mExecutor = Executors.newScheduledThreadPool(5);
        }
    }

    public void initHandler(){
        if (mHandler == null){
            mHandler = new Handler(Looper.getMainLooper());
        }
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }


    public void setTimeOut(int timeOut) {
        if (timeOut > 1*1000 && timeOut < 60*1000){
            this.mTimeOut = timeOut;
        }
    }

    private void callbackResult(final String result, final HttpRequestCallback<String> callback){
        if (callback != null){
            if (mHandler != null){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(result);
                    }
                });
            }else{
                callback.onResult(result);
            }
        }
    }

    private void callbackException(final Exception error, final HttpRequestCallback<String> callback){
        if (callback != null){
            if (mHandler != null){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(error);
                    }
                });
            }else{
                callback.onError(error);
            }
        }
    }

    public void requestAsync(String url,HttpRequestCallback<String> callback){
        requestAsync(url,null,callback);
    }

    public void requestAsync(final String url, final String body, final HttpRequestCallback <String>callback){
        initExecutor();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                requestSync(url,body,callback);
            }
        });
    }

    private void requestSync(String url,String body,HttpRequestCallback <String>callback){
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            URL requestUrl  = new URL(url);
            connection = (HttpURLConnection)requestUrl.openConnection();

            connection.setConnectTimeout(mTimeOut);
            connection.setReadTimeout(mTimeOut);

            if (body != null){
                connection.setRequestMethod("POST");

                //发送post请求必须设置
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                DataOutputStream out = new DataOutputStream(connection
                        .getOutputStream());
                out.writeBytes(body);//写入请求参数
                out.flush();
                out.close();
            }else {
                connection.setRequestMethod("GET");
            }

            if (connection.getResponseCode() == 200) {
                InputStream in = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));

                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                String result = sb.toString();

                if (result != null && !"".equals(result)){
                    callbackResult(result,callback);
                }else {
                    callbackException(new Exception("Received nothing"), callback);
                }
            }else {
                callbackException(new Exception("Request ResponseCode != 200"), callback);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            callbackException(e,callback);
        }catch (IOException e){
            e.printStackTrace();
            callbackException(e,callback);
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();//断开连接，释放资源
            }
        }
    }

    public String requestSync(String url) throws Exception{
        return requestSync(url,null);
    }

    public String requestSync(String url,String body) throws Exception{

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            URL requestUrl  = new URL(url);
            connection = (HttpURLConnection)requestUrl.openConnection();

            connection.setConnectTimeout(mTimeOut);
            connection.setReadTimeout(mTimeOut);

            if (body != null){
                connection.setRequestMethod("POST");

                //发送post请求必须设置
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                DataOutputStream out = new DataOutputStream(connection
                        .getOutputStream());
                out.writeBytes(body);//写入请求参数
                out.flush();
                out.close();
            }else {
                connection.setRequestMethod("GET");
            }

            if (connection.getResponseCode() == 200) {
                InputStream in = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));

                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                String result = sb.toString();

                if (result != null && !"".equals(result)){
                    return result;
                }else {
                    throw (new Exception("Received nothing"));
                }
            }else {
                throw (new Exception("Request ResponseCode != 200"));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw e;
        }catch (IOException e){
            e.printStackTrace();
            throw e;
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();//断开连接，释放资源
            }
        }
    }
}
