package com.realai.realaitv;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import videoPlayer.AndExoPlayerView;


public class ReplayVideoActivity extends Activity {

    private AndExoPlayerView andExoPlayerView;
    private String TEST_URL_MP4 = "https://realaiuploadplayersvideo.s3.ap-south-1.amazonaws.com/2020-09-29/a12e03be-5fa3-4438-858e-026cccf93258/near/213a77c7-869a-4566-b040-315fada2cd12/HighlightsVideo/1601385560423.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay_video);

        andExoPlayerView = findViewById(R.id.andExoPlayerView);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (getIntent().getStringExtra("REPLAY_URL") != null) {
            TEST_URL_MP4 = getIntent().getStringExtra("REPLAY_URL");
        }

        andExoPlayerView.setSource(TEST_URL_MP4);

        andExoPlayerView.getPlayer().addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.e("playWhenReady", "" + playWhenReady);
                switch (playbackState) {
                    case Player.STATE_IDLE:
                        break;
                    case Player.STATE_BUFFERING:
                        break;
                    case Player.STATE_READY:
                        break;
                    case Player.STATE_ENDED:
                        Toast.makeText(getApplicationContext(), "Video Completed", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

}
