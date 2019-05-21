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


public class MainScreenActivity extends AppCompatActivity implements View.OnClickListener, View.OnHoverListener{

    private Button start_btn;
    private Button more_btn;
    private Button continue_btn;
    private Button for_parents_btn;
    private Button what_is_bully_btn;
    private Button exit_btn;
    private MainScreenPopup popup;
    private FirebaseAuth mAuth;
    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound
    private boolean mIsBound = false;
    private MusicService mServ;
    private HomeWatcher mHomeWatcher;
    public Boolean isButtonClicked = false;

    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);
        //bind music service
        mIsBound = SoundUtil.bindMusicService(this, MusicService.class, Scon, mIsBound);

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

        //Set up full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Get Views and set up views
        start_btn = (Button) findViewById(R.id.start_btn);
        more_btn = (Button) findViewById(R.id.more_btn);
        continue_btn = (Button) findViewById(R.id.continue_btn);
        for_parents_btn = (Button) findViewById(R.id.for_parents_btn);
        what_is_bully_btn = (Button) findViewById(R.id.mini_game_btn);
        exit_btn = (Button) findViewById(R.id.exit_application_btn);
        start_btn.setOnClickListener(this);
        start_btn.setOnHoverListener(this);
        more_btn.setOnClickListener(this);
        for_parents_btn.setOnClickListener(this);
        what_is_bully_btn.setOnClickListener(this);
        more_btn.setOnHoverListener(this);
        continue_btn.setOnClickListener(this);
        exit_btn.setOnClickListener(this);
        soundID = SoundUtil.initSound(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        System.out.println("failed");
                    }
                });

        //just for testing!!!! Set the game progress to null
        //GameProgressUtil.setGameProgressToNull(this);

        //check if there is a check point exist
        String gameProgress = GameProgressUtil.getGameProgress(this);
        if (gameProgress == null) {
            continue_btn.setBackground(getResources().getDrawable(R.drawable.continue_button_grey_v2,null));
            continue_btn.setEnabled(false);
        }
        else{
            System.out.println("gameProgress!!!!!" + gameProgress);
            continue_btn.setBackground(getResources().getDrawable(R.drawable.continue_button_background,null));
            continue_btn.setEnabled(true);
        }

        //set click continue button to false
        SharedPreferences sharedPref = this.getSharedPreferences("username_gender_choice", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putBoolean("clickContinue", false);
        spEditor.apply();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_btn:
                if (isButtonClicked) {
                    break;
                }
                else {
                    System.out.println("click start");
                    SoundUtil.playSound(soundID);
                    //clearSharedPreference();
                    goGameEnterScreen();
                    mHomeWatcher.stopWatch();
                    isButtonClicked = true;
                }
                break;
            case R.id.more_btn:
                System.out.println("click more");
                SoundUtil.playSound(soundID);
                initPopupLayout();
                break;
            case R.id.continue_btn:
                if (isButtonClicked) {
                    break;
                }
                else {
                    System.out.println("click continue");
                    SharedPreferences sharedPref = this.getSharedPreferences("username_gender_choice", Context.MODE_PRIVATE);
                    SharedPreferences.Editor spEditor = sharedPref.edit();
                    SoundUtil.playSound(soundID);
                    spEditor.putBoolean("clickContinue", true);
                    spEditor.apply();
                    goGameActivity();
                    //mHomeWatcher.stopWatch();
                    isButtonClicked = true;
                }
                break;
            case R.id.for_parents_btn:
                if (isButtonClicked) {
                    break;
                }
                else {
                    System.out.println("click for parents");
                    SoundUtil.playSound(soundID);
                    goForParentsActivity();
                    mHomeWatcher.stopWatch();
                    isButtonClicked = true;
                }
                break;
            case R.id.mini_game_btn:
                if (isButtonClicked) {
                    break;
                }
                else {
                    System.out.println("click mini game");
                    SoundUtil.playSound(soundID);
                    goMiniGameActivity();
                    mHomeWatcher.stopWatch();
                    isButtonClicked = true;
                }
                break;
            case R.id.exit_application_btn:
                if (isButtonClicked) {
                    break;
                }
                else {
                    System.out.println("click exit game");
                    SoundUtil.playSound(soundID);
                    //show the exit application popup
                    ExitAppPopup exitAppPopup = new ExitAppPopup(this, this);
                    exitAppPopup.showPopupWindow();
                }
                break;
            default:
                break;
        }
    }


    //clear the shared preference when click the start
    //because start means start a new game
    public void onButtonClicked () {
        SharedPreferences sharedPref = this.getSharedPreferences("is_main_screen_button_clicked", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.clear();
    }

    @Override
    public boolean onHover(View view, MotionEvent event) {
        return false;
    }

    public void initPopupLayout() {
        popup = new MainScreenPopup(MainScreenActivity.this);
        //popup.setBackground(getDrawable(R.drawable.mainscreen_popup_window_background));
        popup.showPopupWindow();
    }

    public void goGameEnterScreen(){
        Intent intent = new Intent(this, GameEnterActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void goGameActivity(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void goForParentsActivity(){
        Intent intent = new Intent(this, ForParentsActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void goMiniGameActivity(){
        Intent intent = new Intent(this, MiniGameActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsBound = SoundUtil.unbindMusicService(this, MusicService.class, Scon, mIsBound);
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

    public void changeIsButtonClicked() {
        isButtonClicked = false;
    }
}
