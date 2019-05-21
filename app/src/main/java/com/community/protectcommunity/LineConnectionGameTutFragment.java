package com.community.protectcommunity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
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

import java.util.Timer;
import java.util.TimerTask;

public class LineConnectionGameTutFragment extends Fragment implements View.OnClickListener{
    private View lineConnTutFragment;
    private View textOne;
    private View textTwo;
    private View textThree;
    private View picOne;
    private View picTwo;
    private View picThree;
    private View exampleLine;
    private View textOneClicked;
    private View picTwoClicked;
    private View explainOne;
    private View explainTwo;
    private View explainThree;
    private View explainFour;
    private View explainFive;
    private Button skipButton;
    private AnimatorSet animatorSet = null;
    private Thread uiThread;

    public MusicService_S1 mServ;
    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lineConnTutFragment = LayoutInflater.from(getActivity()).inflate(R.layout.line_conn_tut_fragment,
                container, false);
        //Initialize
        setView();
        soundID = SoundUtil.initSound(this.getActivity());
        //set animation
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startTutorialThread();
            }
        },500);
        return lineConnTutFragment;
    }

    public void setView () {
        //initialize the view
        textOne = lineConnTutFragment.findViewById(R.id.line_conn_tut_text_one);
        textTwo = lineConnTutFragment.findViewById(R.id.line_conn_tut_text_two);
        textThree = lineConnTutFragment.findViewById(R.id.line_conn_tut_text_three);
        picOne = lineConnTutFragment.findViewById(R.id.line_conn_tut_pic_one);
        picTwo = lineConnTutFragment.findViewById(R.id.line_conn_tut_pic_two);
        picThree = lineConnTutFragment.findViewById(R.id.line_conn_tut_pic_three);
        exampleLine = lineConnTutFragment.findViewById(R.id.line_conn_tut_example_line);
        textOneClicked = lineConnTutFragment.findViewById(R.id.line_conn_tut_text_one_clicked);
        picTwoClicked = lineConnTutFragment.findViewById(R.id.line_conn_tut_pic_two_clicked);
        explainOne = lineConnTutFragment.findViewById(R.id.line_conn_game_tut_story_chat_box_one);
        explainTwo = lineConnTutFragment.findViewById(R.id.line_conn_game_tut_story_chat_box_two);
        explainThree = lineConnTutFragment.findViewById(R.id.line_conn_game_tut_story_chat_box_three);
        explainFour = lineConnTutFragment.findViewById(R.id.line_conn_game_tut_story_chat_box_four);
        explainFive = lineConnTutFragment.findViewById(R.id.line_conn_game_tut_story_chat_box_five);
        skipButton = (Button)lineConnTutFragment.findViewById(R.id.skip_button_line_conn_tut_fragment);
        skipButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skip_button_line_conn_tut_fragment:
                SoundUtil.playSound(soundID);
                animatorSet.end();
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

    public void startTutorialThread() {
        if (getActivity() == null) {
            return;
        }

        //animation on
        ObjectAnimator exampleLineAnimOn = AnimUtil.getAnimatorOn(exampleLine, getActivity());
        ObjectAnimator textOneClickedAnimOn = AnimUtil.getAnimatorOn(textOneClicked, getActivity());
        ObjectAnimator picTwoClickedAnimOn = AnimUtil.getAnimatorOn(picTwoClicked, getActivity());
        ObjectAnimator explainOneAnimOn = AnimUtil.getAnimatorOn(explainOne, getActivity());
        ObjectAnimator explainTwoAnimOn = AnimUtil.getAnimatorOn(explainTwo, getActivity());
        ObjectAnimator explainThreeAnimOn = AnimUtil.getAnimatorOn(explainThree, getActivity());
        ObjectAnimator explainFourAnimOn = AnimUtil.getAnimatorOn(explainFour, getActivity());
        ObjectAnimator explainFiveAnimOn = AnimUtil.getAnimatorOn(explainFive, getActivity());


        //animation off
        ObjectAnimator textOneAnimOff = AnimUtil.getAnimatorOff(textOne, getActivity());
        ObjectAnimator picTwoAnimOff = AnimUtil.getAnimatorOff(textOne, getActivity());
        ObjectAnimator explainOneAnimOff = AnimUtil.getAnimatorOff(explainOne, getActivity());
        ObjectAnimator explainTwoAnimOff = AnimUtil.getAnimatorOff(explainTwo, getActivity());
        ObjectAnimator explainThreeAnimOff = AnimUtil.getAnimatorOff(explainThree, getActivity());
        ObjectAnimator explainFourAnimOff = AnimUtil.getAnimatorOff(explainFour, getActivity());
        ObjectAnimator explainFiveAnimOff = AnimUtil.getAnimatorOff(explainFive, getActivity());

        //Null animator for gap
        ValueAnimator nullAnimator1 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator2 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator3 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator4 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator5 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator6 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator7 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator8 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator9 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator10 = AnimUtil.getNullAnimator();

        //setup animation
        animatorSet = new AnimatorSet();
        animatorSet.play(explainOneAnimOn);
        nullAnimator1.setDuration(3000);
        animatorSet.play(nullAnimator1).after(explainOneAnimOn);
        animatorSet.play(explainOneAnimOff).after(nullAnimator1);

        animatorSet.play(explainTwoAnimOn).with(explainOneAnimOff);
        nullAnimator2.setDuration(3000);
        animatorSet.play(nullAnimator2).after(explainTwoAnimOn);
        animatorSet.play(explainTwoAnimOff).after(nullAnimator2);

        animatorSet.play(textOneAnimOff).after(explainTwoAnimOff);
        animatorSet.play(textOneClickedAnimOn).with(textOneAnimOff);
        nullAnimator7.setDuration(1000);
        animatorSet.play(nullAnimator7).after(textOneClickedAnimOn);

        animatorSet.play(explainThreeAnimOn).after(nullAnimator7);
        nullAnimator3.setDuration(3000);
        animatorSet.play(nullAnimator3).after(explainThreeAnimOn);
        animatorSet.play(explainThreeAnimOff).after(nullAnimator3);

        animatorSet.play(picTwoAnimOff).after(explainThreeAnimOff);
        animatorSet.play(picTwoClickedAnimOn).with(picTwoAnimOff);
        nullAnimator8.setDuration(1000);
        animatorSet.play(nullAnimator8).after(picTwoClickedAnimOn);


        animatorSet.play(explainFourAnimOn).after(nullAnimator8);
        nullAnimator4.setDuration(3000);
        animatorSet.play(nullAnimator4).after(explainFourAnimOn);
        animatorSet.play(explainFourAnimOff).after(nullAnimator4);

        animatorSet.play(exampleLineAnimOn).after(explainFourAnimOff);
        animatorSet.play(nullAnimator5).after(exampleLineAnimOn);

        animatorSet.play(explainFiveAnimOn).after(nullAnimator5);
        nullAnimator6.setDuration(3000);
        animatorSet.play(nullAnimator6).after(explainFiveAnimOn);
        animatorSet.play(explainFiveAnimOff).after(nullAnimator6);

        uiThread = new Thread() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animatorSet.start();
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                //go to the first task fragment
                                Fragment nextFragment = new LineConnectionGameTaskOneFragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                ((LineConnectionGameTaskOneFragment) nextFragment).setmServ(mServ);
                                fragmentManager.beginTransaction().replace(R.id.line_conn_game_change_area, nextFragment).commit();
                            }
                        });
                    }
                });
            }
        };
        uiThread.start();

    }
}