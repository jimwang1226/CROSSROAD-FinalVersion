package com.community.protectcommunity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
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

public class MovieFragment extends Fragment implements View.OnClickListener{
    private View movieFragment;
    private View userAndGracieInsideView;
    private View lisaViewScary;
    private View lisaViewNormal;
    private View lisaViewUnhappy;
    private View storyChatboxOne;
    private View storyChatboxTwo;
    private View storyChatboxThree;
    private View gracieChatboxOne;
    private View gracieChatboxTwo;
    private View lisaChatboxOne;
    private View lisaChatboxTwo;
    private View lisaChatboxThree;
    private View userChatboxOne;
    private View userChatboxTwo;

    private Button nextButton;
    private Button backToMainscreenButton;
    private Button playAgainButton;
    private Button skipButton;
    private Button knowledgePointButton;

    private String gender;
    private Boolean isFirstTime = true;

    private int soundId;

    BackToMainScreenPopup popup;
    MoviePopup moviePopup;
    private AnimatorSet animatorSet = null;
    public int choice = 0;
    private Boolean hasOpenKnowledgePoint = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        movieFragment = LayoutInflater.from(getActivity()).inflate(R.layout.movie_fragment,
                container, false);
        //Initialize
        setView();
        soundId = SoundUtil.initSound(this.getActivity());

