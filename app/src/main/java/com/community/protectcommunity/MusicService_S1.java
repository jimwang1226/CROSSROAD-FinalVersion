package com.community.protectcommunity;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class MusicService_S1 extends Service  implements MediaPlayer.OnErrorListener {

    private final IBinder mBinder = new ServiceBinder();
    MediaPlayer mPlayer_s1;
    private int length = 0;

    public MusicService_S1() {
    }

    public class ServiceBinder extends Binder {
        MusicService_S1 getService() {
            return MusicService_S1.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer_s1 = MediaPlayer.create(this, R.raw.background_music_game);
        mPlayer_s1.setOnErrorListener(this);

        if (mPlayer_s1 != null) {
            mPlayer_s1.setLooping(true);
            mPlayer_s1.setVolume(80, 80);
        }


        mPlayer_s1.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int
                    extra) {

                onError(mPlayer_s1, what, extra);
                return true;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mPlayer_s1 != null) {
            mPlayer_s1.start();
        }

        return START_STICKY;
    }

    public void pauseMusic() {
        if (mPlayer_s1 != null) {
            if (mPlayer_s1.isPlaying()) {
                mPlayer_s1.pause();
                length = mPlayer_s1.getCurrentPosition();

            }
        }

    }

    public void resumeMusic() {
        if (mPlayer_s1 != null) {
            if (mPlayer_s1.isPlaying() == false) {
                mPlayer_s1.seekTo(length);
                mPlayer_s1.start();
            }
        }

    }

    public void stopMusic() {
        if (mPlayer_s1 != null) {
            mPlayer_s1.stop();
            mPlayer_s1.release();
            mPlayer_s1 = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer_s1 != null) {
            try {
                mPlayer_s1.stop();
                mPlayer_s1.release();
            } finally {
                mPlayer_s1 = null;
            }
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {

        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
        if (mPlayer_s1 != null) {
            try {
                mPlayer_s1.stop();
                mPlayer_s1.release();
            } finally {
                mPlayer_s1 = null;
            }
        }
        return false;
    }

    public void start() {
        if(mPlayer_s1 != null) {
            mPlayer_s1.start();
        }
    }

    public boolean isAlive() {
        boolean result = false;
        if(mPlayer_s1 != null) {
            result = mPlayer_s1.isPlaying();
        }
        return result;
    }
}
