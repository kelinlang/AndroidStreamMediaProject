package com.lib.commonlib.http;

/**
 * Created by dengjun on 2017/10/17.
 */

public interface HttpRequestCallback<T> {
    void onResult(T result);
    void onError(Exception e);
}
