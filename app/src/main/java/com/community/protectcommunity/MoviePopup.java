package com.community.protectcommunity;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;

import razerdp.basepopup.BasePopupWindow;

public class MoviePopup extends BasePopupWindow implements View.OnClickListener{
    private Button popupYesButton;
    private Button popupNoButton;
    private View popupWindow;
    MovieFragment thisFragment;
    private int soundId;
    public MoviePopup(Context context, MovieFragment fragment) {
        super(context);
        thisFragment = fragment;
        soundId = SoundUtil.initSound(thisFragment.getActivity());

    }

    @Override
    public View onCreateContentView() {
        popupWindow = createPopupById(R.layout.pop_up_window_movie_question);
        popupYesButton = (Button)popupWindow.findViewById(R.id.movie_fragment_choice1);
        popupNoButton = (Button)popupWindow.findViewById(R.id.movie_fragment_choice2);
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
            case R.id.movie_fragment_choice1:
                SoundUtil.playSound(soundId);
                spEditor.putString("question4", "YES");
                spEditor.apply();
                dismissPopupLayout();
                thisFragment.startStorylineChoiceOne();
                thisFragment.choice = 1;
                break;
            case R.id.movie_fragment_choice2:
                SoundUtil.playSound(soundId);
                spEditor.putString("question4", "NO");
                spEditor.apply();
                dismissPopupLayout();
                thisFragment.startStorylineChoiceTwo();
                thisFragment.choice = 2;
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
