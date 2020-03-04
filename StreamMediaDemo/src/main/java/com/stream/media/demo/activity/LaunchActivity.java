package com.stream.media.demo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.stream.media.demo.R;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }


    public void onToPlayerAcitvity(View view){
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }
}
