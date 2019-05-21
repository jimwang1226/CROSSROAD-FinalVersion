package com.community.protectcommunity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LineConnectionGameTaskTwoFragment extends Fragment implements View.OnClickListener{
    private View lineConnTaskTwoFragment;
    private View textOne;
    private View textTwo;
    private View textThree;
    private View picOne;
    private View picTwo;
    private View picThree;
    private View line11;
    private View line12;
    private View line13;
    private View line21;
    private View line22;
    private View line23;
    private View line31;
    private View line32;
    private View line33;

    private Button backToMainscreenButton;
    private Button nextScreenButton;
    private Button replayButton;

    private Thread uiThread;
    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound

    public MusicService_S1 mServ;
    BackToMainScreenPopup popup;

    private Boolean isTextOneClicked = false;
    private Boolean isTextTwoClicked = false;
    private Boolean isTextThreeClicked = false;
    private Boolean isPicOneClicked = false;
    private Boolean isPicTwoClicked = false;
    private Boolean isPicThreeClicked = false;
    private Boolean isTextClicked = false;
    private Boolean isPicClicked = false;

    private List<Integer> connectLine = new ArrayList<Integer>();
    private Boolean isAlreadyGetRightAnswer = false;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lineConnTaskTwoFragment = LayoutInflater.from(getActivity()).inflate(R.layout.line_conn_task_two_fragment,
                container, false);
        //Initialize
        setView();

        return lineConnTaskTwoFragment;
    }

    public void setView () {
        //initialize the view
        textOne = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_text_one);
        textTwo = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_text_two);
        textThree = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_text_three);
        picOne = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_pic_one);
        picTwo = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_pic_two);
        picThree = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_pic_three);
        line11 = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_line_11);
        line12 = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_line_12);
        line13 = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_line_13);
        line21 = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_line_21);
        line22 = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_line_22);
        line23 = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_line_23);
        line31 = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_line_31);
        line32 = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_line_32);
        line33 = lineConnTaskTwoFragment.findViewById(R.id.line_conn_task_two_line_33);
        backToMainscreenButton = (Button) lineConnTaskTwoFragment.findViewById(R.id.return_to_mainscreen_button_line_conn_task_two_fragment);
        nextScreenButton = (Button) lineConnTaskTwoFragment.findViewById(R.id.next_button_line_conn_task_two_fragment);
        replayButton = (Button) lineConnTaskTwoFragment.findViewById(R.id.replay_button_line_conn_task_two);

        textOne.setOnClickListener(this);
        textTwo.setOnClickListener(this);
        textThree.setOnClickListener(this);
        picOne.setOnClickListener(this);
        picTwo.setOnClickListener(this);
        picThree.setOnClickListener(this);
        backToMainscreenButton.setOnClickListener(this);
        nextScreenButton.setOnClickListener(this);
        replayButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Fragment nextFragment;
        Resources res = getResources();
        switch (view.getId()) {
            case R.id.return_to_mainscreen_button_line_conn_task_two_fragment:
                SoundUtil.playSound(soundID);
                getMiniGame();
                // mServ.stopMusic();
                break;
            case R.id.next_button_line_conn_task_two_fragment:
                SoundUtil.playSound(soundID);
                if (!isAlreadyGetRightAnswer) {
                    if (tellRightAnswer()) {
                        isAlreadyGetRightAnswer = true;
                        //pop up right answer pop up window
                        initRightPopupLayout();
                    }
                    else {
                        //pop up wrong answer pop up window
                        initWrongPopupLayout();
                    }
                }
                else{
                     nextFragment = new LineConnectionGameEndingFragment();
                     FragmentManager fragmentManager = getFragmentManager();
                     fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_in,R.animator.slide_in_opp,R.animator.slide_out_opp,
                     R.animator.slide_out).replace(R.id.line_conn_game_change_area, nextFragment).commit();
                }
                break;
            case R.id.replay_button_line_conn_task_two:
                nextFragment = new LineConnectionGameTaskTwoFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.line_conn_game_change_area, nextFragment).commit();
                break;
            case R.id.line_conn_task_two_text_one:
                SoundUtil.playSound(soundID);
                if (isTextOneClicked) {
                    Context context = this.getActivity().getApplicationContext();
                    CharSequence text = getString(R.string.choice_has_been_taken);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                }
                if (!isTextClicked) {
                    isTextOneClicked = true;
                    isTextClicked = true;
                    Drawable drawable = res.getDrawable(R.drawable.line_conn_task_two_text_1_clicked, null);
                    textOne.setBackground(drawable);
                    //11 means left & first
                    connectLine.add(11);
                }
                else {
                    Context context = this.getActivity().getApplicationContext();
                    CharSequence text = getString(R.string.please_choose_from_right);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                break;
            case R.id.line_conn_task_two_text_two:
                SoundUtil.playSound(soundID);
                if (isTextTwoClicked) {
                    Context context = this.getActivity().getApplicationContext();
                    CharSequence text = getString(R.string.choice_has_been_taken);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                }
                if (!isTextClicked) {
                    isTextTwoClicked = true;
                    isTextClicked = true;
                    Drawable drawable = res.getDrawable(R.drawable.line_conn_task_two_text_2_clicked, null);
                    textTwo.setBackground(drawable);
                    //12 means left & second
                    connectLine.add(12);
                }
                else {
                    Context context = this.getActivity().getApplicationContext();
                    CharSequence text = getString(R.string.please_choose_from_right);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                break;
            case R.id.line_conn_task_two_text_three:
                SoundUtil.playSound(soundID);
                if (isTextThreeClicked) {
                    Context context = this.getActivity().getApplicationContext();
                    CharSequence text = getString(R.string.choice_has_been_taken);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                }
                if (!isTextClicked) {
                    isTextThreeClicked = true;
                    isTextClicked = true;
                    Drawable drawable = res.getDrawable(R.drawable.line_conn_task_two_text_3_clicked, null);
                    textThree.setBackground(drawable);
                    //13 means left & third
                    connectLine.add(13);
                }
                else {
                    Context context = this.getActivity().getApplicationContext();
                    CharSequence text = getString(R.string.please_choose_from_right);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                break;
            case R.id.line_conn_task_two_pic_one:
                SoundUtil.playSound(soundID);
                if (isPicOneClicked) {
                    Context context = this.getActivity().getApplicationContext();
                    CharSequence text = getString(R.string.choice_has_been_taken);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                }
                if (!isPicClicked) {
                    isPicOneClicked = true;
                    isPicClicked = true;
                    Drawable drawable = res.getDrawable(R.drawable.line_conn_task_two_pic_1_clicked, null);
                    picOne.setBackground(drawable);
                    //21 means right & first
                    connectLine.add(21);
                }
                else {
                    Context context = this.getActivity().getApplicationContext();
                    CharSequence text = getString(R.string.please_choose_from_left);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                break;
            case R.id.line_conn_task_two_pic_two:
                SoundUtil.playSound(soundID);
                if (isPicTwoClicked) {
                    Context context = this.getActivity().getApplicationContext();
                    CharSequence text = getString(R.string.choice_has_been_taken);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                }
                if (!isPicClicked) {
                    isPicTwoClicked = true;
                    isPicClicked = true;
                    Drawable drawable = res.getDrawable(R.drawable.line_conn_task_two_pic_2_clicked, null);
                    picTwo.setBackground(drawable);
                    //22 means right & second
                    connectLine.add(22);
                }
                else {
                    Context context = this.getActivity().getApplicationContext();
                    CharSequence text = getString(R.string.please_choose_from_left);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                break;
            case R.id.line_conn_task_two_pic_three:
                SoundUtil.playSound(soundID);
                if (isPicThreeClicked) {
                    Context context = this.getActivity().getApplicationContext();
                    CharSequence text = getString(R.string.choice_has_been_taken);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                }
                if (!isPicClicked) {
                    isPicThreeClicked = true;
                    isPicClicked = true;
                    Drawable drawable = res.getDrawable(R.drawable.line_conn_task_two_pic_3_clicked, null);
                    picThree.setBackground(drawable);
                    //23 means right & second
                    connectLine.add(23);
                }
                else {
                    Context context = this.getActivity().getApplicationContext();
                    CharSequence text = getString(R.string.please_choose_from_left);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                break;
            default:
                break;
        }
        showLine();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    public void setmServ(MusicService_S1 mServ) {
        this.mServ = mServ;
    }

    //initialize the pop up window
    public void initPopupLayout() {
        popup = new BackToMainScreenPopup(this.getContext(), getActivity());
        popup.showPopupWindow();
    }

    //go back to mini game activity
    public void getMiniGame(){
        Intent intent = new Intent(this.getActivity(), MiniGameActivity.class);
        startActivity(intent);
        //GameProgressUtil.setGameProgressToNull(this.getActivity());
        this.getActivity().finish();
    }

    //show line
    public void showLine() {
        if (isTextClicked && isPicClicked) {
            isTextClicked = false;
            isPicClicked = false;
            if ((connectLine.get(0) == 11 && connectLine.get(1) == 21) ||
                    (connectLine.get(1) == 11 && connectLine.get(0) == 21)) {
                line11.setVisibility(View.VISIBLE);
            }
            else if ((connectLine.get(0) == 11 && connectLine.get(1) == 22) ||
                    (connectLine.get(1) == 11 && connectLine.get(0) == 22)) {
                line12.setVisibility(View.VISIBLE);
            }
            else if ((connectLine.get(0) == 11 && connectLine.get(1) == 23) ||
                    (connectLine.get(1) == 11 && connectLine.get(0) == 23)) {
                line13.setVisibility(View.VISIBLE);
            }
            else if ((connectLine.get(0) == 12 && connectLine.get(1) == 21) ||
                    (connectLine.get(1) == 12 && connectLine.get(0) == 21)) {
                line21.setVisibility(View.VISIBLE);
            }
            else if ((connectLine.get(0) == 12 && connectLine.get(1) == 22) ||
                    (connectLine.get(1) == 12 && connectLine.get(0) == 22)) {
                line22.setVisibility(View.VISIBLE);
            }
            else if ((connectLine.get(0) == 12 && connectLine.get(1) == 23) ||
                    (connectLine.get(1) == 12 && connectLine.get(0) == 23)) {
                line23.setVisibility(View.VISIBLE);
            }
            else if ((connectLine.get(0) == 13 && connectLine.get(1) == 21) ||
                    (connectLine.get(1) == 13 && connectLine.get(0) == 21)) {
                line31.setVisibility(View.VISIBLE);
            }
            else if ((connectLine.get(0) == 13 && connectLine.get(1) == 22) ||
                    (connectLine.get(1) == 13 && connectLine.get(0) == 22)) {
                line32.setVisibility(View.VISIBLE);
            }
            else if ((connectLine.get(0) == 13 && connectLine.get(1) == 23) ||
                    (connectLine.get(1) == 13 && connectLine.get(0) == 23)) {
                line33.setVisibility(View.VISIBLE);
            }
            connectLine = new ArrayList<Integer>();

        }
    }

    //tell right answer
    public boolean tellRightAnswer() {
        if (line13.getVisibility() == View.VISIBLE
            && line22.getVisibility() == View.VISIBLE
            && line31.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    //init right pop up
    public void initRightPopupLayout() {
        LineConnectionGameRightAnswerPopup popup = new LineConnectionGameRightAnswerPopup(this.getActivity());
        popup.showPopupWindow();
    }

    //init wrong pop up
    public void initWrongPopupLayout() {
        LineConnectionGameWrongAnswerPopup popup = new LineConnectionGameWrongAnswerPopup(this.getActivity());
        popup.showPopupWindow();
    }
}