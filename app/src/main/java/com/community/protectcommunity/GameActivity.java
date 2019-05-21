package com.community.protectcommunity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPref;
    String gender;

    private boolean mIsBound = false;
    public MusicService_S1 mServ;
    private ServiceConnection Scon;
    private HomeWatcher mHomeWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up font
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Oh_Whale.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        setContentView(R.layout.activity_game);
        //Set up full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Get Views and set up views
        sharedPref = this.getSharedPreferences("username_gender_choice", Context.MODE_PRIVATE);
        System.out.println(sharedPref.getString("username",null));
        System.out.println(sharedPref.getString("gender",null));
        Scon = new ServiceConnection(){

            public void onServiceConnected(ComponentName name, IBinder
                    binder) {
                mServ = ((MusicService_S1.ServiceBinder)binder).getService();
            }

            public void onServiceDisconnected(ComponentName name) {
                mServ = null;
            }
        };

        //Switch fragment to introduction fragment
        //or if click the continue button, switch to corresponding fragment
        Fragment nextFragment = new IntroductionFragment();
        FragmentManager fragmentManager = getFragmentManager();
        SharedPreferences sharedPref = this.getSharedPreferences("username_gender_choice", Context.MODE_PRIVATE);
        boolean isContinueClicked = sharedPref.getBoolean("clickContinue", false);
        System.out.println("isContinueClicked" + isContinueClicked);
        if (isContinueClicked) {
            GameProgressUtil.changeToFragment(this);
        }
        else {
            ((IntroductionFragment) nextFragment).setmServ(mServ);
            fragmentManager.beginTransaction().replace(R.id.game_change_area, nextFragment).commit();
        }

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

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unbind music service
        mIsBound = SoundUtil.unbindMusicService(this, MusicService_S1.class, Scon,  mIsBound);
        System.gc();
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (mServ != null){
            mServ.resumeMusic();
        }
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