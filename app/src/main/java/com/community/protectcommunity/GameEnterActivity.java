package com.community.protectcommunity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class GameEnterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText userGameNameContent;
    Animation mShowAnimation;
    RelativeLayout chatBox;
    Button maleButton;
    Button femaleButton;
    Button returnButton;
    Button nextButton;
    RelativeLayout boyGirlChangeArea;
    SharedPreferences sharedPref;
    TextView chatboxContent;
    String gender = "MALE";
    private HomeWatcher mHomeWatcher;
    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound

    private boolean mIsBound = false;
    private MusicService mServ;
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
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/OH_Whale.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        setContentView(R.layout.activity_game_enter);
        //Set up full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Get Views and set up views
        chatBox = (RelativeLayout)findViewById(R.id.game_enter_chat_box);
        maleButton = (Button)findViewById(R.id.male_button);
        femaleButton = (Button)findViewById(R.id.female_button);
        returnButton = (Button)findViewById(R.id.return_to_mainscreen_button);
        nextButton = (Button)findViewById(R.id.next_to_start_game_button);
        boyGirlChangeArea = (RelativeLayout)findViewById(R.id.boy_girl_change_area);
        chatboxContent = (TextView)findViewById(R.id.game_enter_chat_box_content);
        userGameNameContent = (EditText) findViewById(R.id.et_username);
        userGameNameContent.requestFocus();

        maleButton.setOnClickListener(this);
        femaleButton.setOnClickListener(this);
        returnButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        AnimUtil.setShowAnimation(chatBox, 1000, mShowAnimation);
        //returnButton.setVisibility(View.INVISIBLE);
        //nextButton.setVisibility(View.INVISIBLE);

        sharedPref = this.getSharedPreferences("username_gender_choice", Context.MODE_PRIVATE);
        userGameNameContent.setFocusable(true);
        userGameNameContent.setFocusableInTouchMode(true);
        userGameNameContent.requestFocus();

        soundID = SoundUtil.initSound(this);
        //bind music service
        SoundUtil.bindMusicService(this, MusicService.class, Scon, mIsBound);

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
        String username = null;
        switch (view.getId()) {
            case R.id.male_button:
                System.out.println("click male");
                SoundUtil.playSound(soundID);
                Resources res = getResources(); //resource handle
                //change the picture of the character
                Drawable drawable = res.getDrawable(R.drawable.mainscreen_boy, null);
                boyGirlChangeArea.setBackground(drawable);
                gender = "MALE";
                //change the line in the chat box
                username = userGameNameContent.getText().toString().trim();
                if (username.equals("")) {
                    chatboxContent.setText(R.string.game_enter_boy_line);
                    //chatboxContent.setPadding(0,100,0,0);
                    AnimUtil.setShowAnimation(chatBox, 1000, mShowAnimation);
                }
                else {
                    chatboxContent.setText("Hi! " + username);
                    //chatboxContent.setPadding(0,100,0,0);
                    AnimUtil.setShowAnimation(chatBox, 1000, mShowAnimation);
                }
                break;
            case R.id.female_button:
                res = getResources(); //resource handle
                //change the picture of the character
                SoundUtil.playSound(soundID);
                drawable = res.getDrawable(R.drawable.game_enter_girl, null);
                boyGirlChangeArea.setBackground(drawable);
                gender = "FEMALE";
                //change the line in the chat box
                username = userGameNameContent.getText().toString().trim();
                if (username.equals("")) {
                    chatboxContent.setText(R.string.game_enter_girl_line);
                    //chatboxContent.setPadding(0,100,0,0);
                    AnimUtil.setShowAnimation(chatBox, 1000, mShowAnimation);
                }
                else {
                    chatboxContent.setText("Hi! " + username);
                    //chatboxContent.setPadding(0,100,0,0);
                    AnimUtil.setShowAnimation(chatBox, 1000, mShowAnimation);
                }
                System.out.println("click female");
                break;
            case R.id.return_to_mainscreen_button:
                System.out.println("click return");
                SoundUtil.playSound(soundID);
                getHome();
                break;
            case R.id.next_to_start_game_button:
                System.out.println("click next");
                SoundUtil.playSound(soundID);
                //check validation, save username and gender
                if(isUsernameValid()) {
                    saveUsernameGender();
                    mServ.stopMusic();
                    //go to game page
                    getGame();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    //save user's game name and gender to shared preference
    public void saveUsernameGender () {
        String username = userGameNameContent.getText().toString().trim();
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putString("username", username);
        spEditor.putString("gender", gender);
        spEditor.apply();
    }


    //go to game page
    public void getGame(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        this.finish();
    }

    //go to main screen page
    public void getHome(){
        Intent intent = new Intent(this, MainScreenActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHomeWatcher.stopWatch();
        System.gc();
        //Unbind music service
        SoundUtil.unbindMusicService(this, MusicService.class, Scon, mIsBound);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mServ != null){
            mServ.resumeMusic();
        }
    }

    //Check whether the string is just contain number and character
    public static boolean isPureText(String str){
        String format = "^[a-zA-Z0-9]+$";
        Pattern p = Pattern.compile(format);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    //Check the validation of username
    private boolean isUsernameValid() {
        String username = userGameNameContent.getText().toString().trim();
        //Use TextUtils to check is empty or not
        if (TextUtils.isEmpty(username) || !isPureText(username)) {
            Context context = getApplicationContext();
            CharSequence text = getString(R.string.error_invalid_username);
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            //I use requestFocus to get the cursor, because the input is invalid.
            userGameNameContent.requestFocus();
            return false;
        } else {
            return true;
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
