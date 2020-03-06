//
// Created by dengjun on 2018/11/15.
//

#include "cloudvoice_android_log.h"

void setDebug(jboolean isDebug) {
    DEBUG_FLAG = isDebug;
}

static void androidLogPrint(int level, const char *fmt, va_list args) {
    char log[2048];
    vsprintf(log, fmt, args);
    switch (level){
        case LOG_VERBOSE:
            LOGV("%s",log);
            break;
        case LOG_DEBUG:
            LOGD("%s",log);
            break;
        case LOG_INFO:
            LOGI("%s",log);
            break;
        case LOG_WARNING:
            LOGW("%s",log);
            break;
        case LOG_ERROR:
            LOGE("%s",log);
            break;
    }

}


void LogInit(){
   KLogSetCallback(androidLogPrint);
}