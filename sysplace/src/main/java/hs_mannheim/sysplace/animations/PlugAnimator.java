/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *     Hochschule Mannheim University of Applied Sciences
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package hs_mannheim.sysplace.animations;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import hs_mannheim.gestureframework.animation.GestureAnimator;
import hs_mannheim.sysplace.R;


public class PlugAnimator extends GestureAnimator {

    private Point mScreenDims;
    private Animator mPeekInAnimator, mPlugInAnimatorMove, mRetreatAnimator, mCircularRevealAnimator;
    private Animator mPinPeekInAnimator, mPinPlugInAnimatorMove, mPinRetreatAnimator;
    private static final String TAG = "[PlugAnimator]";
    private View mCircularRevealView, mMainLayout, mPlugPins;

    public PlugAnimator(Context context, View view, Point dims) {
        super(context, view);
        mScreenDims = dims;
        Log.d(TAG, "dims: " + mScreenDims.x + ", " + mScreenDims.y);

        Activity activity = (Activity) context;
        mPlugPins = activity.findViewById(R.id.plug_pins);
        mView.setTranslationX(-(mScreenDims.x + mPlugPins.getWidth()));
        mPlugPins.setTranslationX(-mPlugPins.getWidth());
        mCircularRevealView = activity.findViewById(R.id.reveal_frame);
        mMainLayout = activity.findViewById(R.id.layout_main);

        mPeekInAnimator = ObjectAnimator.ofFloat(mView, "translationX", -(mScreenDims.x + mPlugPins.getWidth()), -mScreenDims.x * .6f);
        mPeekInAnimator.setDuration(400);
        mPeekInAnimator.addListener(this);
        mPeekInAnimator.setInterpolator(new OvershootInterpolator());

        mPlugInAnimatorMove = ObjectAnimator.ofFloat(mView, "translationX", -mScreenDims.x * .6f, 0);
        mPlugInAnimatorMove.setDuration(700);
        mPlugInAnimatorMove.addListener(this);
        mPlugInAnimatorMove.setStartDelay(100);
        mPlugInAnimatorMove.setInterpolator(new AccelerateInterpolator(3f));

        mRetreatAnimator = ObjectAnimator.ofFloat(mView, "translationX", -mScreenDims.x * .6f, -mScreenDims.x);
        mRetreatAnimator.setDuration(400);
        mRetreatAnimator.addListener(this);
        mRetreatAnimator.setInterpolator(new OvershootInterpolator());

        mPinPeekInAnimator = ObjectAnimator.ofFloat(mPlugPins, "translationX", -mPlugPins.getWidth(), (-mScreenDims.x * .6f + mScreenDims.x));
        mPinPeekInAnimator.setDuration(400);
        mPinPeekInAnimator.addListener(this);
        mPinPeekInAnimator.setInterpolator(new OvershootInterpolator());

        mPinPlugInAnimatorMove = ObjectAnimator.ofFloat(mPlugPins, "translationX", (-mScreenDims.x * .6f + mScreenDims.x), mScreenDims.x);
        mPinPlugInAnimatorMove.setDuration(700);
        mPinPlugInAnimatorMove.addListener(this);
        mPinPlugInAnimatorMove.setStartDelay(100);
        mPinPlugInAnimatorMove.setInterpolator(new AccelerateInterpolator(3f));

        mPinRetreatAnimator = ObjectAnimator.ofFloat(mPlugPins, "translationX", (-mScreenDims.x * .6f + mScreenDims.x), -mPlugPins.getWidth());
        mPinRetreatAnimator.setDuration(400);
        mPinRetreatAnimator.addListener(this);
        mPinRetreatAnimator.setInterpolator(new OvershootInterpolator());
    }

    @Override
    public void play() {
        mPeekInAnimator.start();
        mPinPeekInAnimator.start();
    }

    public void plugIn() {
        mPlugInAnimatorMove.start();
        mPinPlugInAnimatorMove.start();
    }

    public void retreat() {
        mRetreatAnimator.start();
        mPinRetreatAnimator.start();
    }

    @Override
    protected void registerAnimators() {

    }

    @Override
    public void onAnimationStart(Animator animation) {
        if (animation == mCircularRevealAnimator) {
            mCircularRevealView.clearAnimation();
            mCircularRevealView.setVisibility(View.VISIBLE);
            mCircularRevealView.setBackgroundColor(Color.parseColor("#FFD740"));
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (animation == mPlugInAnimatorMove) {
            Animation shakeAnim = AnimationUtils.loadAnimation(mContext, R.anim.shake);
            mView.startAnimation(shakeAnim);
            mPlugPins.startAnimation(shakeAnim);
            circleAnimation();
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public void setScreenDimensions(Point dims) {
        mScreenDims = dims;
    }

    public void circleAnimation(){
        long finalRadius = Math.round(Math.sqrt(((mScreenDims.y * mScreenDims.y)/4) + (mScreenDims.x * mScreenDims.x)));
        mCircularRevealAnimator= ViewAnimationUtils.createCircularReveal(mCircularRevealView, mScreenDims.x, Math.round((mScreenDims.y/2) - (mScreenDims.y * .1f)), 0, finalRadius);
        mCircularRevealAnimator.setDuration(500);
        mCircularRevealAnimator.setStartDelay(100);
        mCircularRevealAnimator.addListener(this);
        mCircularRevealAnimator.start();
    }
}
