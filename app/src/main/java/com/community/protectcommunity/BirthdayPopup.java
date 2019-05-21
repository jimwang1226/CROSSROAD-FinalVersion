package com.community.protectcommunity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;

import razerdp.basepopup.BasePopupWindow;

public class BirthdayPopup extends BasePopupWindow implements View.OnClickListener{
    private Button popupYesButton;
    private Button popupNoButton;
    private View popupWindow;
    BirthdayFragment thisFragment;
    private int soundId;
    public BirthdayPopup(Context context, BirthdayFragment fragment) {
        super(context);
        thisFragment = fragment;
        soundId = SoundUtil.initSound(thisFragment.getActivity());

    }

    @Override
    public View onCreateContentView() {
        popupWindow = createPopupById(R.layout.pop_up_window_birthday_question);
        popupYesButton = (Button)popupWindow.findViewById(R.id.birthday_fragment_choice1);
        popupNoButton = (Button)popupWindow.findViewById(R.id.birthday_fragment_choice2);
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
        //Fragment nextFragment;
        //FragmentManager fragmentManager = thisFragment.getFragmentManager();
        SharedPreferences sharedPref = thisFragment.getActivity().getSharedPreferences("username_gender_choice",Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        switch (view.getId()) {
            case R.id.birthday_fragment_choice1:
                SoundUtil.playSound(soundId);
                spEditor.putString("question3", "YES");
                spEditor.apply();
                dismissPopupLayout();
                thisFragment.startStorylineThreadTwo();
                break;
            case R.id.birthday_fragment_choice2:
                SoundUtil.playSound(soundId);
                spEditor.putString("question3", "NO");
                spEditor.apply();
                dismissPopupLayout();
                thisFragment.startStorylineThreadTwo();
                break;
            default:
                break;
        }
    }

    //close the pop up window
    public void dismissPopupLayout() {
        this.dismiss();
    }
}
