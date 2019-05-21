package com.community.protectcommunity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class CanteenFragment extends Fragment implements View.OnClickListener{
    private View canteenFragment;
    private View twoBoysInsideView;
    private View userView;
    private View gracieView;
    private View storyChatboxOne;
    private View storyChatboxTwo;
    private View masonChatboxOne;
    private View masonChatboxTwo;
    private View jackChatboxOne;
    private View jackChatboxTwo;

    private Button nextButton;
    private Button backToMainscreenButton;
    private Button playAgainButton;
    private Button skipButton;

    private String gender;
    private Boolean isFirstTime = true;

    private int soundId;

    BackToMainScreenPopup popup;
    KnowledgePointPopup knowledgePointPopup;
    CanteenPopup canteenPopup;
    private AnimatorSet animatorSet = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        canteenFragment = LayoutInflater.from(getActivity()).inflate(R.layout.canteen_fragment,
                container, false);
        //Initialize
        setView();
        soundId = SoundUtil.initSound(this.getActivity());

        //save to check point
        String fragmentName = this.getClass().getName();
        GameProgressUtil.saveCheckPoint(this.getActivity(), fragmentName);
        return canteenFragment;
    }

    public void setView () {
        //initialize the view
        twoBoysInsideView = canteenFragment.findViewById(R.id.canteen_two_boys_inside_view);
        userView = canteenFragment.findViewById(R.id.canteen_user_insdie_view);
        gracieView = canteenFragment.findViewById(R.id.canteen_gracie_inside_view);
        storyChatboxOne = canteenFragment.findViewById(R.id.canteen_story_chat_box_one);
        storyChatboxTwo = canteenFragment.findViewById(R.id.canteen_story_chat_box_two);
        jackChatboxOne = canteenFragment.findViewById(R.id.canteen_jack_chat_box_one);
        jackChatboxTwo = canteenFragment.findViewById(R.id.canteen_jack_chat_box_two);
        masonChatboxOne = canteenFragment.findViewById(R.id.canteen_mason_chat_box_one);
        masonChatboxTwo = canteenFragment.findViewById(R.id.canteen_mason_chat_box_two);

        nextButton = (Button)canteenFragment.findViewById(R.id.next_button_canteen_fragment);
        backToMainscreenButton = (Button)canteenFragment.findViewById(R.id.return_to_mainscreen_button_canteen_fragment);
        playAgainButton = (Button)canteenFragment.findViewById(R.id.play_again_button_canteen_fragment);
        skipButton = (Button)canteenFragment.findViewById(R.id.skip_button_canteen_fragment);

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
            case R.id.next_button_canteen_fragment:
                SoundUtil.playSound(soundId);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                initQuestionPopupLayout();
                break;
            case R.id.return_to_mainscreen_button_canteen_fragment:
                SoundUtil.playSound(soundId);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                initPopupLayout();
                break;
            case R.id.play_again_button_canteen_fragment:
                SoundUtil.playSound(soundId);
                //final AnimatorSet animatorSetCopy = animatorSet;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animatorSet.start();
                    }
                });
                break;
            case R.id.skip_button_canteen_fragment:
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
        ObjectAnimator jackChatboxTwoAnimOn = AnimUtil.getAnimatorOn(jackChatboxTwo, getActivity());
        ObjectAnimator masonChatboxOneAnimOn = AnimUtil.getAnimatorOn(masonChatboxOne, getActivity());
        ObjectAnimator masonChatboxTwoAnimOn = AnimUtil.getAnimatorOn(masonChatboxTwo, getActivity());
        ObjectAnimator storyChatboxOneAnimOn = AnimUtil.getAnimatorOn(storyChatboxOne, getActivity());
        ObjectAnimator storyChatboxTwoAnimOn = AnimUtil.getAnimatorOn(storyChatboxTwo, getActivity());

        //chat box off
        ObjectAnimator jackChatboxOneAnimOff = AnimUtil.getAnimatorOff(jackChatboxOne, getActivity());
        ObjectAnimator jackChatboxTwoAnimOff = AnimUtil.getAnimatorOff(jackChatboxTwo, getActivity());
        ObjectAnimator masonChatboxOneAnimOff = AnimUtil.getAnimatorOff(masonChatboxOne, getActivity());
        ObjectAnimator masonChatboxTwoAnimOff = AnimUtil.getAnimatorOff(masonChatboxTwo, getActivity());
        ObjectAnimator storyChatboxOneAnimOff = AnimUtil.getAnimatorOff(storyChatboxOne, getActivity());
        ObjectAnimator storyChatboxTwoAnimOff = AnimUtil.getAnimatorOff(storyChatboxTwo, getActivity());

        //character on
        ObjectAnimator userViewAnimOn = AnimUtil.getAnimatorOn(userView, getActivity());
        ObjectAnimator gracieViewAnimOn = AnimUtil.getAnimatorOn(gracieView, getActivity());
        ObjectAnimator twoBoysViewAnimOn = AnimUtil.getAnimatorOn(twoBoysInsideView, getActivity());

        //character off
        ObjectAnimator userViewAnimOff = AnimUtil.getAnimatorOff(userView, getActivity());
        ObjectAnimator gracieViewAnimOff = AnimUtil.getAnimatorOff(gracieView, getActivity());
        ObjectAnimator twoBoysViewAnimOff = AnimUtil.getAnimatorOff(twoBoysInsideView, getActivity());

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
            animatorSet.play(storyChatboxOneAnimOn).after(playAgainButtonOff);
        }
        else {
            animatorSet.play(storyChatboxOneAnimOn);
        }

        nullAnimator1.setDuration(3000);
        animatorSet.play(nullAnimator1).after(storyChatboxOneAnimOn);
        animatorSet.play(storyChatboxOneAnimOff).after(nullAnimator1);

        animatorSet.play(gracieViewAnimOn).after(storyChatboxOneAnimOff);
        animatorSet.play(userViewAnimOn).with(gracieViewAnimOn);
        animatorSet.play(twoBoysViewAnimOn).with(gracieViewAnimOn);

        animatorSet.play(masonChatboxOneAnimOn).after(twoBoysViewAnimOn);
        animatorSet.play(nullAnimator2).after(masonChatboxOneAnimOn);
        animatorSet.play(masonChatboxOneAnimOff).after(nullAnimator2);

        animatorSet.play(masonChatboxTwoAnimOn).with(masonChatboxOneAnimOff);
        animatorSet.play(nullAnimator3).after(masonChatboxTwoAnimOn);
        animatorSet.play(masonChatboxTwoAnimOff).after(nullAnimator3);

        animatorSet.play(jackChatboxOneAnimOn).with(masonChatboxTwoAnimOff);
        animatorSet.play(nullAnimator4).after(jackChatboxOneAnimOn);
        animatorSet.play(jackChatboxOneAnimOff).after(nullAnimator4);

        animatorSet.play(jackChatboxTwoAnimOn).with(jackChatboxOneAnimOff);
        animatorSet.play(nullAnimator5).after(jackChatboxTwoAnimOn);
        animatorSet.play(jackChatboxTwoAnimOff).after(nullAnimator5);

        animatorSet.play(twoBoysViewAnimOff).after(jackChatboxTwoAnimOff);
        animatorSet.play(storyChatboxTwoAnimOn).after(twoBoysViewAnimOff);
        nullAnimator6.setDuration(3000);
        animatorSet.play(nullAnimator6).after(storyChatboxTwoAnimOn);
        animatorSet.play(storyChatboxTwoAnimOff).after(nullAnimator6);

        animatorSet.play(playAgainButtonOn).with(storyChatboxTwoAnimOff);

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

    //initialize the knowledge point pop up window
    public void initKnowledgePointPopupLayout() {
        knowledgePointPopup = new KnowledgePointPopup(this.getContext());
        knowledgePointPopup.showPopupWindow();
    }

    //initialize the question pop up window
    public void initQuestionPopupLayout() {
        canteenPopup = new CanteenPopup(this.getContext(), this);
        canteenPopup.showPopupWindow();
    }

    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

}