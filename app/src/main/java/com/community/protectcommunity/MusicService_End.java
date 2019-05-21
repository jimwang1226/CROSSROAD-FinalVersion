package com.community.protectcommunity;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class MusicService_End extends Service  implements MediaPlayer.OnErrorListener {

    private final IBinder mBinder = new ServiceBinder();
    MediaPlayer mPlayer_end;
    private int length = 0;

    public MusicService_End() {
    }

    public class ServiceBinder extends Binder {
        MusicService_End getService() {
            return MusicService_End.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer_end = MediaPlayer.create(this, R.raw.background_music_ending);
        mPlayer_end.setOnErrorListener(this);

        if (mPlayer_end != null) {
            mPlayer_end.setLooping(true);
            mPlayer_end.setVolume(80, 80);
        }


        mPlayer_end.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int
                    extra) {

                onError(mPlayer_end, what, extra);
                return true;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mPlayer_end != null) {
            mPlayer_end.start();
        }

        return START_STICKY;
    }

    public void pauseMusic() {
        if (mPlayer_end != null) {
            if (mPlayer_end.isPlaying()) {
                mPlayer_end.pause();
                length = mPlayer_end.getCurrentPosition();

            }
        }

    }

    public void resumeMusic() {
        if (mPlayer_end != null) {
            if (mPlayer_end.isPlaying() == false) {
                mPlayer_end.seekTo(length);
                mPlayer_end.start();
            }
        }

    }

    public void stopMusic() {
        if (mPlayer_end != null) {
            mPlayer_end.stop();
            mPlayer_end.release();
            mPlayer_end = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer_end != null) {
            try {
                mPlayer_end.stop();
                mPlayer_end.release();
            } finally {
                mPlayer_end = null;
            }
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {

        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
        if (mPlayer_end != null) {
            try {
                mPlayer_end.stop();
                mPlayer_end.release();
            } finally {
                mPlayer_end = null;
            }
        }
        return false;
    }

    public void start() {
        if(mPlayer_end != null) {
            mPlayer_end.start();
        }
    }

    public boolean isAlive() {
        boolean result = false;
        if(mPlayer_end != null) {
            result = mPlayer_end.isPlaying();
        }
        return result;
    }
}
