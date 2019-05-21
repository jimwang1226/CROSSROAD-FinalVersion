package com.community.protectcommunity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class HelpQuestionOneFragment extends Fragment implements View.OnClickListener{
    private View helpQuestionOneFragment;
    private LinearLayout helpQuestionOneLayout;
    private Button six_to_seven_btn;
    private Button eight_to_nine_btn;
    private Button ten_to_eleven_btn;
    private Button help_back_to_for_parents_screen_btn;

    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound
    public MusicService_S1 mServ;
    private SharedPreferences sharedPref;
    BackToMainScreenPopup popup;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helpQuestionOneFragment = LayoutInflater.from(getActivity()).inflate(R.layout.help_question_one_fragment,
                container, false);
        //Initialize
        soundID = SoundUtil.initSound(this.getActivity());
        setView();

        return helpQuestionOneFragment;
    }

    public void setView () {
        //initialize the view
        six_to_seven_btn = (Button) helpQuestionOneFragment.findViewById(R.id.help_age_six_to_seven_btn);
        eight_to_nine_btn = (Button) helpQuestionOneFragment.findViewById(R.id.help_age_eight_to_nine_btn);
        ten_to_eleven_btn = (Button) helpQuestionOneFragment.findViewById(R.id.help_age_ten_to_eleven_btn);
        help_back_to_for_parents_screen_btn = (Button) helpQuestionOneFragment.findViewById(R.id.help_return_to_for_parents_screen_button);
        helpQuestionOneLayout = (LinearLayout) helpQuestionOneFragment.findViewById(R.id.help_q1_fragment_layout);
        six_to_seven_btn.setOnClickListener(this);
        eight_to_nine_btn.setOnClickListener(this);
        ten_to_eleven_btn.setOnClickListener(this);
        help_back_to_for_parents_screen_btn.setOnClickListener(this);

        //set up the view
        sharedPref = getActivity().getSharedPreferences("help_function",Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View view) {
        Fragment nextFragment;
        nextFragment = new HelpQuestionTwoFragment();
        FragmentManager fragmentManager = getFragmentManager();
        switch (view.getId()) {
            case R.id.help_age_six_to_seven_btn:
                System.out.println("click 6-7");
                //save age group to shared preference
                saveAge("6-7");
                fragmentManager.beginTransaction().replace(R.id.help_change_area, nextFragment).commit();
                SoundUtil.playSound(soundID);
                break;
            case R.id.help_age_eight_to_nine_btn:
                System.out.println("click 8-9");
                //save age group to shared preference
                saveAge("8-9");
                fragmentManager.beginTransaction().replace(R.id.help_change_area, nextFragment).commit();
                SoundUtil.playSound(soundID);
                break;
            case R.id.help_age_ten_to_eleven_btn:
                System.out.println("click 10-11");
                //save age group to shared preference
                saveAge("10-11");
                fragmentManager.beginTransaction().replace(R.id.help_change_area, nextFragment).commit();
                SoundUtil.playSound(soundID);
                break;
            case R.id.help_return_to_for_parents_screen_button:
                SoundUtil.playSound(soundID);
                //might cause exception
                //mHomeWatcher.stopWatch();
                getParents();
                break;
            default:
                break;
        }
    }

    public void getParents(){
        Intent intent = new Intent(this.getContext(), ForParentsActivity.class);
        startActivity(intent);
        this.getActivity().finish();
    }

    public void getHome(){
        Intent intent = new Intent(this.getContext(), MainScreenActivity.class);
        this.getContext().startActivity(intent);
        this.getActivity().finish();
        //this.getContentView()
    }

    //initialize the return pop up window
    public void initPopupLayout() {
        popup = new BackToMainScreenPopup(this.getContext(), getActivity());
        popup.showPopupWindow();
    }

    //save age
    public void saveAge(String ageGroup) {
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.clear();
        spEditor.putString("ageGroup", ageGroup);
        spEditor.apply();
    }


    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    public void setmServ(MusicService_S1 mServ) {
        this.mServ = mServ;
    }
}