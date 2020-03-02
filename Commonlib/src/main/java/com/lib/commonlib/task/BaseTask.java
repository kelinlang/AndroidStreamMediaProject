package com.lib.commonlib.task;

import com.lib.commonlib.utils.MLog;

/**
 * Created by dengjun on 2018/6/25.
 */

public abstract class BaseTask implements Runnable {
    protected abstract void onStart();

    protected abstract void onStop();

    protected abstract void execute();

    @Override
    public void run() {
        MLog.d(this.getClass().getSimpleName()+" : onStart "+" Thread id :"+Thread.currentThread().getId());
        onStart();

        execute();

        MLog.d(this.getClass().getSimpleName()+" : onStop "+" Thread id :"+Thread.currentThread().getId());
        onStop();
    }
}
