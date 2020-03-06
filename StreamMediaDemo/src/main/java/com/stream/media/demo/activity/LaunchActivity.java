package com.stream.media.demo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.lib.commonlib.CommonLib;
import com.lib.commonlib.utils.SoLoadUtils;
import com.stream.media.demo.R;

import java.util.ArrayList;
import java.util.List;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        CommonLib.getInstance().setContext(getApplicationContext());
//        SoLoadUtils.loadLibrary(getApplicationContext(),"StreamMediaLib");

//        getImeiPerssion();
//

        if(Build.VERSION.SDK_INT >= 23){
            if (checkAndRequestPermission()){

            }
        }else {

        }
        onToPlayerAcitvity(null);
    }


    public void onToPlayerAcitvity(View view){
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }


    static {
        try {
            System.loadLibrary("StreamMediaLib");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getImeiPerssion() {

        if (!isGranted_(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            String[] permissions2 = new String[1];
            permissions2[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            ActivityCompat.requestPermissions(
                    LaunchActivity.this, permissions2, 1000
            );
        }

        if (!isGranted_(Manifest.permission.READ_PHONE_STATE)) {
            String[] permissions1 = new String[1];
            permissions1[0] = Manifest.permission.READ_PHONE_STATE;
            ActivityCompat.requestPermissions(
                    LaunchActivity.this, permissions1, 1001
            );
        }

        if (!isGranted_(Manifest.permission.READ_SMS)) {
            String[] permissions3 = new String[1];
            permissions3[0] = Manifest.permission.READ_SMS;
            ActivityCompat.requestPermissions(
                    LaunchActivity.this, permissions3, 1002
            );
        }


        if (!isGranted_(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            String[] permissions3 = new String[1];
            permissions3[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
            ActivityCompat.requestPermissions(
                    LaunchActivity.this, permissions3, 1003
            );
        }

        if (!isGranted_(Manifest.permission.CAMERA)) {
            String[] permissions3 = new String[1];
            permissions3[0] = Manifest.permission.CAMERA;
            ActivityCompat.requestPermissions(
                    LaunchActivity.this, permissions3, 1004
            );
        }
    }

    private boolean isGranted_(String permission) {
        int checkSelfPermission = ActivityCompat.checkSelfPermission(this, permission);
        return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024) {


        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.CAMERA);
        }

        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (lackedPermission.size() != 0) {
            // 建议请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
            return false;
        }else {
            return true;
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
}
