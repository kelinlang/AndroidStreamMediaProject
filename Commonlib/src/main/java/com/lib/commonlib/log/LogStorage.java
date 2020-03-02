package com.lib.commonlib.log;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.lib.commonlib.utils.MLog;
import com.lib.commonlib.utils.PermssionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


/**
 * Created by dengjun on 2018/4/24.
 */

public class LogStorage {
    private final static int log_file_max_len = 10*1024*1024;
    private String logFilePath;
    private String logFileAbPath;
    private int logFileMaxNum = 8;
    private List<File> logFileList;

    private String logTag;

    private Context context;

    private boolean writeThreadRunFlag = false;
    private Thread writeThread;

    private volatile boolean newLogFileFlag = false;
    private volatile boolean writeLogFileFlag = false;

    private LogFileCallback logFileCallback;

    private Process logcatProc = null;

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public String getLogTag() {
        return logTag;
    }

    public void setLogTag(String logTag) {
        this.logTag = logTag;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setLogFileCallback(LogFileCallback logFileCallback) {
        this.logFileCallback = logFileCallback;
    }

    /**
     * 开始写一个新的log文件
     */
    public void startNewLogFile(){
        newLogFileFlag = true;
    }

    public void start(){
        if (TextUtils.isEmpty(logFilePath)){
            MLog.i("logFilePath is empty");
            return;
        }

        if (TextUtils.isEmpty(logTag)){
            MLog.i("logTag is empty");
            return;
        }

        if (context == null){
            throw new NullPointerException("context == null");
        }

        writeThreadRunFlag = true;
        writeLogFileFlag = true;
        writeThread = new Thread(new Runnable() {
            @Override
            public void run() {

                BufferedReader reader = null;
                FileOutputStream fileOutputStream = null;
                try {
                    createLogDir();

                    String cmd = "logcat -v time -s "+logTag;

                    logcatProc = Runtime.getRuntime().exec(cmd);
                    reader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()), 1024);

                    String curPid =  String.valueOf(android.os.Process.myPid());

                    while (writeThreadRunFlag){
                        trimFile();

                        File logFile = new File(logFileAbPath,generateFileName());
                        if (!logFile.exists()){
                            logFile.createNewFile();
                        }
                        logFileList.add(logFile);

                        String line = null;
                        fileOutputStream = new FileOutputStream(logFile);

                        MLog.i("write log file start : "+ logFile.getAbsolutePath());
                        while (!newLogFileFlag && writeLogFileFlag
                                && (line = reader.readLine()) != null
                                &&logFile.length() <= log_file_max_len){
                            if (TextUtils.isEmpty(line)){
                                continue;
                            }
                            if (line.contains(curPid)){
//                                MLog.d("log : "+line);
                                fileOutputStream.write((line+"\n").getBytes());
                                fileOutputStream.flush();
                            }
                        }
                        try {
                            if (fileOutputStream != null){
                                fileOutputStream.close();
                                fileOutputStream = null;
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                            MLog.e("log fileOutputStream close",e);
                        }

                        newLogFileFlag = false;

                        MLog.i("write log file finish : "+ logFile.getAbsolutePath());
                        if (logFileCallback != null&& writeThreadRunFlag){
                            logFileCallback.onLogFile(logFile.getAbsolutePath());
                        }
                    }
                    MLog.i("log loop over");
                }catch (Exception e){
                    e.printStackTrace();
                    MLog.e("log writeThread error",e);
                }finally {
                    MLog.i("log thread finally");
                    if (reader != null){
                        try {
                            if (reader != null){
                                reader.close();
                                MLog.i("reader close");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            MLog.e("log reader close error",e);
                        }
                    }
                    try {
                        if (fileOutputStream != null){
                            fileOutputStream.close();
                            MLog.i("log outputstream close");
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                        MLog.e("log fileOutputStream close",e);
                    }
                    if (logcatProc != null){
                        synchronized (this){
                            if (logcatProc != null){
                                MLog.i("logcatProc.destroy");
                                logcatProc.destroy();
                                logcatProc = null;
                            }
                        }
                    }
                }
                MLog.i("log writeThread finish");
            }
        });
        writeThread.start();
    }

    public void stop(){
        MLog.i("log storage stop");
        writeThreadRunFlag = false;
        writeLogFileFlag = false;
       /* if (logcatProc != null){
            synchronized (this){
                if (logcatProc != null){
                    MLog.i("logcatProc.destroy");
                    logcatProc.destroy();
                    logcatProc = null;
                }
            }
        }*/
        if (writeThread != null) {
            try {
                writeThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MLog.i("log storage stop  finish");
    }

    private void createLogDir(){
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        if (sdCardExist && PermssionUtils.checkPermision(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            MLog.i("had WRITE_EXTERNAL_STORAGE permission");
//            logFileAbPath = Environment.getExternalStorageDirectory().toString()+ File.separator+logFilePath;
            logFileAbPath = context.getExternalCacheDir().getPath()+ File.separator+logFilePath;
        }else {
            MLog.i("had not WRITE_EXTERNAL_STORAGE permission");
            logFileAbPath = context.getFilesDir().getPath()+ File.separator+logFilePath;
        }

        MLog.i("logFileAbPath : "+logFileAbPath);
        File fileDir = new File(logFileAbPath);
        if (!fileDir.exists()){
            fileDir.mkdirs();
        }
        if (logFileList == null){
            logFileList = new ArrayList<>();
        }

        File[] files = fileDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".txt") && file.getName().startsWith("log_")){
                    logFileList.add(file);
                }
            }
        }
    }

    private void trimFile(){
        MLog.i("file size : "+logFileList.size());
        if (logFileList.size() > logFileMaxNum){
            Collections.sort(logFileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return (o1.lastModified() < o2.lastModified()) ? -1 : ((o1.lastModified() == o2.lastModified()) ? 0 : 1);
                }
            });
            for (int i = 0; i< logFileList.size() -logFileMaxNum; i++){
                File file = logFileList.remove(0);
                if (file.exists()){
                    MLog.i("delete file : "+ file.getAbsolutePath());
                    file.delete();
                }
            }
        }
    }

    private String generateFileName(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS", Locale.getDefault());
        String str = "log_"+sdf.format(calendar.getTime());
        str += ".txt";
        return str;
    }


    public interface LogFileCallback{
        void  onLogFile(String logFileAbPath);
    }
}
