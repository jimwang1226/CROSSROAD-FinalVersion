package com.community.protectcommunity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class ForParentsActivity extends AppCompatActivity implements View.OnClickListener{

    private Button fact_btn;
    private Button help_btn;
    private Button parents_back_to_mainscreen_btn;
    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound
    private boolean mIsBound = false;
    private MusicService_S1 mServ;
    private HomeWatcher mHomeWatcher;

    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService_S1.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_parents);
        //Set up full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Get Views and set up views
        fact_btn = (Button) findViewById(R.id.fact_btn);
        help_btn = (Button) findViewById(R.id.help_btn);
        parents_back_to_mainscreen_btn = (Button) findViewById(R.id.for_parents_return_to_mainscreen_button);
        fact_btn.setOnClickListener(this);
        help_btn.setOnClickListener(this);
        parents_back_to_mainscreen_btn.setOnClickListener(this);
        soundID = SoundUtil.initSound(this);

        //bind music service
        mIsBound = SoundUtil.bindMusicService(this, MusicService_S1.class, Scon, mIsBound);

        //HomeWatcher Settings
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }

            @Override
            public void onHomeLongPressed() {
                if (mServ != null){
                    mServ.pauseMusic();
                }
            }
        });

        mHomeWatcher.startWatch();
    }

    public void getHome(){
        Intent intent = new Intent(this, MainScreenActivity.class);
        startActivity(intent);
        this.finish();
    }



    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fact_btn:
                System.out.println("click for parents");
                SoundUtil.playSound(soundID);
                goFactSheetActivity();
                break;
            case R.id.help_btn:
                System.out.println("click for parents");
                SoundUtil.playSound(soundID);
                goHelpActivity();
                break;
            case R.id.for_parents_return_to_mainscreen_button:
                SoundUtil.playSound(soundID);
                mServ.stopMusic();
                SoundUtil.playSound(soundID);
                //might cause exception
                //mHomeWatcher.stopWatch();
                getHome();
                break;
            default:
                break;
        }
    }


    //clear the shared preference when click the start
    //because start means start a new game
    public void clearSharedPreference () {
        SharedPreferences sharedPref = this.getSharedPreferences("username_gender_choice", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.clear();
    }

    public void goFactSheetActivity(){
        Intent intent = new Intent(this, FactSheetActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void goHelpActivity(){
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
        this.finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mHomeWatcher.stopWatch();
        mIsBound = SoundUtil.unbindMusicService(this, MusicService_S1.class, Scon, mIsBound);
        System.gc();
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
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }

    }
}
