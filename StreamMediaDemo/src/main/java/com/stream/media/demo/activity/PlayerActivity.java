package com.stream.media.demo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.stream.media.demo.R;
import com.stream.media.jni.MediaJni;
import com.stream.media.jni.StreamParam;
import com.stream.media.view.CameraPriviewView;
import com.stream.media.view.MediaPlayerView;
import com.stream.media.view.VideoDisplayView;

public class PlayerActivity extends AppCompatActivity {
    private CameraPriviewView cameraPriviewView;
    private MediaPlayerView videoDisplayView;
    private MediaJni mediaJni = new MediaJni();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        cameraPriviewView = findViewById(R.id.camera_preview_sf);
        videoDisplayView = findViewById(R.id.media_player_sf);

        cameraPriviewView.setMediaJni(mediaJni);
        StreamParam streamParam = new StreamParam();
        streamParam.type = StreamParam.TYPE_PUSH;
        streamParam.id = "self";
        streamParam.url = "rtmp://192.168.1.6:1935/live/test";
        cameraPriviewView.setStreamParam(streamParam);
    }


    public void onToStartPlayer(View view){
        videoDisplayView.setVisibility(View.VISIBLE);
    }

    public void onToStopPlayer(View view){
        videoDisplayView.setVisibility(View.GONE);
    }

    public void onToStartPush(View view){
        cameraPriviewView.setVisibility(View.VISIBLE);
    }

    public void onToStopPush(View view){
        cameraPriviewView.setVisibility(View.GONE);
    }
}



