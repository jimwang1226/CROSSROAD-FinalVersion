package com.community.protectcommunity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class GameProgressUtil {
    public static void saveCheckPoint(Activity activity, String fragmentName) {
        int lastIndexOfDot = fragmentName.lastIndexOf(".");
        fragmentName = fragmentName.substring(lastIndexOfDot + 1);
        System.out.println("substring!!!!!!!!!!!" + fragmentName);
        SharedPreferences sharedPref = activity.getSharedPreferences("username_gender_choice", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putString("gameProgress", fragmentName);
        spEditor.apply();
    }

    //when user finish the game, the game progress will be set to null at GameEndingActivity
    public static void setGameProgressToNull(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences("username_gender_choice", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putString("gameProgress", null);
        spEditor.apply();
    }

    public static String getGameProgress(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences("username_gender_choice",Context.MODE_PRIVATE);
        String gameProgress = sharedPref.getString("gameProgress", null);
        return gameProgress;
    }

    public static void changeToGameActivity(Activity activity) {
        Intent intent = new Intent(activity, GameActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void changeToFragment(Activity activity) {
        String fragmentName = getGameProgress(activity);
        //Switch fragment to introduction fragment
        Fragment nextFragment = null;
        switch (fragmentName){
            case "IntroductionFragment":
                nextFragment = new IntroductionFragment();
                break;
            case "DrivingFragment":
                nextFragment = new DrivingFragment();
                break;
            case "FrontGateFragment":
                nextFragment = new FrontGateFragment();
                break;
            case "ClassroomFragment":
                nextFragment = new ClassroomFragment();
                break;
            case "ClassroomChoiceOneFragment":
                nextFragment = new ClassroomChoiceOneFragment();
                break;
            case "ClassroomChoiceTwoFragment":
                nextFragment = new ClassroomChoiceTwoFragment();
                break;
            case "CanteenFragment":
                nextFragment = new CanteenFragment();
                break;
            case "CanteenChoiceOneFragment":
                nextFragment = new CanteenChoiceOneFragment();
                break;
            case "CanteenChoiceTwoFragment":
                nextFragment = new CanteenChoiceTwoFragment();
                break;
            case "BirthdayFragment":
                nextFragment = new BirthdayFragment();
                break;
            case "MovieFragment":
                nextFragment = new MovieFragment();
                break;
            default:
                break;
        }
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.game_change_area, nextFragment).commit();
    }
}
