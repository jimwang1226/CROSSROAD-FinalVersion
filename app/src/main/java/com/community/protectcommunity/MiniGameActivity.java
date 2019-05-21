package com.community.protectcommunity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MiniGameActivity extends AppCompatActivity implements View.OnClickListener{

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
        setContentView(R.layout.activity_mini_game);
        //Set up full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Get Views and set up views
        lv = (ListView) findViewById(R.id.mini_game_lv);
        returnButton = (Button) findViewById(R.id.mini_game_return_to_main_screen_button);
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
        FactAdapter adapter = new FactAdapter(MiniGameActivity.this,
                R.layout.list_view_item_layout, factList);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //for testing
                //Toast.makeText(FactSheetActivity.this, "ID = " + position, Toast.LENGTH_SHORT).show();
                switch(position){
                    case 0:
                        getLineGame();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mini_game_return_to_main_screen_button:
                System.out.println("click return");
                SoundUtil.playSound(soundID);
                getMainScreen();
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
    public void getMainScreen(){
        Intent intent = new Intent(this, MainScreenActivity.class);
        startActivity(intent);
        this.finish();
    }

    //go to what is bully game
    public void getLineGame(){
        Intent intent = new Intent(this, LineConnectionGameActivity.class);
        startActivity(intent);
        this.finish();
    }

    //init facts
    private void initFacts() {
        Fact fact1 = new Fact("What is a bully?", R.drawable.line_conn_task_one_pic_1_normal);
        factList.add(fact1);
    }


}
