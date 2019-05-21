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

public class CanteenChoiceTwoFragment extends Fragment implements View.OnClickListener{
    private View canteenChoiceTwoFragment;
    private View userView;
    private View gracieView;
    private View gracieHappyView;
    private View userChatboxOne;
    private View userChatboxTwo;
    private View userChatboxThree;
    private View gracieChatboxOne;
    private View gracieChatboxTwo;
    private View gracieChatboxThree;

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
    CanteenChoiceTwoKnowledgePointPopup knowledgePointPopup;

    private AnimatorSet animatorSet = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        canteenChoiceTwoFragment = LayoutInflater.from(getActivity()).inflate(R.layout.canteen_choice_two_fragment,
                container, false);
        //Initialize
        setView();
        soundId = SoundUtil.initSound(this.getActivity());

        //save to check point
        String fragmentName = this.getClass().getName();
        GameProgressUtil.saveCheckPoint(this.getActivity(), fragmentName);
        return canteenChoiceTwoFragment;
    }

    public void setView () {
        //initialize the view
        userView = canteenChoiceTwoFragment.findViewById(R.id.canteen_choice_two_user_inside_view);
        gracieView = canteenChoiceTwoFragment.findViewById(R.id.canteen_choice_two_gracie_inside_view);
        gracieHappyView = canteenChoiceTwoFragment.findViewById(R.id.canteen_choice_two_gracie_happy_inside_view);
        userChatboxOne = canteenChoiceTwoFragment.findViewById(R.id.canteen_choice_two_user_chat_box_one);
        userChatboxTwo = canteenChoiceTwoFragment.findViewById(R.id.canteen_choice_two_user_chat_box_two);
        userChatboxThree = canteenChoiceTwoFragment.findViewById(R.id.canteen_choice_two_user_chat_box_three);
        gracieChatboxOne = canteenChoiceTwoFragment.findViewById(R.id.canteen_choice_two_gracie_chat_box_one);
        gracieChatboxTwo = canteenChoiceTwoFragment.findViewById(R.id.canteen_choice_two_gracie_chat_box_two);
        gracieChatboxThree = canteenChoiceTwoFragment.findViewById(R.id.canteen_choice_two_gracie_chat_box_three);


        nextButton = (Button)canteenChoiceTwoFragment.findViewById(R.id.next_button_canteen_choice_two);
        backToMainscreenButton = (Button)canteenChoiceTwoFragment.findViewById(R.id.return_to_mainscreen_button_canteen_choice_two);
        playAgainButton = (Button)canteenChoiceTwoFragment.findViewById(R.id.play_again_button_canteen_choice_two);
        knowledgePointButton = (Button)canteenChoiceTwoFragment.findViewById(R.id.knowledge_point_button_canteen_choice_two);
        skipButton = (Button)canteenChoiceTwoFragment.findViewById(R.id.skip_button_canteen_choice_two_fragment);

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
            case R.id.next_button_canteen_choice_two:
                SoundUtil.playSound(soundId);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                if (!hasOpenKnowledgePoint) {
                    hasOpenKnowledgePoint = true;
                    initKnowledgePointPopupLayout();
                }
                else {
                    //getEnding();
                    FragmentManager fragmentManager = this.getFragmentManager();
                    Fragment nextFragment = new BirthdayFragment();
                    fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_in,R.animator.slide_in_opp,R.animator.slide_out_opp,
                            R.animator.slide_out).replace(R.id.game_change_area, nextFragment).commit();
                }
                break;
            case R.id.return_to_mainscreen_button_canteen_choice_two:
                SoundUtil.playSound(soundId);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                initPopupLayout();
                break;
            case R.id.play_again_button_canteen_choice_two:
                SoundUtil.playSound(soundId);
                //final AnimatorSet animatorSetCopy = animatorSet;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animatorSet.start();
                    }
                });
                break;
            case R.id.knowledge_point_button_canteen_choice_two:
                SoundUtil.playSound(soundId);
                hasOpenKnowledgePoint = true;
                initKnowledgePointPopupLayout();
                break;
            case R.id.skip_button_canteen_choice_two_fragment:
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
        ObjectAnimator userChatboxOneAnimOn = AnimUtil.getAnimatorOn(userChatboxOne, getActivity());
        ObjectAnimator userChatboxTwoAnimOn = AnimUtil.getAnimatorOn(userChatboxTwo, getActivity());
        ObjectAnimator userChatboxThreeAnimOn = AnimUtil.getAnimatorOn(userChatboxThree, getActivity());
        ObjectAnimator gracieChatboxOneAnimOn = AnimUtil.getAnimatorOn(gracieChatboxOne, getActivity());
        ObjectAnimator gracieChatboxTwoAnimOn = AnimUtil.getAnimatorOn(gracieChatboxTwo, getActivity());
        ObjectAnimator gracieChatboxThreeAnimOn = AnimUtil.getAnimatorOn(gracieChatboxThree, getActivity());

        //chat box off
        ObjectAnimator userChatboxOneAnimOff = AnimUtil.getAnimatorOff(userChatboxOne, getActivity());
        ObjectAnimator userChatboxTwoAnimOff = AnimUtil.getAnimatorOff(userChatboxTwo, getActivity());
        ObjectAnimator userChatboxThreeAnimOff = AnimUtil.getAnimatorOff(userChatboxThree, getActivity());
        ObjectAnimator gracieChatboxOneAnimOff = AnimUtil.getAnimatorOff(gracieChatboxOne, getActivity());
        ObjectAnimator gracieChatboxTwoAnimOff = AnimUtil.getAnimatorOff(gracieChatboxTwo, getActivity());
        ObjectAnimator gracieChatboxThreeAnimOff = AnimUtil.getAnimatorOff(gracieChatboxThree, getActivity());

        //character on
        ObjectAnimator gracieHappyViewAnimOn = AnimUtil.getAnimatorOn(gracieHappyView, getActivity());

        //character off
        ObjectAnimator userViewAnimOff = AnimUtil.getAnimatorOff(userView, getActivity());
        ObjectAnimator gracieViewAnimOff = AnimUtil.getAnimatorOff(gracieView, getActivity());
        ObjectAnimator gracieHappyViewAnimOff = AnimUtil.getAnimatorOff(gracieHappyView, getActivity());

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
            animatorSet.play(userChatboxOneAnimOn).after(playAgainButtonOff);
        }
        else {
            animatorSet.play(userChatboxOneAnimOn);
        }

        animatorSet.play(nullAnimator1).after(userChatboxOneAnimOn);
        animatorSet.play(userChatboxOneAnimOff).after(nullAnimator1);

        animatorSet.play(gracieViewAnimOff).after(userChatboxOneAnimOff);
        animatorSet.play(gracieHappyViewAnimOn).with(gracieViewAnimOff);

        animatorSet.play(gracieChatboxOneAnimOn).after(gracieHappyViewAnimOn);
        animatorSet.play(nullAnimator2).after(gracieChatboxOneAnimOn);
        animatorSet.play(gracieChatboxOneAnimOff).after(nullAnimator2);

        animatorSet.play(userChatboxTwoAnimOn).with(gracieChatboxOneAnimOff);
        animatorSet.play(nullAnimator3).after(userChatboxTwoAnimOn);
        animatorSet.play(userChatboxTwoAnimOff).after(nullAnimator3);

        animatorSet.play(gracieChatboxTwoAnimOn).with(userChatboxTwoAnimOff);
        animatorSet.play(nullAnimator4).after(gracieChatboxTwoAnimOn);
        animatorSet.play(gracieChatboxTwoAnimOff).after(nullAnimator4);

        animatorSet.play(gracieChatboxThreeAnimOn).with(gracieChatboxTwoAnimOff);
        animatorSet.play(nullAnimator5).after(gracieChatboxThreeAnimOn);
        animatorSet.play(gracieChatboxThreeAnimOff).after(nullAnimator5);

        animatorSet.play(userChatboxThreeAnimOn).with(gracieChatboxThreeAnimOff);
        animatorSet.play(nullAnimator6).after(userChatboxThreeAnimOn);
        animatorSet.play(userChatboxThreeAnimOff).after(nullAnimator6);

        animatorSet.play(userViewAnimOff).after(userChatboxThreeAnimOff);
        animatorSet.play(gracieHappyViewAnimOff).with(userViewAnimOff);

        animatorSet.play(playAgainButtonOn).after(gracieHappyViewAnimOff);

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
        knowledgePointPopup = new CanteenChoiceTwoKnowledgePointPopup(this.getContext());
        knowledgePointPopup.showPopupWindow();
    }

    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}