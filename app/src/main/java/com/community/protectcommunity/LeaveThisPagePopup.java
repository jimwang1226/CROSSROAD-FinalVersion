package com.community.protectcommunity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;

import razerdp.basepopup.BasePopupWindow;

public class LeaveThisPagePopup extends BasePopupWindow implements View.OnClickListener{
    private Button popupYesButton;
    private Button popupNoButton;
    private View popupWindow;
    private Activity activity;
    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound
    private String url;
    public LeaveThisPagePopup(Context context, Activity activity, String url) {
        super(context);
        this.activity = activity;
        soundID = SoundUtil.initSound(activity);
        this.url = url;
    }

    @Override
    public View onCreateContentView() {
        popupWindow = createPopupById(R.layout.pop_up_window_leave_this_page);
        popupYesButton = (Button)popupWindow.findViewById(R.id.leave_this_page_yes_button);
        popupNoButton = (Button)popupWindow.findViewById(R.id.leave_this_page_no_button);
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
            case R.id.leave_this_page_yes_button:
                SoundUtil.playSound(soundID);
                dismissPopupLayout();
                if (!url.equals("")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    this.activity.startActivity(browserIntent);
                }
                System.gc();
                break;
            case R.id.leave_this_page_no_button:
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
