package com.lib.commonlib.uuid;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class UUIDGenerator {
    private static final String UUID_PATH = "/uuinfo";
    private static final String UUID_FILENAME = "phone_uuid.tmp";
    private static final String other_packages = "asdf";
    public static final String DEV_UUID = "dev_uuid";//设备的uuid
    public static final String SP_NAME = "jb_sp";
    private static String TAG = UUIDGenerator.class.getSimpleName();

    public static String getUUID(Context context) {
        String yauuid = readFile(getBlmPath(context) + UUID_PATH, UUID_FILENAME);

        if (!TextUtils.isEmpty(yauuid)) {
            return yauuid;
        }

        String filePath = generatePath(context) + UUID_PATH;
        String phone_uuid = readFile(filePath, UUID_FILENAME);
        if (phone_uuid == null || phone_uuid.length() == 0) {
            phone_uuid = getString(context, DEV_UUID, "");
            if (TextUtils.isEmpty(phone_uuid) || phone_uuid.length() == 0) {
                UUID uuid = UUID.randomUUID();
                phone_uuid = uuid.toString().replaceAll("-", "").trim();
                putString(context, DEV_UUID, phone_uuid);
            }
            writeFile(filePath, phone_uuid, UUID_FILENAME);
        }
        return phone_uuid;
    }

    //之前的UUID如果在yaya中找不到中间会在blm中创建，现在改成还是在yaya文件夹中创建，
    // 但是要保证yaya文件夹中没有其它文件，否则又被识别为广告的风险
    public static String generatePath(Context context) {
        String root = "";
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            if (checkWriteExPermision(context)) {
                root = Environment.getExternalStorageDirectory().toString() + File.separator + getYaString();
            } else {
                root = context.getFilesDir().getAbsolutePath() + File.separator + getYaString();
            }
        } else {
            root = context.getFilesDir().getAbsolutePath() + File.separator + getYaString();
        }
        return root;
    }

    public static String getBlmPath(Context context) {
        String root = "";
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            root = Environment.getExternalStorageDirectory().getPath().toString() + File.separator + "blm";
        } else {
            root = context.getFilesDir().getAbsolutePath() + File.separator + "blm";
        }
        return root;
    }


    private static String getYaString() {
        byte[] abb = {121, 97, 121, 97};
        return new String(abb);
    }


    public static String readother_path(Context context) {
        return readFile(generatePath(context), other_packages);

    }

    public static void startotherSer(Context context) {
        String ownpackage = context.getApplicationInfo().processName;
        String rootpath = generatePath(context);
        String packages = readother_path(context);
        if (!TextUtils.isEmpty(packages)) {
            if (packages.contains(",")) {
                String[] stringpackages = packages.split(",");
                for (int i = 0; i < stringpackages.length; i++) {
                    if (!stringpackages[i].equals(ownpackage)) {
                        startservice(context, stringpackages[i]);
//                        MyLog.d("UUIDGenerator", "startservice--" + stringpackages[i]);
                    }

                }
                if (!packages.contains(ownpackage)) {
                    writeFile(rootpath, packages + "," + ownpackage, other_packages);
//                    MyLog.d("UUIDGenerator", "writeownpackage--" + ownpackage);
                }

            } else {
                if (!packages.equals(ownpackage)) {
                    writeFile(rootpath, packages + "," + ownpackage, other_packages);
//                    MyLog.d("UUIDGenerator", "writefileandstartservice--" + ownpackage);
                    startservice(context, ownpackage);
                }
            }

        } else {
//            MyLog.d("UUIDGenerator", "writefile--" + ownpackage);
            writeFile(rootpath, ownpackage, other_packages);
        }

    }

    private static void startservice(Context context, String apackage) {
  /*      try {
            Intent intent2 = new Intent();
            intent2.setComponent(new ComponentName(apackage, "com.pg.im.sdk.lib.YayaService"));
            context.startService(intent2);
        } catch (Exception e) {
//            MyLog.e("UUIDGenerator", "start service error");
        }*/

    }

    /**
     * 写入文件
     *
     * @param body
     * @param fileName
     */
    private static void writeFile(String filePath, String body, String fileName) {

        BufferedWriter bw = null;
        try {

            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }

            bw = new BufferedWriter(new FileWriter(filePath + File.separator + fileName));//字符输出流  写数据到文件
            bw.write(body);
            bw.flush();
        } catch (IOException e) {
//            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文件
     *
     * @param fileName
     * @return
     */
    private static String readFile(String filePath, String fileName) {
        String result = "";
        BufferedReader br = null;
        try {

            File file = new File(filePath + File.separator + fileName);
            if (file.exists()) {
                br = new BufferedReader(new FileReader(file));//字符输入流  从文件读取数据
                result = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 检查是否存在写外部存储权限
     *
     * @param context
     * @return
     */
    private static boolean checkWriteExPermision(Context context) {
        PackageManager pm = context.getPackageManager();
        String packagename = context.getPackageName();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", packagename));
        return permission;
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        //添加保存数据
        sp.edit().putString(key, value).commit();

    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);

    }

}
