package org.gittner.osmbugs.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.gc.materialdesign.views.ButtonFloat;

public class RotatingIconButtonFloat extends ButtonFloat
{
    private final Animation mAnimation;

    private boolean mStarted = false;
    private boolean mStartRequest = false;
    private boolean mStopRequest = false;

    private final Animation.AnimationListener mAnimationListener = new Animation.AnimationListener()
    {
        final Interpolator mStartInterpolator = new AccelerateInterpolator();
        final Interpolator mRepeatInterpolator = new LinearInterpolator();
        final Interpolator mEndInterpolator = new DecelerateInterpolator();


        @Override
        public void onAnimationStart(final Animation animation)
        {
            mStartRequest = false;
            animation.setInterpolator(mStartInterpolator);
            animation.setRepeatCount(Animation.INFINITE);
        }


        @Override
        public void onAnimationEnd(final Animation animation)
        {
            mStarted = false;
            mAnimation.cancel();
            if (mStartRequest)
            {
                restart();
            }
            else
            {
                mStarted = false;
            }
            mStopRequest = false;
        }


        @Override
        public void onAnimationRepeat(final Animation animation)
        {
            animation.setInterpolator(mRepeatInterpolator);
            animation.setDuration(500);
            if (mStopRequest)
            {
                animation.setInterpolator(mEndInterpolator);
                animation.setRepeatCount(0);
                animation.setDuration(750);
            }
        }
    };


    public RotatingIconButtonFloat(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        mAnimation = new RotateAnimation(
                0f,
                360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
    }


    public void setRotate(final boolean rotate)
    {
        mStartRequest = rotate;
        mStopRequest = !rotate;
        if (mStartRequest && !mStarted)
        {
            restart();
        }
    }


    private void restart()
    {
        getIcon().setAnimation(mAnimation);
        mAnimation.setAnimationListener(mAnimationListener);
        mAnimation.setDuration(750);
        mAnimation.start();
        mStarted = true;
    }
}
