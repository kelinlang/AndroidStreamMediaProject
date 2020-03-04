package com.stream.media.codec;



import com.lib.commonlib.utils.MLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dengjun on 2017/12/6.
 */

public class FileStorage {
    private String fileAbPath;

    private File audioFile;
    private FileOutputStream fileOutputStream;

    private boolean saveEnableFlag = false;

    public FileStorage() {
    }

    public FileStorage(String fileAbPath) {
        this.fileAbPath = fileAbPath;
    }

    public void setFileAbPath(String fileAbPath) {
        this.fileAbPath = fileAbPath;
    }

    public void  open(){
        if (saveEnableFlag == false){
            return;
        }
        audioFile = new File(fileAbPath);
        if (audioFile.exists()){
            audioFile.delete();
        }
        try {
            audioFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            MLog.i("createNewFile问题异常 "+ e);
        }
        try {
            fileOutputStream = new FileOutputStream(audioFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            MLog.i("FileOutputStream问题异常 "+ e);
        }
    }


    public void close(){
        if (fileOutputStream != null){
            try {
                fileOutputStream.close();
                fileOutputStream = null;
                MLog.i("保存录音文件："+audioFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                MLog.i("close异常 "+ e);
            }
        }
    }

    public void wirte(short[] data){
        if (fileOutputStream != null && data != null){
            try {
//                MLog.i("write  start=---------------------");
                fileOutputStream.write(shortsToByteArray(data));
//                MLog.i("write  end=---------------------");
            } catch (IOException e) {
                MLog.i("wirte异常 "+ e);
                close();
            }
        }
    }

    public void wirte(byte[] data){
        if (fileOutputStream != null && data != null){
            try {
                fileOutputStream.write(data);
            } catch (IOException e) {
                MLog.i("wirte异常 "+ e);
                close();
            }
        }
    }

    public static byte[] shortsToByteArray(short[] shorts){
//        MLog.i("shortsToByteArray  start=---------------------");
        byte[] shortsBuf = new byte[shorts.length*2];
        int n=0;
        for(int i=0;i<shorts.length;i++){
            byte[] shortBuf = shortToByte(shorts[i]);
            for(int j=0;j<2;j++){
                shortsBuf[n] = shortBuf[j];
                n++;
            }
        }
//        MLog.i("shortsToByteArray  end=---------------------");
        return shortsBuf;
    }

    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();//
            //将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    public static void bytesToFile(byte[] data,String filePath) throws IOException {
        File wavFile = new File(filePath);
        if (wavFile.exists()){
            wavFile.delete();
        }
        wavFile.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(wavFile);
        fileOutputStream.write(data);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public void setSaveEnableFlag(boolean saveEnableFlag) {
        this.saveEnableFlag = saveEnableFlag;
    }
}
