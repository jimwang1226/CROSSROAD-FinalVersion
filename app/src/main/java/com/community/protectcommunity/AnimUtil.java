package com.community.protectcommunity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class AnimUtil {
    public static void setShowAnimation(final View view, int duration, Animation mShowAnimation)
    {
        if (null == view || duration < 0)
        {
            return;
        }
        if (null != mShowAnimation)
        {
            mShowAnimation.cancel();
        }
        mShowAnimation = new AlphaAnimation(0.0f, 1.0f);
        mShowAnimation.setDuration(duration);
        mShowAnimation.setFillAfter(true);
        mShowAnimation.setAnimationListener(new Animation.AnimationListener()
        {

            @Override
            public void onAnimationStart(Animation arg0)
            {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation arg0)
            {

            }

            @Override
            public void onAnimationEnd(Animation arg0)
            {

            }
        });
        view.startAnimation(mShowAnimation);
    }

    public static ObjectAnimator getAnimatorOn (final View view, final Activity activity) {
        if(activity == null) {
            return null;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        objectAnimator.setDuration(500);
        return objectAnimator;
    }

    public static ObjectAnimator getAnimatorOff(final View view, final Activity activity) {
        if (activity == null) {
            return null;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        objectAnimator.setDuration(500);
        return objectAnimator;
    }

    public static ValueAnimator getNullAnimator () {
        ValueAnimator nullAnimator = ValueAnimator.ofFloat (1.0F , 0F);
        nullAnimator.setDuration(2000);
        return nullAnimator;
    }

    public static ObjectAnimator getPlayAgainAnimatorOff(final View view, final Activity activity) {
        if (activity == null) {
            return null;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.GONE);
                        view.setEnabled(false);
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        objectAnimator.setDuration(500);
        return objectAnimator;
    }

    public static ObjectAnimator getPlayAgainAnimatorOn(final View view, final Activity activity) {
        if (activity == null) {
            return null;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.VISIBLE);
                        view.setEnabled(true);
                    }
                });
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        objectAnimator.setDuration(500);
        return objectAnimator;
    }
}
