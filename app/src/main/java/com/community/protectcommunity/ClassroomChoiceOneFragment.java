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

public class ClassroomChoiceOneFragment extends Fragment implements View.OnClickListener{
    private View classroomChoiceOneFragment;
    private View threeCharactersInside;
    private View masonChatboxOne;
    private View userChatboxOne;
    private View userChatboxTwo;
    private View storyChatboxOne;
    private View gracieChatboxOne;
    private View gracieChatboxTwo;

    private Button nextButton;
    private Button backToMainscreenButton;
    private Button playAgainButton;
    private Button knowledgePointButton;
    private Button skipButton;

    private String gender;
    private Boolean isFirstTime = true;
    private Boolean hasOpenKnowledgePoint = false;

    private int soundId;

    BackToMainScreenPopup popup;
    ClassroomPopup classroomPopup;
    ClassroomChoiceOneKnowledgePointPopup knowledgePointPopup;
    private AnimatorSet animatorSet = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        classroomChoiceOneFragment = LayoutInflater.from(getActivity()).inflate(R.layout.classroom_choice_one_fragment,
                container, false);
        //Initialize
        setView();
        soundId = SoundUtil.initSound(this.getActivity());

        //save to check point
        String fragmentName = this.getClass().getName();
        GameProgressUtil.saveCheckPoint(this.getActivity(), fragmentName);
        return classroomChoiceOneFragment;
    }

    public void setView () {
        //initialize the view
        // classroomFragment.findViewById(R.id.front_gate_fragment_principle_chat_box_one);
        threeCharactersInside = classroomChoiceOneFragment.findViewById(R.id.classroom_choice_one_three_characters_inside_view);
        masonChatboxOne = classroomChoiceOneFragment.findViewById(R.id.classroom_choice_one_mason_chat_box_one);
        userChatboxOne = classroomChoiceOneFragment.findViewById(R.id.classroom_choice_one_user_chat_box_one);
        userChatboxTwo = classroomChoiceOneFragment.findViewById(R.id.classroom_choice_one_user_chat_box_two);
        storyChatboxOne = classroomChoiceOneFragment.findViewById(R.id.classroom_choice_one_story_chat_box_one);
        gracieChatboxOne = classroomChoiceOneFragment.findViewById(R.id.classroom_choice_one_gracie_chat_box_one);
        gracieChatboxTwo = classroomChoiceOneFragment.findViewById(R.id.classroom_choice_one_gracie_chat_box_two);

        nextButton = (Button)classroomChoiceOneFragment.findViewById(R.id.next_button_classroom_choice_one);
        backToMainscreenButton = (Button)classroomChoiceOneFragment.findViewById(R.id.return_to_mainscreen_button_classroom_choice_one);
        playAgainButton = (Button)classroomChoiceOneFragment.findViewById(R.id.play_again_button_classroom_choice_one);
        knowledgePointButton = (Button)classroomChoiceOneFragment.findViewById(R.id.knowledge_point_button_classroom_choice_one);
        skipButton = (Button)classroomChoiceOneFragment.findViewById(R.id.skip_button_classroom_choice_one_fragment);

        //set up the view
        SharedPreferences sharedPref = getActivity().getSharedPreferences("username_gender_choice",Context.MODE_PRIVATE);
        gender = sharedPref.getString("gender", null);

        if ("MALE".equals(gender)) {
            threeCharactersInside.setBackground(getResources().getDrawable(R.drawable.three_characters_inside_boy_user_at_front,null));
        }
        else {
            threeCharactersInside.setBackground(getResources().getDrawable(R.drawable.three_characters_inside_girl_user_at_front,null));
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
            case R.id.next_button_classroom_choice_one:
                SoundUtil.playSound(soundId);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                if (!hasOpenKnowledgePoint) {
                    hasOpenKnowledgePoint = true;
                    initKnowledgePointPopupLayout();
                }
                else {
                    nextFragment = new CanteenFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_in,R.animator.slide_in_opp,R.animator.slide_out_opp,
                            R.animator.slide_out).replace(R.id.game_change_area, nextFragment).commit();
                }
                break;
            case R.id.return_to_mainscreen_button_classroom_choice_one:
                SoundUtil.playSound(soundId);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                initPopupLayout();
                break;
            case R.id.play_again_button_classroom_choice_one:
                SoundUtil.playSound(soundId);
                //final AnimatorSet animatorSetCopy = animatorSet;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animatorSet.start();
                    }
                });
                break;
            case R.id.knowledge_point_button_classroom_choice_one:
                SoundUtil.playSound(soundId);
                hasOpenKnowledgePoint = true;
                initKnowledgePointPopupLayout();
                break;
            case R.id.skip_button_classroom_choice_one_fragment:
                SoundUtil.playSound(soundId);
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
        ObjectAnimator userChatboxOneAnimOn = AnimUtil.getAnimatorOn(userChatboxOne, getActivity());
        ObjectAnimator userChatboxTwoAnimOn = AnimUtil.getAnimatorOn(userChatboxTwo, getActivity());
        ObjectAnimator gracieChatboxOneAnimOn = AnimUtil.getAnimatorOn(gracieChatboxOne, getActivity());
        ObjectAnimator gracieChatboxTwoAnimOn = AnimUtil.getAnimatorOn(gracieChatboxTwo, getActivity());
        ObjectAnimator storyChatboxOneAnimOn = AnimUtil.getAnimatorOn(storyChatboxOne, getActivity());

        //chat box off
        ObjectAnimator masonChatboxOneAnimOff = AnimUtil.getAnimatorOff(masonChatboxOne, getActivity());
        ObjectAnimator userChatboxOneAnimOff = AnimUtil.getAnimatorOff(userChatboxOne, getActivity());
        ObjectAnimator userChatboxTwoAnimOff = AnimUtil.getAnimatorOff(userChatboxTwo, getActivity());
        ObjectAnimator gracieChatboxOneAnimOff = AnimUtil.getAnimatorOff(gracieChatboxOne, getActivity());
        ObjectAnimator gracieChatboxTwoAnimOff = AnimUtil.getAnimatorOff(gracieChatboxTwo, getActivity());
        ObjectAnimator storyChatboxOneAnimOff = AnimUtil.getAnimatorOff(storyChatboxOne, getActivity());

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
            animatorSet.play(storyChatboxOneAnimOn).after(playAgainButtonOff);
        }
        else {
            animatorSet.play(storyChatboxOneAnimOn);
        }

        nullAnimator1.setDuration(3000);
        animatorSet.play(nullAnimator1).after(storyChatboxOneAnimOn);
        animatorSet.play(storyChatboxOneAnimOff).after(nullAnimator1);

        animatorSet.play(userChatboxOneAnimOn).with(storyChatboxOneAnimOff);
        animatorSet.play(nullAnimator2).after(userChatboxOneAnimOn);
        animatorSet.play(userChatboxOneAnimOff).after(nullAnimator2);

        animatorSet.play(gracieChatboxOneAnimOn).with(userChatboxOneAnimOff);
        animatorSet.play(nullAnimator3).after(gracieChatboxOneAnimOn);
        animatorSet.play(gracieChatboxOneAnimOff).after(nullAnimator3);

        animatorSet.play(gracieChatboxTwoAnimOn).with(gracieChatboxOneAnimOff);
        animatorSet.play(nullAnimator4).after(gracieChatboxTwoAnimOn);
        animatorSet.play(gracieChatboxTwoAnimOff).after(nullAnimator4);

        animatorSet.play(masonChatboxOneAnimOn).with(gracieChatboxTwoAnimOff);
        animatorSet.play(nullAnimator5).after(masonChatboxOneAnimOn);
        animatorSet.play(masonChatboxOneAnimOff).after(nullAnimator5);

        animatorSet.play(userChatboxTwoAnimOn).with(masonChatboxOneAnimOff);
        animatorSet.play(nullAnimator6).after(userChatboxTwoAnimOn);
        animatorSet.play(userChatboxTwoAnimOff).after(nullAnimator6);

        animatorSet.play(playAgainButtonOn).with(userChatboxTwoAnimOff);

        //final AnimatorSet animatorSetCopy = animatorSet;
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
        /**
        Looper.prepare();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animatorSetCopy.start();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //next and knowledge point button will appear
                        //after the animation finish in the first time
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
        }, 100);
        Looper.loop();
         */
    }

    //initialize the return pop up window
    public void initPopupLayout() {
        popup = new BackToMainScreenPopup(this.getContext(), getActivity());
        popup.showPopupWindow();
    }

    //initialize the knowledge point pop up window
    public void initKnowledgePointPopupLayout() {
        knowledgePointPopup = new ClassroomChoiceOneKnowledgePointPopup(this.getContext());
        knowledgePointPopup.showPopupWindow();
    }

    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

}