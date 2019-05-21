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

public class HelpQuestionTwoFragment extends Fragment implements View.OnClickListener{
    private View helpQuestionTwoFragment;
    private LinearLayout helpQuestionTwoLayout;
    private Button yesButton;
    private Button backToParentsButton;
    private Button noButton;

    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound
    private SharedPreferences sharedPref;

    BackToMainScreenPopup popup;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helpQuestionTwoFragment = LayoutInflater.from(getActivity()).inflate(R.layout.help_question_two_fragment,
                container, false);
        //Initialize
        soundID = SoundUtil.initSound(this.getActivity());
        setView();

        return helpQuestionTwoFragment;
    }

    public void setView () {
        //initialize the view
        yesButton = (Button)helpQuestionTwoFragment.findViewById(R.id.help_q2_yes_button);
        noButton = (Button)helpQuestionTwoFragment.findViewById(R.id.help_q2_no_button);
        backToParentsButton = (Button)helpQuestionTwoFragment.findViewById(R.id.help_return_to_for_parents_screen_button);
        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);
        backToParentsButton.setOnClickListener(this);

        //set up the view
        sharedPref = getActivity().getSharedPreferences("help_function",Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View view) {
        Fragment nextFragment;
        FragmentManager fragmentManager = getFragmentManager();
        nextFragment = new HelpQuestionThreeFragment();
        switch (view.getId()) {
            case R.id.help_q2_yes_button:
                SoundUtil.playSound(soundID);
                fragmentManager.beginTransaction().replace(R.id.help_change_area, nextFragment).commit();
                saveQuesTwo("Yes");
                break;
            case R.id.help_q2_no_button:
                SoundUtil.playSound(soundID);
                fragmentManager.beginTransaction().replace(R.id.help_change_area, nextFragment).commit();
                saveQuesTwo("No");
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

    //save question two
    public void saveQuesTwo(String yesOrNo) {
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putString("questionTwo", yesOrNo);
        spEditor.apply();
    }

    //initialize the return pop up window
    public void initPopupLayout() {
        popup = new BackToMainScreenPopup(this.getContext(), getActivity());
        popup.showPopupWindow();
    }

    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}