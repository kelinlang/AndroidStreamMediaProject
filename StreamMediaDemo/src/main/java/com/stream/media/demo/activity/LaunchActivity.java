package com.stream.media.demo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.lib.commonlib.CommonLib;
import com.lib.commonlib.utils.SoLoadUtils;
import com.stream.media.demo.R;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        CommonLib.getInstance().setContext(getApplicationContext());
//        SoLoadUtils.loadLibrary(getApplicationContext(),"StreamMediaLib");

        getImeiPerssion();
//        onToPlayerAcitvity(null);
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
    }

    private boolean isGranted_(String permission) {
        int checkSelfPermission = ActivityCompat.checkSelfPermission(this, permission);
        return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
    }
}
