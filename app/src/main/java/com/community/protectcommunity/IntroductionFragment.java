package com.community.protectcommunity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class IntroductionFragment extends Fragment implements View.OnClickListener{
    private View introductionFragment;
    private TextView introductionLine;
    private Animation mShowAnimation;
    private LinearLayout introductionLayout;
    private Button backToMainscreenButton;
    private Button nextScreenButton;
    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound
    BackToMainScreenPopup popup;

    public MusicService_S1 mServ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        introductionFragment = LayoutInflater.from(getActivity()).inflate(R.layout.introduction_fragment,
                container, false);
        //Initialize
        setView();
        //set animation
        AnimUtil.setShowAnimation(introductionLayout, 1000, mShowAnimation);

        soundID = SoundUtil.initSound(this.getActivity());

        //save check point
        String fragmentName = this.getClass().getName();
        System.out.println("hey!!!!!!!!!!"+fragmentName);
        GameProgressUtil.saveCheckPoint(this.getActivity(),fragmentName);
        return introductionFragment;
    }

    public void setView () {
        //initialize the view
        introductionLine = (TextView)introductionFragment.findViewById(R.id.introduction_fragement_line);
        introductionLayout = (LinearLayout)introductionFragment.findViewById(R.id.introduction_fragment_layout);
        backToMainscreenButton = (Button)introductionFragment.findViewById(R.id.return_to_mainscreen_button_intro_fragment);
        nextScreenButton = (Button)introductionFragment.findViewById(R.id.next_button_intro_fragment);

        //set up the view
        SharedPreferences sharedPref = getActivity().getSharedPreferences("username_gender_choice",Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        backToMainscreenButton.setOnClickListener(this);
        nextScreenButton.setOnClickListener(this);


        introductionLine.setText(getResources().getString(R.string.introduction_fragment_line_part1) +
                " " +
                username +
                " " +
                getResources().getString(R.string.introduction_fragment_line_part2) +
                " " +
                username +
                " " +
                getResources().getString(R.string.introduction_fragment_line_part3));

    }

    //initialize the pop up window
    public void initPopupLayout() {
        popup = new BackToMainScreenPopup(this.getContext(), getActivity());
        popup.showPopupWindow();
    }

    @Override
    public void onClick(View view) {
        Fragment nextFragment;
        switch (view.getId()) {
            case R.id.return_to_mainscreen_button_intro_fragment:
                SoundUtil.playSound(soundID);
                initPopupLayout();
                // mServ.stopMusic();
                break;
            case R.id.next_button_intro_fragment:
                SoundUtil.playSound(soundID);
                nextFragment = new DrivingFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_in,R.animator.slide_in_opp,R.animator.slide_out_opp,
                        R.animator.slide_out).replace(R.id.game_change_area, nextFragment).commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    public void setmServ(MusicService_S1 mServ) {
        this.mServ = mServ;
    }
}