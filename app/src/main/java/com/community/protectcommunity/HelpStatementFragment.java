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
import android.widget.LinearLayout;

public class HelpStatementFragment extends Fragment implements View.OnClickListener{
    private View helpStatementFragment;
    private LinearLayout helpQuestionTwoLayout;
    private Button startButton;
    private Button backToParentsButton;

    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound
    private SharedPreferences sharedPref;
    public MusicService_S1 mServ;

    BackToMainScreenPopup popup;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helpStatementFragment = LayoutInflater.from(getActivity()).inflate(R.layout.help_statement_fragment,
                container, false);
        //Initialize
        soundID = SoundUtil.initSound(this.getActivity());
        setView();

        return helpStatementFragment;
    }

    public void setView () {
        //initialize the view
        startButton = (Button)helpStatementFragment.findViewById(R.id.help_start_btn);
        backToParentsButton = (Button)helpStatementFragment.findViewById(R.id.help_return_to_for_parents_screen_button);
        startButton.setOnClickListener(this);
        backToParentsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Fragment nextFragment;
        FragmentManager fragmentManager = getFragmentManager();
        nextFragment = new HelpQuestionOneFragment();
        switch (view.getId()) {
            case R.id.help_start_btn:
                SoundUtil.playSound(soundID);
                fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_in,R.animator.slide_in_opp,R.animator.slide_out_opp,
                       R.animator.slide_out).replace(R.id.help_change_area, nextFragment).commit();
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

    //initialize the return pop up window
    public void initPopupLayout() {
        popup = new BackToMainScreenPopup(this.getContext(), getActivity());
        popup.showPopupWindow();
    }

    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    public void setmServ(MusicService_S1 mServ) {
        this.mServ = mServ;
    }
}