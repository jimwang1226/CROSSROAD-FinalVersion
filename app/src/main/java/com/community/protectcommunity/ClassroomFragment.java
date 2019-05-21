package com.community.protectcommunity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class ClassroomFragment extends Fragment implements View.OnClickListener{
    private View classroomFragment;
    private View threeCharactersOutside;
    private View threeCharactersInside;
    private View masonChatboxOne;
    private View masonChatboxTwo;
    private View userChatboxOne;
    private View storyChatboxOne;
    private View pencilCaseOne;
    private View pencilCaseTwo;
    private View gracieChatboxOne;
    private View jackChatboxOne;
    private View masonChatboxThree;
    private View storyChatboxTwo;
    private View gracieUnhappy;

    private Button nextButton;
    private Button backToMainscreenButton;
    private Button playAgainButton;
    private Button skipButton;
    //private Button knowledgePointButton;

    private String gender;
    private Boolean isFirstTime = true;

    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound

    BackToMainScreenPopup popup;
    ClassroomPopup classroomPopup;
    KnowledgePointPopup knowledgePointPopup;
    private AnimatorSet animatorSet = null;
    private Thread uiThread;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        classroomFragment = LayoutInflater.from(getActivity()).inflate(R.layout.classroom_fragment,
                container, false);
        //Initialize
        soundID = SoundUtil.initSound(this.getActivity());
        setView();

        //save to check point
        String fragmentName = this.getClass().getName();
        GameProgressUtil.saveCheckPoint(this.getActivity(), fragmentName);
        return classroomFragment;
    }

    public void setView () {
        //initialize the view
        // classroomFragment.findViewById(R.id.front_gate_fragment_principle_chat_box_one);
        threeCharactersOutside = classroomFragment.findViewById(R.id.classroom_fragment_three_characters_boy_outside_view);
        threeCharactersInside = classroomFragment.findViewById(R.id.classroom_fragment_three_characters_boy_inside_view);
        masonChatboxOne = classroomFragment.findViewById(R.id.classroom_fragment_mason_chat_box_one);
        masonChatboxTwo = classroomFragment.findViewById(R.id.classroom_fragment_mason_chat_box_two);
        userChatboxOne = classroomFragment.findViewById(R.id.classroom_fragment_user_chat_box_one);
        storyChatboxOne = classroomFragment.findViewById(R.id.classroom_fragment_story_chat_box_one);
        pencilCaseOne = classroomFragment.findViewById(R.id.classroom_fragment_pencil_one);
        pencilCaseTwo = classroomFragment.findViewById(R.id.classroom_fragment_pencil_two);
        gracieChatboxOne = classroomFragment.findViewById(R.id.classroom_fragment_gracie_chat_box_one);
        jackChatboxOne = classroomFragment.findViewById(R.id.classroom_fragment_jack_chat_box_one);
        masonChatboxThree = classroomFragment.findViewById(R.id.classroom_fragment_mason_chat_box_three);
        storyChatboxTwo = classroomFragment.findViewById(R.id.classroom_fragment_story_chat_box_two);
        gracieUnhappy = classroomFragment.findViewById(R.id.classroom_fragment_gracie_inside_unhappy_view);

        /**
        //get the width and height of the screen
        DisplayMetrics dm = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        threeCharactersOutside.setX((int)(dm.widthPixels * 0.66));
        threeCharactersOutside.setY((int)(dm.heightPixels * 0.66));
        threeCharactersOutside.setVisibility(View.VISIBLE);
         */

        nextButton = (Button)classroomFragment.findViewById(R.id.next_button_classroom_fragment);
        backToMainscreenButton = (Button)classroomFragment.findViewById(R.id.return_to_mainscreen_button_classroom_fragment);
        playAgainButton = (Button)classroomFragment.findViewById(R.id.play_again_button_classroom_fragment);
        skipButton = (Button)classroomFragment.findViewById(R.id.skip_button_classroom_fragment);
        //knowledgePointButton = (Button)classroomFragment.findViewById(R.id.knowledge_point_button_classroom_fragment);

        //set up the view
        SharedPreferences sharedPref = getActivity().getSharedPreferences("username_gender_choice",Context.MODE_PRIVATE);
        gender = sharedPref.getString("gender", null);

        if ("MALE".equals(gender)) {
            threeCharactersOutside.setBackground(getResources().getDrawable(R.drawable.three_characters_boy_outside_classroom,null));
            threeCharactersInside.setBackground(getResources().getDrawable(R.drawable.three_character_boy_inside_classroom,null));
        }
        else {
            threeCharactersOutside.setBackground(getResources().getDrawable(R.drawable.three_characters_girl_outside_classroom,null));
            threeCharactersInside.setBackground(getResources().getDrawable(R.drawable.three_characters_girl_inside_classroom,null));
        }

        nextButton.setOnClickListener(this);
        backToMainscreenButton.setOnClickListener(this);
        playAgainButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);
        //knowledgePointButton.setOnClickListener(this);

        //start the storyline, delay play it because there is a screen switch animation
        //otherwise it would stuck

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startStorylineThread();
            }
        },500);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_button_classroom_fragment:
                SoundUtil.playSound(soundID);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                initQuestionPopupLayout();
                break;
            case R.id.return_to_mainscreen_button_classroom_fragment:
                SoundUtil.playSound(soundID);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                initPopupLayout();
                break;
            case R.id.play_again_button_classroom_fragment:
                SoundUtil.playSound(soundID);
                //final AnimatorSet animatorSetCopy = animatorSet;
                pencilCaseOne.setVisibility(View.VISIBLE);
                uiThread = new Thread() {
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                animatorSet.start();
                            }
                        });
                    }
                };
                uiThread.start();
                break;
            case R.id.skip_button_classroom_fragment:
                SoundUtil.playSound(soundID);
                animatorSet.end();
                break;
            default:
                break;
        }
    }

    public void startStorylineThread() {
        if (getActivity() == null) {
            return;
        }

        //chat box on
        ObjectAnimator masonChatboxOneAnimOn = AnimUtil.getAnimatorOn(masonChatboxOne, getActivity());
        ObjectAnimator masonChatboxTwoAnimOn = AnimUtil.getAnimatorOn(masonChatboxTwo, getActivity());
        ObjectAnimator masonChatboxThreeAnimOn = AnimUtil.getAnimatorOn(masonChatboxThree, getActivity());
        ObjectAnimator userChatboxOneAnimOn = AnimUtil.getAnimatorOn(userChatboxOne, getActivity());
        ObjectAnimator gracieChatboxOneAnimOn = AnimUtil.getAnimatorOn(gracieChatboxOne, getActivity());
        ObjectAnimator jackChatboxOneAnimOn = AnimUtil.getAnimatorOn(jackChatboxOne, getActivity());
        ObjectAnimator storyChatboxOneAnimOn = AnimUtil.getAnimatorOn(storyChatboxOne, getActivity());
        ObjectAnimator storyChatboxTwoAnimOn = AnimUtil.getAnimatorOn(storyChatboxTwo, getActivity());

        //chat box off
        ObjectAnimator masonChatboxOneAnimOff = AnimUtil.getAnimatorOff(masonChatboxOne, getActivity());
        ObjectAnimator masonChatboxTwoAnimOff = AnimUtil.getAnimatorOff(masonChatboxTwo, getActivity());
        final ObjectAnimator masonChatboxThreeAnimOff = AnimUtil.getAnimatorOff(masonChatboxThree, getActivity());
        ObjectAnimator userChatboxOneAnimOff = AnimUtil.getAnimatorOff(userChatboxOne, getActivity());
        ObjectAnimator gracieChatboxOneAnimOff = AnimUtil.getAnimatorOff(gracieChatboxOne, getActivity());
        ObjectAnimator jackChatboxOneAnimOff = AnimUtil.getAnimatorOff(jackChatboxOne, getActivity());
        ObjectAnimator storyChatboxOneAnimOff = AnimUtil.getAnimatorOff(storyChatboxOne, getActivity());
        ObjectAnimator storyChatboxTwoAnimOff = AnimUtil.getAnimatorOff(storyChatboxTwo, getActivity());

        //character & object view on
        ObjectAnimator threeCharactersOutsideAnimOn = AnimUtil.getAnimatorOn(threeCharactersOutside, getActivity());
        ObjectAnimator threeCharactersInsideAnimOn = AnimUtil.getAnimatorOn(threeCharactersInside, getActivity());
        ObjectAnimator pencilCaseOneAnimOn = AnimUtil.getAnimatorOn(pencilCaseOne, getActivity());
        ObjectAnimator pencilCaseTwoAnimOn = AnimUtil.getAnimatorOn(pencilCaseTwo, getActivity());

        //character & object view off
        ObjectAnimator threeCharactersOutsideAnimOff = AnimUtil.getAnimatorOff(threeCharactersOutside, getActivity());
        ObjectAnimator threeCharactersInsideAnimOff = AnimUtil.getAnimatorOff(threeCharactersInside, getActivity());
        ObjectAnimator pencilCaseOneAnimOff = AnimUtil.getAnimatorOff(pencilCaseOne, getActivity());
        ObjectAnimator pencilCaseTwoAnimOff = AnimUtil.getAnimatorOff(pencilCaseTwo, getActivity());

        //play again button on
        ObjectAnimator playAgainButtonOn = AnimUtil.getPlayAgainAnimatorOn(playAgainButton, getActivity());

        //play again button off
        ObjectAnimator playAgainButtonOff = AnimUtil.getPlayAgainAnimatorOff(playAgainButton, getActivity());

        //next button on
        final ObjectAnimator nextButtonOn = AnimUtil.getPlayAgainAnimatorOn(nextButton, getActivity());

        //gracie unhappy on
        ObjectAnimator gracieUnhappyOn = AnimUtil.getPlayAgainAnimatorOn(gracieUnhappy, getActivity());

        //knowledge point button on
        //final ObjectAnimator knowledgePointButtonOn = AnimUtil.getPlayAgainAnimatorOn(knowledgePointButton, getActivity());

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
        if (playAgainButton.getVisibility() == View.VISIBLE) {
            animatorSet.play(playAgainButtonOff);
            animatorSet.play(threeCharactersOutsideAnimOn).after(playAgainButtonOff);
        }
        else {
            animatorSet.play(threeCharactersInsideAnimOn);
        }

        animatorSet.play(masonChatboxOneAnimOn).after(threeCharactersOutsideAnimOn);
        animatorSet.play(nullAnimator1).after(masonChatboxOneAnimOn);
        animatorSet.play(masonChatboxOneAnimOff).after(nullAnimator1);

        animatorSet.play(masonChatboxTwoAnimOn).with(masonChatboxOneAnimOff);
        animatorSet.play(nullAnimator2).after(masonChatboxTwoAnimOn);
        animatorSet.play(masonChatboxTwoAnimOff).after(nullAnimator2);

        animatorSet.play(userChatboxOneAnimOn).with(masonChatboxTwoAnimOff);
        animatorSet.play(nullAnimator3).after(userChatboxOneAnimOn);
        animatorSet.play(userChatboxOneAnimOff).after(nullAnimator3);

        animatorSet.play(threeCharactersOutsideAnimOff).after(userChatboxOneAnimOff);
        animatorSet.play(storyChatboxOneAnimOn).after(threeCharactersOutsideAnimOff);
        nullAnimator4.setDuration(3000);
        animatorSet.play(nullAnimator4).after(storyChatboxOneAnimOn);
        animatorSet.play(storyChatboxOneAnimOff).after(nullAnimator4);

        animatorSet.play(pencilCaseOneAnimOff).after(storyChatboxOneAnimOff);
        animatorSet.play(pencilCaseTwoAnimOn).after(pencilCaseOneAnimOff);
        nullAnimator5.setDuration(500);
        animatorSet.play(nullAnimator5).after(pencilCaseTwoAnimOn);
        animatorSet.play(pencilCaseTwoAnimOff).after(nullAnimator5);

        animatorSet.play(gracieUnhappyOn).after(pencilCaseTwoAnimOff);
        animatorSet.play(threeCharactersInsideAnimOn).after(gracieUnhappyOn);
        animatorSet.play(gracieChatboxOneAnimOn).after(threeCharactersInsideAnimOn);
        animatorSet.play(nullAnimator6).after(gracieChatboxOneAnimOn);
        animatorSet.play(gracieChatboxOneAnimOff).after(nullAnimator6);

        animatorSet.play(jackChatboxOneAnimOn).with(gracieChatboxOneAnimOff);
        animatorSet.play(nullAnimator7).after(jackChatboxOneAnimOn);
        animatorSet.play(jackChatboxOneAnimOff).after(nullAnimator7);

        animatorSet.play(masonChatboxThreeAnimOn).with(jackChatboxOneAnimOff);
        animatorSet.play(nullAnimator8).after(masonChatboxThreeAnimOn);
        animatorSet.play(masonChatboxThreeAnimOff).after(nullAnimator8);

        animatorSet.play(storyChatboxTwoAnimOn).with(masonChatboxThreeAnimOff);
        nullAnimator9.setDuration(3000);
        animatorSet.play(nullAnimator9).after(storyChatboxTwoAnimOn);
        animatorSet.play(storyChatboxTwoAnimOff).after(nullAnimator9);
        animatorSet.play(playAgainButtonOn).with(storyChatboxTwoAnimOff);

        //final AnimatorSet animatorSetCopy = animatorSet;
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
                                //next and knowledge point button will appear
                                //after the animation finish in the first time
                                if (isFirstTime) {
                                    isFirstTime = false;
                                    AnimatorSet showTwoButton = new AnimatorSet();
                                    if(nextButton.getVisibility() == View.GONE) {
                                        showTwoButton.play(nextButtonOn);
                                    }
                                    showTwoButton.start();
                                }
                            }
                        });
                    }
                });
            }
        };
        uiThread.start();
    }

    //initialize the return pop up window
    public void initPopupLayout() {
        popup = new BackToMainScreenPopup(this.getContext(), getActivity());
        popup.showPopupWindow();
    }

    //initialize the knowledge point pop up window
    public void initKnowledgePointPopupLayout() {
        knowledgePointPopup = new KnowledgePointPopup(this.getContext());
        knowledgePointPopup.showPopupWindow();
    }

    //initialize the question pop up window
    public void initQuestionPopupLayout() {
        classroomPopup = new ClassroomPopup(this.getContext(), this);
        classroomPopup.showPopupWindow();
    }

    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}