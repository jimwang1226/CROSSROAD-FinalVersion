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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class ClassroomChoiceTwoFragment extends Fragment implements View.OnClickListener{
    private View classroomChoiceTwoFragment;
    private View jackChatboxOne;
    private View userChatboxOne;
    private View storyChatboxOne;
    private View threeCharactersInside;

    private Button nextButton;
    private Button backToMainscreenButton;
    private Button playAgainButton;
    private Button knowledgePointButton;
    private Button skipButton;

    private String gender;
    private Boolean isFirstTime = true;
    private Boolean hasOpenKnowledgePoint = false;

    private int soundID;//Create an audio ID for a sound

    BackToMainScreenPopup popup;
    ClassroomChoiceTwoKnowledgePointPopup knowledgePointPopup;
    private AnimatorSet animatorSet = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        classroomChoiceTwoFragment = LayoutInflater.from(getActivity()).inflate(R.layout.classroom_choice_two_fragment,
                container, false);
        //Initialize
        setView();
        soundID = SoundUtil.initSound(this.getActivity());

        //save to check point
        String fragmentName = this.getClass().getName();
        GameProgressUtil.saveCheckPoint(this.getActivity(), fragmentName);
        return classroomChoiceTwoFragment;
    }

    public void setView () {
        //initialize the view
        jackChatboxOne = classroomChoiceTwoFragment.findViewById(R.id.classroom_choice_two_jack_chat_box_one);
        userChatboxOne = classroomChoiceTwoFragment.findViewById(R.id.classroom_choice_two_user_chat_box_one);
        storyChatboxOne = classroomChoiceTwoFragment.findViewById(R.id.classroom_choice_two_story_chat_box_one);
        threeCharactersInside = classroomChoiceTwoFragment.findViewById(R.id.classroom_choice_two_three_characters_inside_view);

        nextButton = (Button)classroomChoiceTwoFragment.findViewById(R.id.next_button_classroom_choice_two);
        backToMainscreenButton = (Button)classroomChoiceTwoFragment.findViewById(R.id.return_to_mainscreen_button_classroom_choice_two);
        playAgainButton = (Button)classroomChoiceTwoFragment.findViewById(R.id.play_again_button_classroom_choice_two);
        knowledgePointButton = (Button)classroomChoiceTwoFragment.findViewById(R.id.knowledge_point_button_classroom_choice_two);
        skipButton = (Button)classroomChoiceTwoFragment.findViewById(R.id.skip_button_classroom_choice_two_fragment);

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
            case R.id.next_button_classroom_choice_two:
                SoundUtil.playSound(soundID);
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
            case R.id.return_to_mainscreen_button_classroom_choice_two:
                SoundUtil.playSound(soundID);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                initPopupLayout();
                break;
            case R.id.play_again_button_classroom_choice_two:
                SoundUtil.playSound(soundID);
                //final AnimatorSet animatorSetCopy = animatorSet;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animatorSet.start();
                    }
                });
                /**
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animatorSetCopy.start();
                    }
                }, 0);
                 */
                break;
            case R.id.knowledge_point_button_classroom_choice_two:
                SoundUtil.playSound(soundID);
                hasOpenKnowledgePoint = true;
                initKnowledgePointPopupLayout();
                break;
            case R.id.skip_button_classroom_choice_two_fragment:
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
        ObjectAnimator userChatboxOneAnimOn = AnimUtil.getAnimatorOn(userChatboxOne, getActivity());
        ObjectAnimator jackChatboxOneAnimOn = AnimUtil.getAnimatorOn(jackChatboxOne, getActivity());
        ObjectAnimator storyChatboxOneAnimOn = AnimUtil.getAnimatorOn(storyChatboxOne, getActivity());

        //chat box off
        ObjectAnimator userChatboxOneAnimOff = AnimUtil.getAnimatorOff(userChatboxOne, getActivity());
        ObjectAnimator jackChatboxOneAnimOff = AnimUtil.getAnimatorOff(jackChatboxOne, getActivity());
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
            animatorSet.play(userChatboxOneAnimOn).after(playAgainButtonOff);
        }
        else {
            animatorSet.play(userChatboxOneAnimOn);
        }

        animatorSet.play(nullAnimator1).after(userChatboxOneAnimOn);
        animatorSet.play(userChatboxOneAnimOff).after(nullAnimator1);

        animatorSet.play(jackChatboxOneAnimOn).with(userChatboxOneAnimOff);
        animatorSet.play(nullAnimator2).after(jackChatboxOneAnimOn);
        animatorSet.play(jackChatboxOneAnimOff).after(nullAnimator2);

        animatorSet.play(storyChatboxOneAnimOn).with(jackChatboxOneAnimOff);
        nullAnimator3.setDuration(3000);
        animatorSet.play(nullAnimator4).after(storyChatboxOneAnimOn);
        animatorSet.play(storyChatboxOneAnimOff).after(nullAnimator4);

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
        knowledgePointPopup = new ClassroomChoiceTwoKnowledgePointPopup(this.getContext());
        knowledgePointPopup.showPopupWindow();
    }

    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

}