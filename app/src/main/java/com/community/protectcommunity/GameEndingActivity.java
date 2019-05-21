package com.community.protectcommunity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

/**
 * Created by jingewang on 28/3/19.
 */

public class GameEndingActivity extends Activity implements View.OnClickListener{
    //Execute when the page is created
    Button backToMainScreenButton;
    View score;
    VideoView endingVideo;
    RelativeLayout endingActivityLayout;
    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound
    private int finalScoreInt = 0;
    private HomeWatcher mHomeWatcher;

    private boolean mIsBound = false;
    private MediaController mediaController;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ending);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        backToMainScreenButton = (Button)findViewById(R.id.return_to_mainscreen_button_ending_activity);
        endingActivityLayout = (RelativeLayout)findViewById(R.id.ending_activity_layout);
        endingVideo = (VideoView)findViewById(R.id.ending_video);
        score = findViewById(R.id.ending_score);
        //get the width and height of the screen
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        score.setX((int)(dm.widthPixels * 0.34));

        mediaController = new MediaController(this);

        SoundUtil.initSound(this);

        //calculate the score from shared preference
        SharedPreferences sharedPref = getSharedPreferences("username_gender_choice", Context.MODE_PRIVATE);
        String question1 = sharedPref.getString("question1", null);
        String question2 = sharedPref.getString("question2", null);
        String question3 = sharedPref.getString("question3", null);
        String question4 = sharedPref.getString("question4", null);
        //I wrote it in a reverse order, so yes will plus 50 points
        if ("YES".equals(question1)) {
            finalScoreInt += 25;
        }
        if ("NO".equals(question2)) {
            finalScoreInt += 25;
        }
        if ("YES".equals(question3) || "NO".equals(question3)) {
            finalScoreInt += 25;
        }
        if ("YES".equals(question4)) {
            finalScoreInt += 25;
        }

        System.out.println(finalScoreInt);
        Drawable drawable;
        Resources res = getResources();
        if(finalScoreInt == 100) {
            //set the score view background to 100
            drawable = res.getDrawable(R.drawable.number_100, null);
            score.setBackground(drawable);
            drawable = res.getDrawable(R.drawable.ending_activity_background, null);
            endingActivityLayout.setBackground(drawable);
        }
        if(finalScoreInt == 75) {
            //set the score view background to 75
            drawable = res.getDrawable(R.drawable.number_75, null);
            score.setBackground(drawable);
            drawable = res.getDrawable(R.drawable.ending_activity_background, null);
            endingActivityLayout.setBackground(drawable);
        }
        if(finalScoreInt == 50) {
            //set the score view background to 50
            drawable = res.getDrawable(R.drawable.number_50, null);
            score.setBackground(drawable);
            drawable = res.getDrawable(R.drawable.ending_activity_background, null);
            endingActivityLayout.setBackground(drawable);
        }
        if(finalScoreInt == 25) {
            //set the score view background to 25
            drawable = res.getDrawable(R.drawable.number_25, null);
            score.setBackground(drawable);
            drawable = res.getDrawable(R.drawable.ending_activity_background, null);
            endingActivityLayout.setBackground(drawable);
        }
        if(finalScoreInt == 0) {
            //set the score view background to 0
            drawable = res.getDrawable(R.drawable.number_0, null);
            score.setBackground(drawable);
            drawable = res.getDrawable(R.drawable.ending_activity_background, null);
            endingActivityLayout.setBackground(drawable);
        }

        //set up and start animation
        ObjectAnimator scoreViewAnimOn = AnimUtil.getAnimatorOn(score, this);
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scoreViewAnimOn);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatorSet.start();
            }
        });
        backToMainScreenButton.setOnClickListener(this);

        //HomeWatcher Settings
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
            }

            @Override
            public void onHomeLongPressed() {
            }
        });

        mHomeWatcher.startWatch();
        videoPlay();
    }

    public void getHome(){
        Intent intent = new Intent(this, MainScreenActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_to_mainscreen_button_ending_activity:
                SoundUtil.playSound(soundID);
                GameProgressUtil.setGameProgressToNull(this);
                SoundUtil.playSound(soundID);
                //might cause exception
                //mHomeWatcher.stopWatch();
                getHome();
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }

        if (!isScreenOn) {
        }

    }

    public void videoPlay() {
        String videoPath;
        if (finalScoreInt >= 75) {
            videoPath = "android.resource://com.community.protectcommunity/" + R.raw.high_score_video;
        } else {
            videoPath = "android.resource://com.community.protectcommunity/" + R.raw.low_score_video;
        }
        Uri uri = Uri.parse(videoPath);
        endingVideo.setVideoURI(uri);
        endingVideo.setMediaController(mediaController);
        mediaController.setAnchorView(endingVideo);
        MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener(){

            @Override
            public void onPrepared(MediaPlayer m) {
                try {
                    if (m.isPlaying()) {
                        m.stop();
                        m.release();
                        m = new MediaPlayer();
                    }
                    m.setVolume(100f, 100f);
                    m.setLooping(false);
                    m.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        endingVideo.setOnPreparedListener(preparedListener);

        endingVideo.start();
    }
}
