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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class CanteenChoiceOneFragment extends Fragment implements View.OnClickListener{
    private View canteenChoiceOneFragment;
    private View userView;
    private View gracieView;
    private View jackView;
    private View storyChatboxOne;
    private View jackChatboxOne;

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
    CanteenChoiceOneKnowledgePointPopup knowledgePointPopup;

    private AnimatorSet animatorSet = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        canteenChoiceOneFragment = LayoutInflater.from(getActivity()).inflate(R.layout.canteen_choice_one_fragment,
                container, false);
        //Initialize
        setView();
        soundId = SoundUtil.initSound(this.getActivity());

        //save to check point
        String fragmentName = this.getClass().getName();
        GameProgressUtil.saveCheckPoint(this.getActivity(), fragmentName);
        return canteenChoiceOneFragment;
    }

    public void setView () {
        //initialize the view
        userView = canteenChoiceOneFragment.findViewById(R.id.canteen_choice_one_user_inside_view);
        gracieView = canteenChoiceOneFragment.findViewById(R.id.canteen_choice_one_gracie_inside_view);
        storyChatboxOne = canteenChoiceOneFragment.findViewById(R.id.canteen_choice_one_story_chat_box_one);
        jackChatboxOne = canteenChoiceOneFragment.findViewById(R.id.canteen_choice_one_jack_chat_box_one);
        jackView = canteenChoiceOneFragment.findViewById(R.id.canteen_choice_one_jack_inside_view);

        nextButton = (Button)canteenChoiceOneFragment.findViewById(R.id.next_button_canteen_choice_one);
        backToMainscreenButton = (Button)canteenChoiceOneFragment.findViewById(R.id.return_to_mainscreen_button_canteen_choice_one);
        playAgainButton = (Button)canteenChoiceOneFragment.findViewById(R.id.play_again_button_canteen_choice_one);
        knowledgePointButton = (Button)canteenChoiceOneFragment.findViewById(R.id.knowledge_point_button_canteen_choice_one);
        skipButton = (Button)canteenChoiceOneFragment.findViewById(R.id.skip_button_canteen_choice_one_fragment);

        //set up the view
        SharedPreferences sharedPref = getActivity().getSharedPreferences("username_gender_choice",Context.MODE_PRIVATE);
        gender = sharedPref.getString("gender", null);

        if ("MALE".equals(gender)) {
            userView.setBackground(getResources().getDrawable(R.drawable.canteen_user_boy_inside,null));
        }
        else {
            userView.setBackground(getResources().getDrawable(R.drawable.canteen_user_girl_inside,null));
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
        switch (view.getId()) {
            case R.id.next_button_canteen_choice_one:
                SoundUtil.playSound(soundId);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                if (!hasOpenKnowledgePoint) {
                    hasOpenKnowledgePoint = true;
                    initKnowledgePointPopupLayout();
                }
                else {
                    //go to birthday fragment
                    FragmentManager fragmentManager = this.getFragmentManager();
                    Fragment nextFragment = new BirthdayFragment();
                    fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_in,R.animator.slide_in_opp,R.animator.slide_out_opp,
                            R.animator.slide_out).replace(R.id.game_change_area, nextFragment).commit();
                }
                break;
            case R.id.return_to_mainscreen_button_canteen_choice_one:
                SoundUtil.playSound(soundId);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                initPopupLayout();
                break;
            case R.id.play_again_button_canteen_choice_one:
                SoundUtil.playSound(soundId);
                //final AnimatorSet animatorSetCopy = animatorSet;
                gracieView.setVisibility(View.VISIBLE);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animatorSet.start();
                    }
                });
                break;
            case R.id.knowledge_point_button_canteen_choice_one:
                SoundUtil.playSound(soundId);
                hasOpenKnowledgePoint = true;
                initKnowledgePointPopupLayout();
                break;
            case R.id.skip_button_canteen_choice_one_fragment:
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
        ObjectAnimator jackChatboxOneAnimOn = AnimUtil.getAnimatorOn(jackChatboxOne, getActivity());
        ObjectAnimator storyChatboxOneAnimOn = AnimUtil.getAnimatorOn(storyChatboxOne, getActivity());

        //chat box off
        ObjectAnimator jackChatboxOneAnimOff = AnimUtil.getAnimatorOff(jackChatboxOne, getActivity());
        ObjectAnimator storyChatboxOneAnimOff = AnimUtil.getAnimatorOff(storyChatboxOne, getActivity());

        //character on
        ObjectAnimator userViewAnimOn = AnimUtil.getAnimatorOn(userView, getActivity());
        ObjectAnimator gracieViewAnimOn = AnimUtil.getAnimatorOn(gracieView, getActivity());
        ObjectAnimator jackViewAnimOn = AnimUtil.getAnimatorOn(jackView, getActivity());

        //character off
        ObjectAnimator userViewAnimOff = AnimUtil.getAnimatorOff(userView, getActivity());
        ObjectAnimator gracieViewAnimOff = AnimUtil.getAnimatorOff(gracieView, getActivity());
        ObjectAnimator jackViewAnimOff = AnimUtil.getAnimatorOff(jackView, getActivity());

        //play again button on
        ObjectAnimator playAgainButtonOn = AnimUtil.getPlayAgainAnimatorOn(playAgainButton, getActivity());

        //play again button off
        ObjectAnimator playAgainButtonOff = AnimUtil.getPlayAgainAnimatorOff(playAgainButton, getActivity());

        //knowledge point button on
        final ObjectAnimator knowledgePointButtonOn = AnimUtil.getPlayAgainAnimatorOn(knowledgePointButton, getActivity());

        //next button on
        final ObjectAnimator nextButtonOn = AnimUtil.getPlayAgainAnimatorOn(nextButton, getActivity());

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
            animatorSet.play(jackViewAnimOn).after(playAgainButtonOff);
        }
        else {
            animatorSet.play(jackViewAnimOn);
        }

        animatorSet.play(jackChatboxOneAnimOn).after(jackViewAnimOn);
        animatorSet.play(nullAnimator1).after(jackChatboxOneAnimOn);
        animatorSet.play(jackChatboxOneAnimOff).after(nullAnimator1);

        animatorSet.play(jackViewAnimOff).after(jackChatboxOneAnimOff);
        animatorSet.play(userViewAnimOff).with(jackViewAnimOff);

        animatorSet.play(gracieViewAnimOff).after(userViewAnimOff);
        nullAnimator2.setDuration(3000);
        animatorSet.play(storyChatboxOneAnimOn).with(gracieViewAnimOff);
        animatorSet.play(nullAnimator2).after(storyChatboxOneAnimOn);
        animatorSet.play(storyChatboxOneAnimOff).after(nullAnimator2);

        animatorSet.play(playAgainButtonOn).with(storyChatboxOneAnimOff);

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
    }

    //initialize the return pop up window
    public void initPopupLayout() {
        popup = new BackToMainScreenPopup(this.getContext(), getActivity());
        popup.showPopupWindow();
    }

    //initialize the knowledge point pop up window
    public void initKnowledgePointPopupLayout() {
        knowledgePointPopup = new CanteenChoiceOneKnowledgePointPopup(this.getContext());
        knowledgePointPopup.showPopupWindow();
    }

    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}