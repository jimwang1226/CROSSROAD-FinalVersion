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

public class BirthdayFragment extends Fragment implements View.OnClickListener{
    private View birthdayFragment;
    private View twoGirlsInsideView;
    private View userFarView;
    private View userNearView;
    private View storyChatboxOne;
    private View gracieChatboxOne;
    private View gracieChatboxTwo;
    private View gracieChatboxThree;
    private View gracieChatboxFour;
    private View gracieChatboxFive;
    private View lisaChatboxOne;

    private Button nextButton;
    private Button backToMainscreenButton;
    private Button playAgainButton;
    private Button skipButton;

    private String gender;
    private Boolean isFirstTime = true;

    private int soundId;

    BackToMainScreenPopup popup;
    KnowledgePointPopup knowledgePointPopup;
    BirthdayPopup birthdayPopup;
    private AnimatorSet animatorSet = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        birthdayFragment = LayoutInflater.from(getActivity()).inflate(R.layout.birthday_fragment,
                container, false);
        //Initialize
        setView();
        soundId = SoundUtil.initSound(this.getActivity());

        //save to check point
        String fragmentName = this.getClass().getName();
        GameProgressUtil.saveCheckPoint(this.getActivity(), fragmentName);
        return birthdayFragment;
    }

    public void setView () {
        //initialize the view
        twoGirlsInsideView = birthdayFragment.findViewById(R.id.birthday_two_girls_inside_view);
        userFarView = birthdayFragment.findViewById(R.id.birthday_user_inside_far_view);
        userNearView = birthdayFragment.findViewById(R.id.birthday_user_inside_near_view);
        storyChatboxOne = birthdayFragment.findViewById(R.id.birthday_story_chat_box_one);
        gracieChatboxOne = birthdayFragment.findViewById(R.id.birthday_gracie_chat_box_one);
        gracieChatboxTwo = birthdayFragment.findViewById(R.id.birthday_gracie_chat_box_two);
        gracieChatboxThree = birthdayFragment.findViewById(R.id.birthday_gracie_chat_box_three);
        gracieChatboxFour = birthdayFragment.findViewById(R.id.birthday_gracie_chat_box_four);
        gracieChatboxFive = birthdayFragment.findViewById(R.id.birthday_gracie_chat_box_five);
        lisaChatboxOne = birthdayFragment.findViewById(R.id.birthday_lisa_chat_box_one);

        nextButton = (Button)birthdayFragment.findViewById(R.id.next_button_birthday_fragment);
        backToMainscreenButton = (Button)birthdayFragment.findViewById(R.id.return_to_mainscreen_button_birthday_fragment);
        playAgainButton = (Button)birthdayFragment.findViewById(R.id.play_again_button_birthday_fragment);
        skipButton = (Button)birthdayFragment.findViewById(R.id.skip_button_birthday_fragment);

        //set up the view
        SharedPreferences sharedPref = getActivity().getSharedPreferences("username_gender_choice",Context.MODE_PRIVATE);
        gender = sharedPref.getString("gender", null);

        if ("MALE".equals(gender)) {
            userNearView.setBackground(getResources().getDrawable(R.drawable.birthday_user_inside_near_view,null));
            userFarView.setBackground(getResources().getDrawable(R.drawable.boy,null));
        }
        else {
            userNearView.setBackground(getResources().getDrawable(R.drawable.birthday_user_inside_girl_near_view,null));
            userFarView.setBackground(getResources().getDrawable(R.drawable.girl,null));
        }

        nextButton.setOnClickListener(this);
        backToMainscreenButton.setOnClickListener(this);
        playAgainButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);

        //start the storyline, delay play it because there is a screen switch animation
        //otherwise it would stuck

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startStorylineThreadOne();
            }
        },500);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_button_birthday_fragment:
                SoundUtil.playSound(soundId);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                //go to scenario 6
                getMovieFragment();
                break;
            case R.id.return_to_mainscreen_button_birthday_fragment:
                SoundUtil.playSound(soundId);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                initPopupLayout();
                break;
            case R.id.play_again_button_birthday_fragment:
                SoundUtil.playSound(soundId);
                //final AnimatorSet animatorSetCopy = animatorSet;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animatorSet.start();
                    }
                });
                break;
            case R.id.skip_button_birthday_fragment:
                SoundUtil.playSound(soundId);
                animatorSet.end();
                break;
            default:
                break;
        }
    }

    public void startStorylineThreadOne() {
        if (getActivity() == null) {
            return;
        }
        //story box on
        ObjectAnimator storyChatboxOneAnimOn = AnimUtil.getAnimatorOn(storyChatboxOne, getActivity());
        ObjectAnimator gracieChatboxOneAnimOn = AnimUtil.getAnimatorOn(gracieChatboxOne, getActivity());

        //chat box off
        ObjectAnimator storyChatboxOneAnimOff = AnimUtil.getAnimatorOff(storyChatboxOne, getActivity());
        ObjectAnimator gracieChatboxOneAnimOff = AnimUtil.getAnimatorOff(gracieChatboxOne, getActivity());

        //character on
        ObjectAnimator userFarViewAnimOn = AnimUtil.getAnimatorOn(userFarView, getActivity());
        ObjectAnimator twoGirlsInsideViewAnimOn = AnimUtil.getAnimatorOn(twoGirlsInsideView, getActivity());
        ObjectAnimator playAgainButtonOff = AnimUtil.getPlayAgainAnimatorOff(playAgainButton, getActivity());

        //play again button on
        ObjectAnimator playAgainButtonOn = AnimUtil.getPlayAgainAnimatorOn(playAgainButton, getActivity());

        //Null animator for gap
        ValueAnimator nullAnimator1 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator2 = AnimUtil.getNullAnimator();

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

        animatorSet.play(twoGirlsInsideViewAnimOn).after(storyChatboxOneAnimOff);
        animatorSet.play(userFarViewAnimOn).with(twoGirlsInsideViewAnimOn);

        animatorSet.play(gracieChatboxOneAnimOn).after(twoGirlsInsideViewAnimOn);
        animatorSet.play(nullAnimator2).after(gracieChatboxOneAnimOn);
        animatorSet.play(gracieChatboxOneAnimOff).after(nullAnimator2);
        animatorSet.play(playAgainButtonOn).with(gracieChatboxOneAnimOff);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatorSet.start();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //pop up choice window
                        initQuestionPopupLayout();
                    }
                });
            }
        });
    }

    public void startStorylineThreadTwo() {
        if (getActivity() == null) {
            return;
        }

        //chat box on
        ObjectAnimator gracieChatboxTwoAnimOn = AnimUtil.getAnimatorOn(gracieChatboxTwo, getActivity());
        ObjectAnimator gracieChatboxThreeAnimOn = AnimUtil.getAnimatorOn(gracieChatboxThree, getActivity());
        ObjectAnimator gracieChatboxFourAnimOn = AnimUtil.getAnimatorOn(gracieChatboxFour, getActivity());
        ObjectAnimator gracieChatboxFiveAnimOn = AnimUtil.getAnimatorOn(gracieChatboxFive, getActivity());
        ObjectAnimator lisaChatboxOneAnimOn = AnimUtil.getAnimatorOn(lisaChatboxOne, getActivity());

        //chat box off
        ObjectAnimator gracieChatboxTwoAnimOff = AnimUtil.getAnimatorOff(gracieChatboxTwo, getActivity());
        ObjectAnimator gracieChatboxThreeAnimOff = AnimUtil.getAnimatorOff(gracieChatboxThree, getActivity());
        ObjectAnimator gracieChatboxFourAnimOff = AnimUtil.getAnimatorOff(gracieChatboxFour, getActivity());
        ObjectAnimator gracieChatboxFiveAnimOff = AnimUtil.getAnimatorOff(gracieChatboxFive, getActivity());
        ObjectAnimator lisaChatboxOneAnimOff = AnimUtil.getAnimatorOff(lisaChatboxOne, getActivity());

        //character on
        ObjectAnimator userNearViewAnimOn = AnimUtil.getAnimatorOn(userNearView, getActivity());
        ObjectAnimator twoGirlsViewAnimOn = AnimUtil.getAnimatorOn(twoGirlsInsideView, getActivity());

        //character off
        ObjectAnimator userFarViewAnimOff = AnimUtil.getAnimatorOff(userFarView, getActivity());
        ObjectAnimator userNearViewAnimOff = AnimUtil.getAnimatorOff(userNearView, getActivity());
        ObjectAnimator twoGirlsViewAnimOff = AnimUtil.getAnimatorOff(twoGirlsInsideView, getActivity());



        //play again button on
        ObjectAnimator playAgainButtonOn = AnimUtil.getPlayAgainAnimatorOn(playAgainButton, getActivity());

        //play again button off
        ObjectAnimator playAgainButtonOff = AnimUtil.getPlayAgainAnimatorOff(playAgainButton, getActivity());

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
            animatorSet.play(userFarViewAnimOff).after(playAgainButtonOff);
        }
        else {
            animatorSet.play(userFarViewAnimOff);
        }

        animatorSet.play(userNearViewAnimOn).after(userFarViewAnimOff);
        animatorSet.play(twoGirlsViewAnimOn).with(userNearViewAnimOn);

        animatorSet.play(gracieChatboxTwoAnimOn).after(userNearViewAnimOn);
        animatorSet.play(nullAnimator1).after(gracieChatboxTwoAnimOn);
        animatorSet.play(gracieChatboxTwoAnimOff).after(nullAnimator1);

        animatorSet.play(gracieChatboxThreeAnimOn).with(gracieChatboxTwoAnimOff);
        animatorSet.play(nullAnimator2).after(gracieChatboxThreeAnimOn);
        animatorSet.play(gracieChatboxThreeAnimOff).after(nullAnimator2);

        animatorSet.play(lisaChatboxOneAnimOn).with(gracieChatboxThreeAnimOff);
        animatorSet.play(nullAnimator3).after(lisaChatboxOneAnimOn);
        animatorSet.play(lisaChatboxOneAnimOff).after(nullAnimator3);

        animatorSet.play(gracieChatboxFourAnimOn).with(lisaChatboxOneAnimOff);
        animatorSet.play(nullAnimator4).after(gracieChatboxFourAnimOn);
        animatorSet.play(gracieChatboxFourAnimOff).after(nullAnimator4);

        animatorSet.play(gracieChatboxFiveAnimOn).with(gracieChatboxFourAnimOff);
        animatorSet.play(nullAnimator5).after(gracieChatboxFiveAnimOn);
        animatorSet.play(gracieChatboxFiveAnimOff).after(nullAnimator5);

        animatorSet.play(twoGirlsViewAnimOff).with(gracieChatboxFiveAnimOff);
        animatorSet.play(userNearViewAnimOff).with(gracieChatboxFiveAnimOff);
        animatorSet.play(playAgainButtonOn).with(gracieChatboxFiveAnimOff);

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
                            /**
                            if(knowledgePointButton.getVisibility() == View.GONE) {
                                showTwoButton.play(knowledgePointButtonOn);
                            }
                             */
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

    //initialize the return pop up window
    public void initPopupLayout() {
        popup = new BackToMainScreenPopup(this.getContext(), getActivity());
        popup.showPopupWindow();
    }

    //initialize the question pop up window
    public void initQuestionPopupLayout() {
        birthdayPopup = new BirthdayPopup(this.getContext(), this);
        birthdayPopup.showPopupWindow();
    }

    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    public void getMovieFragment(){
        FragmentManager fragmentManager = this.getFragmentManager();
        Fragment nextFragment = new MovieFragment();
        fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_in,R.animator.slide_in_opp,R.animator.slide_out_opp,
                R.animator.slide_out).replace(R.id.game_change_area, nextFragment).commit();
    }
}