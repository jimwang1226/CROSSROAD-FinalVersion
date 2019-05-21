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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;


public class FactSheetActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView lv;
    private Button returnButton;
    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound
    private boolean mIsBound = false;
    private MusicService_S1 mServ;
    private HomeWatcher mHomeWatcher;
    private List<Fact> factList = new ArrayList<Fact>();

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
        setContentView(R.layout.activity_fact_sheet);
        //set up font
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Oh_Whale.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        //Set up full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Get Views and set up views
        lv = (ListView) findViewById(R.id.fact_sheet_lv);
        returnButton = (Button) findViewById(R.id.fact_sheet_return_to_for_parents_button);
        returnButton.setOnClickListener(this);

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

        //init fact
        initFacts();
        FactAdapter adapter = new FactAdapter(FactSheetActivity.this,
                R.layout.list_view_item_layout, factList);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //for testing
                //Toast.makeText(FactSheetActivity.this, "点击的子项ID = " + position, Toast.LENGTH_SHORT).show();
                switch(position){
                    case 3:
                        KnowledgePointPopup popup = new KnowledgePointPopup(FactSheetActivity.this);
                        popup.changeBackground();
                        SoundUtil.playSound(soundID);
                        popup.showPopupWindow();
                        break;
                    case 4:
                        FrontGateKnowledgePointPopup frontGatePopup = new FrontGateKnowledgePointPopup(FactSheetActivity.this);
                        frontGatePopup.changeBackground();
                        SoundUtil.playSound(soundID);
                        frontGatePopup.showPopupWindow();
                        break;
                    case 0:
                        FactSchoolBullySituationPopup schoolBullyPopup = new FactSchoolBullySituationPopup(FactSheetActivity.this);
                        SoundUtil.playSound(soundID);
                        schoolBullyPopup.showPopupWindow();
                        break;
                    case 1:
                        FactSchoolBullyTrendPopup schoolBullyTrendPopup = new FactSchoolBullyTrendPopup(FactSheetActivity.this);
                        SoundUtil.playSound(soundID);
                        schoolBullyTrendPopup.showPopupWindow();
                        break;
                    case 2:
                        FactSpeechDiffPopup speechDiffPopup = new FactSpeechDiffPopup(FactSheetActivity.this);
                        SoundUtil.playSound(soundID);
                        speechDiffPopup.showPopupWindow();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fact_sheet_return_to_for_parents_button:
                System.out.println("click return");
                SoundUtil.playSound(soundID);
                getParents();
                break;
            default:
                break;
        }
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

    //go to main screen page
    public void getParents(){
        Intent intent = new Intent(this, ForParentsActivity.class);
        startActivity(intent);
        this.finish();
    }

    //init facts
    private void initFacts() {
        Fact fact1 = new Fact("School bully, a serious problem in Victoria", R.drawable.school_bully);
        factList.add(fact1);
        Fact fact2 = new Fact("School bully trend from 2006 to 2018", R.drawable.bully_trend);
        factList.add(fact2);
        Fact fact3= new Fact("Speech difficulty situation is severe in Victoria", R.drawable.speech_difficulty);
        factList.add(fact3);
        Fact fact4 = new Fact("Victoria 7 to 9 male and female ratio", R.drawable.male_female_ratio);
        factList.add(fact4);
        Fact fact5 = new Fact("Victoria student enrollment statistics", R.drawable.student_enrollment);
        factList.add(fact5);
    }

}
