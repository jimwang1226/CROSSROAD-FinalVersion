package com.community.protectcommunity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.SoundPool;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SoundUtil {
    @SuppressLint("NewApi")
    static SoundPool soundPool = new SoundPool.Builder().build();
    public static int initSound(Activity activity) {
        System.out.print(activity);
        int soundID = soundPool.load(activity, R.raw.button_4, 1);
        return soundID;
    }

    public static void playSound(int soundID) {
        soundPool.play(
                soundID,
                1f,
                1f,
                0,
                0,
                1
        );
    }

    public static boolean doBindService(Activity activity,Class service, ServiceConnection Scon, boolean mIsBound){
        activity.bindService(new Intent(activity,service),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        return mIsBound;
    }

    public static boolean doUnbindService(Activity activity, boolean mIsBound, ServiceConnection Scon)
    {
        if(mIsBound)
        {
            activity.unbindService(Scon);
            mIsBound = false;
        }
        return mIsBound;
    }

    public static void resumePlay(MusicService mServ) {
        if (mServ != null){
            mServ.resumeMusic();
        }
    }

    //call in onCreate to bind music
    public static boolean bindMusicService(final Activity activity,final Class service, ServiceConnection Scon, boolean mIsBound) {
        mIsBound = SoundUtil.doBindService(activity, service, Scon, mIsBound);
        //Bind music service
        final Intent music = new Intent();
        music.setClass(activity,service);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.startService(music);
            }
        },2500);
        return mIsBound;
    }

    //call in onDestroy to unbind music
    public static boolean unbindMusicService(Activity activity, Class service, ServiceConnection Scon, boolean mIsBound){
         mIsBound = doUnbindService(activity, mIsBound, Scon);
         Intent music = new Intent();
         music.setClass(activity,service);
         activity.stopService(music);
         return mIsBound;
    }


}