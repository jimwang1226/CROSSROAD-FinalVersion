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
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class FrontGateFragment extends Fragment implements View.OnClickListener{
    private View frontGateFragment;
    private View principleChatboxOne;
    private View principleChatboxTwo;
    private View principleChatboxThree;
    private View principleChatboxFour;
    private View principleChatboxFive;
    private View principleChatboxSix;
    private View principleChatboxSeven;
    private View userChatboxOne;
    private View jackChatboxOne;
    private View masonChatboxOne;
    private View userView;

    private Button nextButton;
    private Button backToMainscreenButton;
    private Button playAgainButton;
    private Button knowledgePointButton;
    private Button skipButton;

    private String gender;
    private boolean isFirstTime = true;
    private boolean hasOpenKnowledgePoint = false;

    BackToMainScreenPopup popup;
    FrontGateKnowledgePointPopup knowledgePointPopup;
    private AnimatorSet animatorSet = null;
    private Thread uiThread;

    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        frontGateFragment = LayoutInflater.from(getActivity()).inflate(R.layout.front_gate_fragment,
                container, false);
        //Initialize
        setView();

        //save to check point
        String fragmentName = this.getClass().getName();
        GameProgressUtil.saveCheckPoint(this.getActivity(), fragmentName);

        soundID = SoundUtil.initSound(this.getActivity());
        return frontGateFragment;
    }

    public void setView () {
        //initialize the view
        principleChatboxOne = frontGateFragment.findViewById(R.id.front_gate_fragment_principle_chat_box_one);
        principleChatboxTwo = frontGateFragment.findViewById(R.id.front_gate_fragment_principle_chat_box_two);
        principleChatboxThree = frontGateFragment.findViewById(R.id.front_gate_fragment_principle_chat_box_three);
        principleChatboxFour = frontGateFragment.findViewById(R.id.front_gate_fragment_principle_chat_box_four);
        principleChatboxFive = frontGateFragment.findViewById(R.id.front_gate_fragment_principle_chat_box_five);
        principleChatboxSix = frontGateFragment.findViewById(R.id.front_gate_fragment_principle_chat_box_six);
        principleChatboxSeven = frontGateFragment.findViewById(R.id.front_gate_fragment_principle_chat_box_seven);
        userChatboxOne = frontGateFragment.findViewById(R.id.front_gate_fragment_user_chat_box_one);
        jackChatboxOne = frontGateFragment.findViewById(R.id.front_gate_fragment_jack_chat_box_one);
        masonChatboxOne = frontGateFragment.findViewById(R.id.front_gate_fragment_mason_chat_box_one);
        userView = frontGateFragment.findViewById(R.id.front_gate_fragment_user_view);

        nextButton = (Button)frontGateFragment.findViewById(R.id.next_button_front_gate_fragment);
        backToMainscreenButton = (Button)frontGateFragment.findViewById(R.id.return_to_mainscreen_button_front_gate_fragment);
        playAgainButton = (Button)frontGateFragment.findViewById(R.id.play_again_button_front_gate_fragment);
        knowledgePointButton = (Button)frontGateFragment.findViewById(R.id.knowledge_point_button_front_gate_fragment);
        skipButton = (Button)frontGateFragment.findViewById(R.id.skip_button_front_gate_fragment);

        //set up the view
        SharedPreferences sharedPref = getActivity().getSharedPreferences("username_gender_choice",Context.MODE_PRIVATE);
        gender = sharedPref.getString("gender", null);
        if ("MALE".equals(gender)) {
            userView.setBackground(getResources().getDrawable(R.drawable.mainscreen_boy,null));
        }
        else {
            userView.setBackground(getResources().getDrawable(R.drawable.game_enter_girl,null));
        }
        nextButton.setOnClickListener(this);
        backToMainscreenButton.setOnClickListener(this);
        playAgainButton.setOnClickListener(this);
        knowledgePointButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);

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
        Fragment nextFragment;
        switch (view.getId()) {
            case R.id.next_button_front_gate_fragment:
                SoundUtil.playSound(soundID);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                if (!hasOpenKnowledgePoint) {
                    initKnowledgePointPopupLayout();
                    hasOpenKnowledgePoint = true;
                }
                else{
                    nextFragment = new ClassroomFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_in,R.animator.slide_in_opp,R.animator.slide_out_opp,
                            R.animator.slide_out).replace(R.id.game_change_area, nextFragment).commit();
                }
                break;
            case R.id.return_to_mainscreen_button_front_gate_fragment:
                SoundUtil.playSound(soundID);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                initPopupLayout();
                break;
            case R.id.play_again_button_front_gate_fragment:
                SoundUtil.playSound(soundID);
                //final AnimatorSet animatorSetCopy = animatorSet;
                uiThread = new Thread() {
                    public void run () {
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
            case R.id.knowledge_point_button_front_gate_fragment:
                SoundUtil.playSound(soundID);
                hasOpenKnowledgePoint = true;
                initKnowledgePointPopupLayout();
                break;
            case R.id.skip_button_front_gate_fragment:
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
        //principle chat box one - seven on
        ObjectAnimator principleChatboxOneAnimOn = AnimUtil.getAnimatorOn(principleChatboxOne, getActivity());
        ObjectAnimator principleChatboxTwoAnimOn = AnimUtil.getAnimatorOn(principleChatboxTwo, getActivity());
        ObjectAnimator principleChatboxThreeAnimOn = AnimUtil.getAnimatorOn(principleChatboxThree, getActivity());
        ObjectAnimator principleChatboxFourAnimOn = AnimUtil.getAnimatorOn(principleChatboxFour, getActivity());
        ObjectAnimator principleChatboxFiveAnimOn = AnimUtil.getAnimatorOn(principleChatboxFive, getActivity());
        ObjectAnimator principleChatboxSixAnimOn = AnimUtil.getAnimatorOn(principleChatboxSix, getActivity());
        ObjectAnimator principleChatboxSevenAnimOn = AnimUtil.getAnimatorOn(principleChatboxSeven, getActivity());

        //principle chat box one - seven off
        ObjectAnimator principleChatboxOneAnimOff = AnimUtil.getAnimatorOff(principleChatboxOne, getActivity());
        ObjectAnimator principleChatboxTwoAnimOff = AnimUtil.getAnimatorOff(principleChatboxTwo, getActivity());
        ObjectAnimator principleChatboxThreeAnimOff = AnimUtil.getAnimatorOff(principleChatboxThree, getActivity());
        ObjectAnimator principleChatboxFourAnimOff = AnimUtil.getAnimatorOff(principleChatboxFour, getActivity());
        ObjectAnimator principleChatboxFiveAnimOff = AnimUtil.getAnimatorOff(principleChatboxFive, getActivity());
        ObjectAnimator principleChatboxSixAnimOff = AnimUtil.getAnimatorOff(principleChatboxSix, getActivity());
        ObjectAnimator principleChatboxSevenAnimOff = AnimUtil.getAnimatorOff(principleChatboxSeven, getActivity());

        //user,jack,mason chat box one on
        ObjectAnimator userChatboxOneAnimOn = AnimUtil.getAnimatorOn(userChatboxOne, getActivity());
        ObjectAnimator jackChatboxOneAnimOn = AnimUtil.getAnimatorOn(jackChatboxOne, getActivity());
        ObjectAnimator masonChatboxOneAnimOn = AnimUtil.getAnimatorOn(masonChatboxOne, getActivity());

        //user,jack,mason chat box one off
        ObjectAnimator userChatboxOneAnimOff = AnimUtil.getAnimatorOff(userChatboxOne, getActivity());
        ObjectAnimator jackChatboxOneAnimOff = AnimUtil.getAnimatorOff(jackChatboxOne, getActivity());
        ObjectAnimator masonChatboxOneAnimOff = AnimUtil.getAnimatorOff(masonChatboxOne, getActivity());

        //play again button on
        ObjectAnimator playAgainButtonOn = AnimUtil.getPlayAgainAnimatorOn(playAgainButton, getActivity());

        //play again button off
        ObjectAnimator playAgainButtonOff = AnimUtil.getPlayAgainAnimatorOff(playAgainButton, getActivity());

        //next button on
        final ObjectAnimator nextButtonOn = AnimUtil.getPlayAgainAnimatorOn(nextButton, getActivity());

        //knowledge point button on
        final ObjectAnimator knowledgePointButtonOn = AnimUtil.getPlayAgainAnimatorOn(knowledgePointButton, getActivity());

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
            animatorSet.play(principleChatboxOneAnimOn).after(playAgainButtonOff);
        }
        else {
            animatorSet.play(principleChatboxOneAnimOn);
        }

        animatorSet.play(nullAnimator1).after(principleChatboxOneAnimOn);
        animatorSet.play(principleChatboxOneAnimOff).after(nullAnimator1);
        animatorSet.play(userChatboxOneAnimOn).with(principleChatboxOneAnimOff);

        animatorSet.play(nullAnimator2).after(userChatboxOneAnimOn);
        animatorSet.play(userChatboxOneAnimOff).after(nullAnimator2);
        animatorSet.play(principleChatboxTwoAnimOn).with(userChatboxOneAnimOff);

        animatorSet.play(nullAnimator3).after(principleChatboxTwoAnimOn);
        animatorSet.play(principleChatboxTwoAnimOff).after(nullAnimator3);
        animatorSet.play(principleChatboxThreeAnimOn).with(principleChatboxTwoAnimOff);

        animatorSet.play(nullAnimator4).after(principleChatboxThreeAnimOn);
        animatorSet.play(principleChatboxThreeAnimOff).after(nullAnimator4);
        animatorSet.play(jackChatboxOneAnimOn).with(principleChatboxThreeAnimOff);

        animatorSet.play(nullAnimator5).after(jackChatboxOneAnimOn);
        animatorSet.play(jackChatboxOneAnimOff).after(nullAnimator5);
        animatorSet.play(masonChatboxOneAnimOn).with(jackChatboxOneAnimOff);

        animatorSet.play(nullAnimator6).after(masonChatboxOneAnimOn);
        animatorSet.play(masonChatboxOneAnimOff).after(nullAnimator6);
        animatorSet.play(principleChatboxFourAnimOn).with(masonChatboxOneAnimOff);

        animatorSet.play(nullAnimator7).after(principleChatboxFourAnimOn);
        animatorSet.play(principleChatboxFourAnimOff).after(nullAnimator7);
        animatorSet.play(principleChatboxFiveAnimOn).with(principleChatboxFourAnimOff);

        animatorSet.play(nullAnimator8).after(principleChatboxFiveAnimOn);
        animatorSet.play(principleChatboxFiveAnimOff).after(nullAnimator8);
        animatorSet.play(principleChatboxSixAnimOn).with(principleChatboxFiveAnimOff);

        animatorSet.play(nullAnimator9).after(principleChatboxSixAnimOn);
        animatorSet.play(principleChatboxSixAnimOff).after(nullAnimator9);
        animatorSet.play(principleChatboxSevenAnimOn).with(principleChatboxSixAnimOff);

        animatorSet.play(nullAnimator10).after(principleChatboxSevenAnimOn);
        animatorSet.play(principleChatboxSevenAnimOff).after(nullAnimator10);
        animatorSet.play(playAgainButtonOn).with(principleChatboxSevenAnimOff);

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
                                if (isFirstTime) {
                                    isFirstTime = false;
                                    AnimatorSet showTwoButton = new AnimatorSet();
                                    if(knowledgePointButton.getVisibility() == View.GONE) {
                                        showTwoButton.play(knowledgePointButtonOn);
                                    }
                                    if(nextButton.getVisibility() == View.GONE) {
                                        showTwoButton.play(nextButtonOn).with(knowledgePointButtonOn);
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
        knowledgePointPopup = new FrontGateKnowledgePointPopup(this.getContext());
        knowledgePointPopup.showPopupWindow();
    }

    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }


}