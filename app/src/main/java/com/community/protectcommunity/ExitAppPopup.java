package com.community.protectcommunity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;

import razerdp.basepopup.BasePopupWindow;

public class ExitAppPopup extends BasePopupWindow implements View.OnClickListener{
    private Button popupYesButton;
    private Button popupNoButton;
    private View popupWindow;
    private MainScreenActivity activity;
    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound
    public ExitAppPopup(Context context, MainScreenActivity activity) {
        super(context);
        this.activity = activity;
        soundID = SoundUtil.initSound(activity);
    }

    @Override
    public View onCreateContentView() {
        popupWindow = createPopupById(R.layout.pop_up_window_exit_app);
        popupYesButton = (Button)popupWindow.findViewById(R.id.exit_app_yes_button);
        popupNoButton = (Button)popupWindow.findViewById(R.id.exit_app_no_button);
        popupYesButton.setOnClickListener(this);
        popupNoButton.setOnClickListener(this);
        return popupWindow;
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return getDefaultScaleAnimation(true);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getDefaultScaleAnimation(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exit_app_yes_button:
                SoundUtil.playSound(soundID);
                dismissPopupLayout();
                activity.finish();
                System.exit(0);
                System.gc();
                break;
            case R.id.exit_app_no_button:
                SoundUtil.playSound(soundID);
                dismissPopupLayout();
                break;
            default:
                break;
        }
    }


    //close the pop up window
    public void dismissPopupLayout() {
        System.gc();
        this.dismiss();
    }
}
