package com.community.protectcommunity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HelpQuestionElevenFragment extends Fragment implements View.OnClickListener{
    private View helpQuestionElevenFragment;
    private Button yesButton;
    private Button backToParentsButton;
    private Button noButton;

    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound
    private SharedPreferences sharedPref;

    BackToMainScreenPopup popup;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helpQuestionElevenFragment = LayoutInflater.from(getActivity()).inflate(R.layout.help_question_eleven_fragment,
                container, false);
        //Initialize
        soundID = SoundUtil.initSound(this.getActivity());
        setView();

        return helpQuestionElevenFragment;
    }

    public void setView () {
        //initialize the view
        yesButton = (Button)helpQuestionElevenFragment.findViewById(R.id.help_q11_yes_button);
        noButton = (Button)helpQuestionElevenFragment.findViewById(R.id.help_q11_no_button);
        backToParentsButton = (Button)helpQuestionElevenFragment.findViewById(R.id.help_return_to_for_parents_screen_button);
        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);
        backToParentsButton.setOnClickListener(this);

        //set up the view
        sharedPref = getActivity().getSharedPreferences("help_function",Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View view) {
        int finalScore;
        switch (view.getId()) {
            case R.id.help_q11_yes_button:
                SoundUtil.playSound(soundID);
                saveQuesEleven("Yes");
                finalScore = calculateScore();
                showResultPage(finalScore);
                break;
            case R.id.help_q11_no_button:
                SoundUtil.playSound(soundID);
                saveQuesEleven("No");
                finalScore = calculateScore();
                showResultPage(finalScore);
                break;
            case R.id.help_return_to_for_parents_screen_button:
                SoundUtil.playSound(soundID);
                //might cause exception
                //mHomeWatcher.stopWatch();
                getParents();
                break;
            default:
                break;
        }
    }

    public void getParents(){
        Intent intent = new Intent(this.getContext(), ForParentsActivity.class);
        startActivity(intent);
        this.getActivity().finish();
    }

    public void getHome(){
        Intent intent = new Intent(this.getContext(), MainScreenActivity.class);
        this.getContext().startActivity(intent);
        this.getActivity().finish();
        //this.getContentView()
    }

    //save question four
    public void saveQuesEleven(String yesOrNo) {
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putString("questionEleven", yesOrNo);
        spEditor.apply();
    }

    //initialize the return pop up window
    public void initPopupLayout() {
        popup = new BackToMainScreenPopup(this.getContext(), getActivity());
        popup.showPopupWindow();
    }

    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    public int calculateScore() {
        int finalScore = 0;
        String question2 = sharedPref.getString("questionTwo", null);
        String question3 = sharedPref.getString("questionThree", null);
        String question4 = sharedPref.getString("questionFour", null);
        String question5 = sharedPref.getString("questionFive", null);
        String question6 = sharedPref.getString("questionSix", null);
        String question7 = sharedPref.getString("questionSeven", null);
        String question8 = sharedPref.getString("questionEight", null);
        String question9 = sharedPref.getString("questionNine", null);
        String question10 = sharedPref.getString("questionTen", null);
        String question11 = sharedPref.getString("questionEleven", null);
        if (question2 != null) {
            if (question2.equals("Yes")) {
                finalScore += 10;
            }
        }
        if (question3 != null) {
            if (question3.equals("Yes")) {
                finalScore += 10;
            }
        }
        if (question4 != null) {
            if (question4.equals("Yes")) {
                finalScore += 10;
            }
        }
        if (question5 != null) {
            if (question5.equals("Yes")) {
                finalScore += 10;
            }
        }
        if (question6 != null) {
            if (question6.equals("Yes")) {
                finalScore += 10;
            }
        }
        if (question7 != null) {
            if (question7.equals("Yes")) {
                finalScore += 10;
            }
        }
        if (question8 != null) {
            if (question8.equals("Yes")) {
                finalScore += 10;
            }
        }
        if (question9 != null) {
            if (question9.equals("Yes")) {
                finalScore += 10;
            }
        }
        if (question10 != null) {
            if (question10.equals("Yes")) {
                finalScore += 10;
            }
        }
        if (question11 != null) {
            if (question11.equals("Yes")) {
                finalScore += 10;
            }
        }
        System.out.println(finalScore);
        return finalScore;
    }

    //show result page according to score, and set up the link
    //save the final page to shared preference
    public void showResultPage(int finalScore) {
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putInt("finalScore", finalScore);
        spEditor.apply();
        HelpResultFragment nextFragment = new HelpResultFragment();
        FragmentManager fragmentManager = getFragmentManager();
        //change the background of help result fragment
        nextFragment.setScore(finalScore);
        //change the url link in help result fragment
        String ageGroup = sharedPref.getString("ageGroup", null);

        fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_in,R.animator.slide_in_opp,R.animator.slide_out_opp,
                R.animator.slide_out).replace(R.id.help_change_area, nextFragment).commit();
    }
}