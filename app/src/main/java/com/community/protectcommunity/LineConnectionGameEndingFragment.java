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
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LineConnectionGameEndingFragment extends Fragment implements View.OnClickListener{
    private View lineConnectionGameEndingFragment;
    private Button backToMainscreenButton;

    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound
    BackToMainScreenPopup popup;

    public MusicService_S1 mServ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lineConnectionGameEndingFragment = LayoutInflater.from(getActivity()).inflate(R.layout.line_conn_ending_fragment,
                container, false);
        //Initialize
        setView();
        soundID = SoundUtil.initSound(this.getActivity());

        return lineConnectionGameEndingFragment;
    }

    public void setView () {
        //initialize the view
        backToMainscreenButton = (Button)lineConnectionGameEndingFragment.findViewById(R.id.return_to_mainscreen_button_line_conn_ending_fragment);
        backToMainscreenButton.setOnClickListener(this);
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
        GameProgressUtil.setGameProgressToNull(this.getActivity());
        this.getActivity().finish();
    }

    @Override
    public void onClick(View view) {
        Fragment nextFragment;
        switch (view.getId()) {
            case R.id.return_to_mainscreen_button_line_conn_ending_fragment:
                SoundUtil.playSound(soundID);
                getMiniGame();
                // mServ.stopMusic();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    public void setmServ(MusicService_S1 mServ) {
        this.mServ = mServ;
    }
}