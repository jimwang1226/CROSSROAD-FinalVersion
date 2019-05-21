package com.community.protectcommunity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class DrivingFragment extends Fragment implements View.OnClickListener {
    private View drivingFragment;
    private View momChatboxOne;
    private View momChatboxTwo;
    private View momChatboxThree;
    private View userChatboxOne;
    private View userChatboxTwo;
    private Button nextButton;
    private Button backToMainscreenButton;
    private Button playAgainButton;
    private Button skipButton;
    private Button knowledgePointButton;
    private View car;
    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound

    BackToMainScreenPopup popup;
    KnowledgePointPopup knowledgePointPopup;
    Boolean isClickNextButtonFirstTime = true;

    private AnimatorSet animatorSet = null;
    boolean isFirstTime = true;
    private Thread uiThread;

    private SharedPreferences sharedPref;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        drivingFragment = LayoutInflater.from(getActivity()).inflate(R.layout.driving_fragment,
                container, false);
        //Initialize
        setView();
        //set animation
        //AnimUtil.setShowAnimation(introductionLayout, 5000, mShowAnimation);
        soundID = SoundUtil.initSound(this.getActivity());

        //save to check point
        String fragmentName = this.getClass().getName();
        GameProgressUtil.saveCheckPoint(this.getActivity(), fragmentName);

        return drivingFragment;

    }

    public void setView() {
        //initialize the view
        momChatboxOne = drivingFragment.findViewById(R.id.driving_fragment_mom_chat_box_one);
        momChatboxTwo = drivingFragment.findViewById(R.id.driving_fragment_mom_chat_box_two);
        momChatboxThree = drivingFragment.findViewById(R.id.driving_fragment_mom_chat_box_three);
        userChatboxOne = drivingFragment.findViewById(R.id.driving_fragment_user_chat_box_one);
        userChatboxTwo = drivingFragment.findViewById(R.id.driving_fragment_user_chat_box_two);
        car = drivingFragment.findViewById(R.id.driving_fragment_car);
        nextButton = (Button) drivingFragment.findViewById(R.id.next_button_driving_fragment);
        backToMainscreenButton = (Button) drivingFragment.findViewById(R.id.return_to_mainscreen_button_driving_fragment);
        playAgainButton = (Button) drivingFragment.findViewById(R.id.play_again_button_driving_fragment);
        skipButton = (Button) drivingFragment.findViewById(R.id.skip_button_driving_fragment);
        knowledgePointButton = (Button) drivingFragment.findViewById(R.id.knowledge_point_button_driving_fragment);

        //set up the view
        SharedPreferences sharedPref = getActivity().getSharedPreferences("username_gender_choice", Context.MODE_PRIVATE);
        String gender = sharedPref.getString("gender", null);
        nextButton.setOnClickListener(this);
        backToMainscreenButton.setOnClickListener(this);
        playAgainButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);
        knowledgePointButton.setOnClickListener(this);

        if ("MALE".equals(gender)) {
            car.setBackground(getResources().getDrawable(R.drawable.car, null));
        } else {
            car.setBackground(getResources().getDrawable(R.drawable.car_girl, null));
        }

        //start the storyline, delay play it because there is a screen switch animation
        //otherwise it would stuck
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startStorylineThread();
            }
        }, 500);
    }

    @Override
    public void onClick(View view) {
        Fragment nextFragment;
        switch (view.getId()) {
            case R.id.next_button_driving_fragment:
                SoundUtil.playSound(soundID);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                if (isClickNextButtonFirstTime) {
                    showKnowledgePoint();
                    isClickNextButtonFirstTime = false;
                }
                else {
                    nextFragment = new FrontGateFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_in, R.animator.slide_in_opp, R.animator.slide_out_opp,
                            R.animator.slide_out).replace(R.id.game_change_area, nextFragment).commit();
                }
                break;
            case R.id.return_to_mainscreen_button_driving_fragment:
                SoundUtil.playSound(soundID);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                initPopupLayout();
                break;
            case R.id.play_again_button_driving_fragment:
                SoundUtil.playSound(soundID);
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
                /**
                 new Handler().postDelayed(new Runnable() {
                @Override public void run() {

                animatorSetCopy.start();

                }
                }, 0);
                 */
                break;
            case R.id.skip_button_driving_fragment:
                SoundUtil.playSound(soundID);
                animatorSet.end();
                break;
            case R.id.knowledge_point_button_driving_fragment:
                SoundUtil.playSound(soundID);
                showKnowledgePoint();
                break;
            default:
                break;
        }
    }

    public void startStorylineThread() {
        if (getActivity() == null) {
            return;
        }
        //mom chat box one on
        ObjectAnimator momChatboxOneAnimShow = AnimUtil.getAnimatorOn(momChatboxOne, getActivity());

        //mom chat box one off
        ObjectAnimator momChatboxOneAnimOff = AnimUtil.getAnimatorOff(momChatboxOne, getActivity());

        //user chat box one on
        ObjectAnimator userChatboxOneAnimShow = AnimUtil.getAnimatorOn(userChatboxOne, getActivity());

        //user chat box one off
        ObjectAnimator userChatboxOneAnimOff = AnimUtil.getAnimatorOff(userChatboxOne, getActivity());

        //mom chat box two on
        ObjectAnimator momChatboxTwoAnimShow = AnimUtil.getAnimatorOn(momChatboxTwo, getActivity());

        //mom chat box two off
        ObjectAnimator momChatboxTwoAnimOff = AnimUtil.getAnimatorOff(momChatboxTwo, getActivity());

        //user chat box two on
        ObjectAnimator userChatboxTwoAnimShow = AnimUtil.getAnimatorOn(userChatboxTwo, getActivity());

        //user chat box two off
        ObjectAnimator userChatboxTwoAnimOff = AnimUtil.getAnimatorOff(userChatboxTwo, getActivity());

        //mom chat box three on
        ObjectAnimator momChatboxThreeAnimShow = AnimUtil.getAnimatorOn(momChatboxThree, getActivity());

        //mom chat box three off
        ObjectAnimator momChatboxThreeAnimOff = AnimUtil.getAnimatorOff(momChatboxThree, getActivity());

        //play again button on
        ObjectAnimator playAgainButtonOn = AnimUtil.getPlayAgainAnimatorOn(playAgainButton, getActivity());

        //play again button off
        ObjectAnimator playAgainButtonOff = AnimUtil.getPlayAgainAnimatorOff(playAgainButton, getActivity());

        //next button on
        final ObjectAnimator nextButtonOn = AnimUtil.getPlayAgainAnimatorOn(nextButton, getActivity());

        //knowledge point button on
        final ObjectAnimator knowledgePointButtonOn = AnimUtil.getPlayAgainAnimatorOn(knowledgePointButton, getActivity());

        //Null animator for gap
        ValueAnimator nullAnimator = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator1 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator2 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator3 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator4 = AnimUtil.getNullAnimator();

        //setup animation
        animatorSet = new AnimatorSet();

        if (playAgainButton.getVisibility() == View.VISIBLE) {
            animatorSet.play(playAgainButtonOff);
            animatorSet.play(momChatboxOneAnimShow).after(playAgainButtonOff);
        } else {
            animatorSet.play(momChatboxOneAnimShow);
        }
        try {

            animatorSet.play(nullAnimator).after(momChatboxOneAnimShow);
            animatorSet.play(userChatboxOneAnimShow).after(nullAnimator);
            animatorSet.play(momChatboxOneAnimOff).with(userChatboxOneAnimShow);

            animatorSet.play(nullAnimator1).after(momChatboxOneAnimOff);
            animatorSet.play(userChatboxOneAnimOff).after(nullAnimator1);
            animatorSet.play(momChatboxTwoAnimShow).with(userChatboxOneAnimOff);

            animatorSet.play(nullAnimator2).after(momChatboxTwoAnimShow);
            animatorSet.play(momChatboxTwoAnimOff).after(nullAnimator2);
            animatorSet.play(userChatboxTwoAnimShow).with(momChatboxTwoAnimOff);

            animatorSet.play(nullAnimator3).after(userChatboxTwoAnimShow);
            animatorSet.play(userChatboxTwoAnimOff).after(nullAnimator3);
            animatorSet.play(momChatboxThreeAnimShow).with(userChatboxTwoAnimOff);

            animatorSet.play(nullAnimator4).after(momChatboxThreeAnimShow);
            animatorSet.play(momChatboxThreeAnimOff).after(nullAnimator4);

            animatorSet.play(playAgainButtonOn).after(momChatboxThreeAnimOff);
        } catch (Exception e) {
            return;
        }

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
                                    AnimatorSet showNextButton = new AnimatorSet();
                                    if(knowledgePointButton.getVisibility() == View.GONE) {
                                        showNextButton.play(knowledgePointButtonOn);
                                    }
                                    if (nextButton.getVisibility() == View.GONE) {
                                        showNextButton.play(nextButtonOn);
                                    }
                                    showNextButton.start();
                                }
                            }
                        });
                    }
                });
            }
        };
        uiThread.start();


    }

    //initialize the pop up window
    public void initPopupLayout() {
        popup = new BackToMainScreenPopup(this.getContext(), getActivity());
        popup.showPopupWindow();
    }

    public void showKnowledgePoint() {
        knowledgePointPopup = new KnowledgePointPopup(this.getContext());
        knowledgePointPopup.showPopupWindow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

}