        //save to check point
        String fragmentName = this.getClass().getName();
        GameProgressUtil.saveCheckPoint(this.getActivity(), fragmentName);
        return movieFragment;
    }

    public void setView () {
        //initialize the view

        userAndGracieInsideView = movieFragment.findViewById(R.id.movie_user_and_gracie_inside_view);
        lisaViewScary = movieFragment.findViewById(R.id.movie_lisa_inside_view_scary);
        lisaViewNormal = movieFragment.findViewById(R.id.movie_lisa_inside_view_normal);
        lisaViewUnhappy = movieFragment.findViewById(R.id.movie_lisa_inside_view_unhappy);
        storyChatboxOne = movieFragment.findViewById(R.id.movie_story_chat_box_one);
        storyChatboxTwo = movieFragment.findViewById(R.id.movie_story_chat_box_two);
        storyChatboxThree = movieFragment.findViewById(R.id.movie_story_chat_box_three);
        gracieChatboxOne = movieFragment.findViewById(R.id.movie_gracie_chat_box_one);
        gracieChatboxTwo = movieFragment.findViewById(R.id.movie_gracie_chat_box_two);
        lisaChatboxOne = movieFragment.findViewById(R.id.movie_lisa_chat_box_one);
        lisaChatboxTwo = movieFragment.findViewById(R.id.movie_lisa_chat_box_two);
        lisaChatboxThree = movieFragment.findViewById(R.id.movie_lisa_chat_box_three);
        userChatboxOne = movieFragment.findViewById(R.id.movie_user_chat_box_one);
        userChatboxTwo = movieFragment.findViewById(R.id.movie_user_chat_box_two);

        knowledgePointButton = (Button) movieFragment.findViewById(R.id.knowledge_point_button_movie_fragment);
        nextButton = (Button)movieFragment.findViewById(R.id.next_button_movie_fragment);
        backToMainscreenButton = (Button)movieFragment.findViewById(R.id.return_to_mainscreen_button_movie_fragment);
        playAgainButton = (Button)movieFragment.findViewById(R.id.play_again_button_movie_fragment);
        skipButton = (Button)movieFragment.findViewById(R.id.skip_button_movie_fragment);


        //set up the view
        SharedPreferences sharedPref = getActivity().getSharedPreferences("username_gender_choice",Context.MODE_PRIVATE);
        gender = sharedPref.getString("gender", null);

        if ("MALE".equals(gender)) {
            userAndGracieInsideView.setBackground(getResources().getDrawable(R.drawable.movie_user_and_gracie_inside_view_boy,null));
        }
        else {
            userAndGracieInsideView.setBackground(getResources().getDrawable(R.drawable.movie_user_and_gracie_inside_view_girl,null));
        }

        nextButton.setOnClickListener(this);
        backToMainscreenButton.setOnClickListener(this);
        playAgainButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);
        knowledgePointButton.setOnClickListener(this);

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
            case R.id.next_button_movie_fragment:
                SoundUtil.playSound(soundId);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                if (!hasOpenKnowledgePoint) {
                    hasOpenKnowledgePoint = true;
                    initKnowledgePointPopupLayout();
                }
                else {
                    //go to game ending
                    getEnding();
                }
                break;
            case R.id.return_to_mainscreen_button_movie_fragment:
                SoundUtil.playSound(soundId);
                if (animatorSet != null) {
                    animatorSet.end();
                }
                initPopupLayout();
                break;
            case R.id.play_again_button_movie_fragment:
                SoundUtil.playSound(soundId);
                //final AnimatorSet animatorSetCopy = animatorSet;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       animatorSet.start();
                    }
                });
                break;
            case R.id.skip_button_movie_fragment:
                SoundUtil.playSound(soundId);
                animatorSet.end();
                break;
            case R.id.knowledge_point_button_movie_fragment:
                SoundUtil.playSound(soundId);
                hasOpenKnowledgePoint = true;
                initKnowledgePointPopupLayout();
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
        ObjectAnimator storyChatboxTwoAnimOn = AnimUtil.getAnimatorOn(storyChatboxTwo, getActivity());
        ObjectAnimator gracieChatboxOneAnimOn = AnimUtil.getAnimatorOn(gracieChatboxOne, getActivity());
        ObjectAnimator lisaChatboxOneAnimOn = AnimUtil.getAnimatorOn(lisaChatboxOne, getActivity());
        ObjectAnimator userChatboxOneAnimOn = AnimUtil.getAnimatorOn(userChatboxOne, getActivity());

        //chat box off
        ObjectAnimator storyChatboxOneAnimOff = AnimUtil.getAnimatorOff(storyChatboxOne, getActivity());
        ObjectAnimator storyChatboxTwoAnimOff = AnimUtil.getAnimatorOff(storyChatboxTwo, getActivity());
        ObjectAnimator gracieChatboxOneAnimOff = AnimUtil.getAnimatorOff(gracieChatboxOne, getActivity());
        ObjectAnimator lisaChatboxOneAnimOff = AnimUtil.getAnimatorOff(lisaChatboxOne, getActivity());
        ObjectAnimator userChatboxOneAnimOff = AnimUtil.getAnimatorOff(userChatboxOne, getActivity());

        //character on
        ObjectAnimator userAndGracieViewAnimOn = AnimUtil.getAnimatorOn(userAndGracieInsideView, getActivity());
        ObjectAnimator lisaScaryViewAnimOn = AnimUtil.getAnimatorOn(lisaViewScary, getActivity());
        ObjectAnimator playAgainButtonOff = AnimUtil.getPlayAgainAnimatorOff(playAgainButton, getActivity());
        ObjectAnimator playAgainButtonOn = AnimUtil.getPlayAgainAnimatorOn(playAgainButton, getActivity());

        //Null animator for gap
        ValueAnimator nullAnimator1 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator2 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator3 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator4 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator5 = AnimUtil.getNullAnimator();
        ValueAnimator nullAnimator6 = AnimUtil.getNullAnimator();


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

        animatorSet.play(userAndGracieViewAnimOn).after(storyChatboxOneAnimOff);
        animatorSet.play(lisaScaryViewAnimOn).with(userAndGracieViewAnimOn);

        animatorSet.play(lisaChatboxOneAnimOn).after(lisaScaryViewAnimOn);
        animatorSet.play(nullAnimator2).after(lisaChatboxOneAnimOn);
        animatorSet.play(lisaChatboxOneAnimOff).after(nullAnimator2);

        animatorSet.play(userChatboxOneAnimOn).with(lisaChatboxOneAnimOff);
        animatorSet.play(nullAnimator3).after(userChatboxOneAnimOn);
        animatorSet.play(userChatboxOneAnimOff).after(nullAnimator3);

        animatorSet.play(gracieChatboxOneAnimOn).with(userChatboxOneAnimOff);
        animatorSet.play(nullAnimator4).after(gracieChatboxOneAnimOn);
        animatorSet.play(gracieChatboxOneAnimOff).after(nullAnimator4);
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

    public void startStorylineChoiceOne() {
        if (getActivity() == null) {
            return;
        }
        //make a record for the choice
        choice = 1;
        //chat box on
        ObjectAnimator userChatboxTwoAnimOn = AnimUtil.getAnimatorOn(userChatboxTwo, getActivity());
        ObjectAnimator lisaChatboxTwoAnimOn = AnimUtil.getAnimatorOn(lisaChatboxTwo, getActivity());

        //chat box off
        ObjectAnimator userChatboxTwoAnimOff = AnimUtil.getAnimatorOff(userChatboxTwo, getActivity());
        ObjectAnimator lisaChatboxTwoAnimOff = AnimUtil.getAnimatorOff(lisaChatboxTwo, getActivity());

        //character on
        ObjectAnimator lisaNormalViewAnimOn = AnimUtil.getAnimatorOn(lisaViewNormal, getActivity());
        ObjectAnimator lisaScaryViewAnimOn = AnimUtil.getAnimatorOn(lisaViewScary, getActivity());
        ObjectAnimator userAndGracieViewAnimOn = AnimUtil.getAnimatorOn(userAndGracieInsideView, getActivity());

        //character off
        ObjectAnimator lisaScaryViewAnimOff = AnimUtil.getAnimatorOff(lisaViewScary, getActivity());
        ObjectAnimator lisaNormalViewAnimOff = AnimUtil.getAnimatorOff(lisaViewNormal, getActivity());
        ObjectAnimator userAndGracieViewAnimOff = AnimUtil.getAnimatorOff(userAndGracieInsideView, getActivity());

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
            animatorSet.play(userChatboxTwoAnimOn).after(playAgainButtonOff);
        }
        else {
            animatorSet.play(userChatboxTwoAnimOn);
        }

        animatorSet.play(userAndGracieViewAnimOn).with(userChatboxTwoAnimOn);
        animatorSet.play(lisaScaryViewAnimOn).with(userChatboxTwoAnimOn);
        animatorSet.play(nullAnimator1).after(userChatboxTwoAnimOn);
        animatorSet.play(userChatboxTwoAnimOff).after(nullAnimator1);

        animatorSet.play(lisaChatboxTwoAnimOn).with(userChatboxTwoAnimOff);
        animatorSet.play(lisaScaryViewAnimOff).with(lisaChatboxTwoAnimOn);
        animatorSet.play(lisaNormalViewAnimOn).with(lisaChatboxTwoAnimOn);
        animatorSet.play(nullAnimator2).after(lisaChatboxTwoAnimOn);
        animatorSet.play(lisaChatboxTwoAnimOff).after(nullAnimator2);

        animatorSet.play(lisaNormalViewAnimOff).after(lisaChatboxTwoAnimOff);
        animatorSet.play(userAndGracieViewAnimOff).with(lisaNormalViewAnimOff);

        animatorSet.play(playAgainButtonOn).with(userAndGracieViewAnimOff);

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
                                showTwoButton.play(nextButtonOn);
                            }
                            showTwoButton.start();
                        }
                    }
                });
            }
        });
    }

    public void startStorylineChoiceTwo() {
        if (getActivity() == null) {
            return;
        }
        //make a record for the choice
        choice = 2;
        //chat box on
        ObjectAnimator gracieChatboxTwoAnimOn = AnimUtil.getAnimatorOn(gracieChatboxTwo, getActivity());
        ObjectAnimator lisaChatboxThreeAnimOn = AnimUtil.getAnimatorOn(lisaChatboxThree, getActivity());
        ObjectAnimator storyChatboxThreeAnimOn = AnimUtil.getAnimatorOn(storyChatboxThree, getActivity());

        //chat box off
        ObjectAnimator gracieChatboxTwoAnimOff = AnimUtil.getAnimatorOff(gracieChatboxTwo, getActivity());
        ObjectAnimator lisaChatboxThreeAnimOff = AnimUtil.getAnimatorOff(lisaChatboxThree, getActivity());
        ObjectAnimator storyChatboxThreeAnimOff = AnimUtil.getAnimatorOff(storyChatboxThree, getActivity());

        //character on
        ObjectAnimator lisaNormalViewAnimOn = AnimUtil.getAnimatorOn(lisaViewNormal, getActivity());
        ObjectAnimator lisaUnhappyViewAnimOn = AnimUtil.getAnimatorOn(lisaViewUnhappy, getActivity());
        ObjectAnimator lisaScaryViewAnimOn = AnimUtil.getAnimatorOn(lisaViewUnhappy, getActivity());
        ObjectAnimator userAndGracieViewAnimOn = AnimUtil.getAnimatorOn(userAndGracieInsideView, getActivity());

        //character off
        ObjectAnimator lisaScaryViewAnimOff = AnimUtil.getAnimatorOff(lisaViewScary, getActivity());
        ObjectAnimator lisaNormalViewAnimOff = AnimUtil.getAnimatorOff(lisaViewNormal, getActivity());
        ObjectAnimator lisaUnhappyViewAnimOff = AnimUtil.getAnimatorOff(lisaViewUnhappy, getActivity());
        ObjectAnimator userAndGracieViewAnimOff = AnimUtil.getAnimatorOff(userAndGracieInsideView, getActivity());

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
            animatorSet.play(lisaScaryViewAnimOn).after(playAgainButtonOff);
        }
        else {
            animatorSet.play(lisaScaryViewAnimOn);
        }
        animatorSet.play(userAndGracieViewAnimOn).with(lisaScaryViewAnimOn);

        animatorSet.play(lisaScaryViewAnimOff).after(userAndGracieViewAnimOn);
        animatorSet.play(lisaNormalViewAnimOn).with(lisaScaryViewAnimOff);

        animatorSet.play(lisaChatboxThreeAnimOn).with(lisaNormalViewAnimOn);
        animatorSet.play(nullAnimator1).after(lisaChatboxThreeAnimOn);
        animatorSet.play(lisaChatboxThreeAnimOff).after(nullAnimator1);

        animatorSet.play(gracieChatboxTwoAnimOn).with(lisaChatboxThreeAnimOff);
        animatorSet.play(nullAnimator2).after(gracieChatboxTwoAnimOn);
        animatorSet.play(gracieChatboxTwoAnimOff).after(nullAnimator2);

        animatorSet.play(storyChatboxThreeAnimOn).after(gracieChatboxTwoAnimOff);
        animatorSet.play(lisaNormalViewAnimOff).with(storyChatboxThreeAnimOn);
        animatorSet.play(lisaUnhappyViewAnimOn).with(storyChatboxThreeAnimOn);
        animatorSet.play(nullAnimator3).after(storyChatboxThreeAnimOn);
        animatorSet.play(storyChatboxThreeAnimOff).after(nullAnimator3);

        animatorSet.play(lisaUnhappyViewAnimOff).after(storyChatboxThreeAnimOff);
        animatorSet.play(userAndGracieViewAnimOff).with(lisaUnhappyViewAnimOff);

        animatorSet.play(playAgainButtonOn).with(lisaUnhappyViewAnimOff);

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
        moviePopup = new MoviePopup(this.getContext(), this);
        moviePopup.showPopupWindow();
    }

    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    //initialize the knowledge point pop up window
    public void initKnowledgePointPopupLayout() {
        System.out.println("mmmmmmmm"+choice);
        //knowledgePointPopup = new MovieKnowledgePointPopup(this.getContext(), choice);
        //set up the knowledge background according to choice
        if (choice == 1) {
            MovieKnowledgePointPopup knowledgePointPopup = new MovieKnowledgePointPopup(this.getContext());
            knowledgePointPopup.showPopupWindow();
        }
        else if (choice == 2){
            MovieKnowledgePointPopupChoice2 knowledgePointPopup = new MovieKnowledgePointPopupChoice2(this.getContext());
            knowledgePointPopup.showPopupWindow();
        }
    }

    public void getEnding(){
        Intent intent = new Intent(this.getActivity(), GameEndingActivity.class);
        startActivity(intent);
        this.getActivity().finish();
    }
}