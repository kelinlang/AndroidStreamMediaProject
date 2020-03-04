package com.stream.media.demo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
}
