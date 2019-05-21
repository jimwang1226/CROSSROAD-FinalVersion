package com.community.protectcommunity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.rpc.Help;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.grpc.internal.SharedResourceHolder;

public class HelpResultFragment extends Fragment implements View.OnClickListener{
    private View helpResultFragment;
    private Button backToParentsButton;
    private Button searchButton;
    private EditText postcodeContent;
    private View helpResultText;
    private TextView linkOne;
    private TextView linkTwo;
    private int score = 0;
    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound
    SharedPreferences sharedPref;
    String validPostcode = "";

    BackToMainScreenPopup popup;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helpResultFragment = LayoutInflater.from(getActivity()).inflate(R.layout.help_result_fragment,
                container, false);
        //Initialize
        soundID = SoundUtil.initSound(this.getActivity());

        setView();
        return helpResultFragment;
    }

    public void setView () {
        //initialize the view
        postcodeContent = (EditText) helpResultFragment.findViewById(R.id.help_result_postcode);
        postcodeContent.requestFocus();
        backToParentsButton = (Button) helpResultFragment.findViewById(R.id.help_return_to_for_parents_screen_button);
        searchButton = (Button) helpResultFragment.findViewById(R.id.help_search_btn);
        helpResultText = helpResultFragment.findViewById(R.id.help_result_text);
        linkOne = (TextView) helpResultFragment.findViewById(R.id.help_result_article_link);
        linkTwo = (TextView) helpResultFragment.findViewById(R.id.help_result_article_link_two);
        backToParentsButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        changeBackground();
        changeLinks();
        //set up the view
        sharedPref = getActivity().getSharedPreferences("help_function",Context.MODE_PRIVATE);
        postcodeContent.setFocusable(true);
        postcodeContent.setFocusableInTouchMode(true);
        postcodeContent.requestFocus();
    }

    @Override
    public void onClick(View view) {
        Fragment nextFragment;
        FragmentManager fragmentManager = getFragmentManager();
        nextFragment = new HelpSearchResultFragment();
        switch (view.getId()) {

            case R.id.help_return_to_for_parents_screen_button:
                SoundUtil.playSound(soundID);
                //might cause exception
                //mHomeWatcher.stopWatch();
                getParents();
                break;
            case R.id.help_search_btn:
                SoundUtil.playSound(soundID);
                if (isPostcodeValid()) {
                    //add to shared preference and switch fragment
                    SharedPreferences.Editor spEditor = sharedPref.edit();
                    if(!validPostcode.equals("")) {
                        spEditor.putString("postcode", validPostcode);
                        System.out.println("valid postcode" + validPostcode);
                        spEditor.apply();
                        //switch fragment
                        fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_in,R.animator.slide_in_opp,R.animator.slide_out_opp,
                                R.animator.slide_out).replace(R.id.help_change_area, nextFragment).commit();
                    }
                }
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

    //change the background of this fragment
    public void changeBackground() {
        if (score <= 30) {
            helpResultText.setBackground(getActivity().getResources().getDrawable(R.drawable.help_result_low, null));
        }
        else if (score <= 60) {
            helpResultText.setBackground(getActivity().getResources().getDrawable(R.drawable.help_result_medium, null));
        }
        else {
            helpResultText.setBackground(getActivity().getResources().getDrawable(R.drawable.help_result_high, null));
        }
    }

    //change the links provided in the fragment
    public void changeLinks() {
        if (score <= 30) {
            linkOne.setText( Html.fromHtml("<a href=\"https://headspace.org.au/young-people/life-issues/\">Headspace - For Young People - Life Issues</a>"));
            linkOne.setMovementMethod(LinkMovementMethod.getInstance());
            linkTwo.setVisibility(View.VISIBLE);
            linkTwo.setText( Html.fromHtml("<a href=\"https://headspace.org.au/young-people/mental-health/\">Headspace - For Young People - Mental Health</a>"));
            linkTwo.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else if (score <= 60) {
            linkOne.setText( Html.fromHtml("<a href=\"https://healthyfamilies.beyondblue.org.au/age-6-12/mental-health-conditions-in-children\">Beyond Blue - Mental Health Conditions in Children</a>"));
            linkOne.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            linkOne.setText( Html.fromHtml("<a href=\"https://headspace.org.au/friends-and-family/mental-health/\">Headspace - For Family and Friends - Mental Health</a>"));
            linkOne.setMovementMethod(LinkMovementMethod.getInstance());
            linkTwo.setVisibility(View.VISIBLE);
            linkTwo.setText( Html.fromHtml("<a href=\"https://www.psychology.org.au/Find-a-Psychologist\">APS - Find a Psychologist</a>"));
            linkTwo.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    //set score
    public void setScore(int score) {
        this.score = score;
    }

    //Check whether the string is post code
    public static boolean isPostcode(String str){
        String format = "^[0-9]{4}$";
        Pattern p = Pattern.compile(format);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    //Check the validation of postcode
    private boolean isPostcodeValid() {
        String postcode = postcodeContent.getText().toString().trim();
        //Use TextUtils to check is empty or not
        if (TextUtils.isEmpty(postcode) || !isPostcode(postcode)) {
            Context context = this.getContext();
            CharSequence text = getString(R.string.error_invalid_postcode);
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            //I use requestFocus to get the cursor, because the input is invalid.
            postcodeContent.requestFocus();
            return false;
        } else {
            validPostcode = postcode;
            return true;
        }
    }
}