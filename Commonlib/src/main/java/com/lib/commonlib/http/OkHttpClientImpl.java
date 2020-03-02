package com.lib.commonlib.http;




import com.network.okhttp.Call;
import com.network.okhttp.Callback;
import com.network.okhttp.MediaType;
import com.network.okhttp.OkHttpClient;
import com.network.okhttp.Request;
import com.network.okhttp.RequestBody;
import com.network.okhttp.Response;
import com.lib.commonlib.utils.DataJsonTranslation;

import java.io.IOException;
import java.util.concurrent.TimeUnit;



/**
 * Created by dengjun on 2017/10/16.
 */

public class OkHttpClientImpl {
    private static final long HTTP_TIME_OUT_DEF = 10000;
    private OkHttpClient mOkHttpClient;

    public OkHttpClientImpl() {
        init();
    }

    private void init(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder
                .readTimeout(HTTP_TIME_OUT_DEF, TimeUnit.MILLISECONDS)
                .writeTimeout(HTTP_TIME_OUT_DEF, TimeUnit.MILLISECONDS)
                .connectTimeout(HTTP_TIME_OUT_DEF, TimeUnit.MILLISECONDS)
                ;
        mOkHttpClient = builder.build();
    }

    public void setmOkHttpClient(OkHttpClient okHttpClient) {
        this.mOkHttpClient = okHttpClient;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public Call enqueue(String url, Object bodyObject, Callback callback){
        final Request request;

        if (bodyObject != null){
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    DataJsonTranslation.objectToJson(bodyObject));

            request = new Request.Builder()
                    .post(body)
                    .url(url)
                    .build();
        }else {
            request = new Request.Builder()
                    .url(url)
                    .build();
        }

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call execute(String url, String bodyObject,String mediaType, Callback callback){
        final Request request;

        if (bodyObject != null){
            RequestBody body = RequestBody.create(
                    MediaType.parse(mediaType),
                    bodyObject);

            request = new Request.Builder()
                    .post(body)
                    .url(url)
                    .build();
        }else {
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
        }

        Call call = mOkHttpClient.newCall(request);
        try {
            Response response = call.execute();
            if (callback!= null){
                callback.onResponse(call,response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (callback!= null){
                callback.onFailure(call,e);
            }
        }

        return call;
    }


    public Call enqueue(String url, String bodyString,String mediaType, Callback callback){
        final Request request;

        if (bodyString != null){
            RequestBody body = RequestBody.create(
                    MediaType.parse(mediaType),
                    bodyString);

            request = new Request.Builder()
                    .post(body)
                    .url(url)
                    .build();
        }else {
            request = new Request.Builder()
                    .url(url)
                    .build();
        }

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call enqueue(String url, String bodyString, Callback callback){
        final Request request;

        if (bodyString != null){
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    bodyString);

            request = new Request.Builder()
                    .post(body)
                    .url(url)
                    .build();
        }else {
            request = new Request.Builder()
                    .url(url)
                    .build();
        }

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call enqueue(String url, byte[] bodyData, Callback callback){
        final Request request;

        if (bodyData != null){
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/octet-stream"),
                    bodyData);

            request = new Request.Builder()
                    .post(body)
                    .url(url)
                    .build();
        }else {
            request = new Request.Builder()
                    .url(url)
                    .build();
        }

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call execute(String url, byte[] bodyData, Callback callback){
        final Request request;

        if (bodyData != null){
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/octet-stream"),
                    bodyData);

            request = new Request.Builder()
                    .post(body)
                    .url(url)
                    .build();
        }else {
            request = new Request.Builder()
                    .url(url)
                    .build();
        }

        Call call = mOkHttpClient.newCall(request);

        try {
            Response response = call.execute();
            if (callback!= null){
                callback.onResponse(call,response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (callback!= null){
                callback.onFailure(call,e);
            }
        }

        return call;
    }
}
