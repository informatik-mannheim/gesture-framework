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

public class SocketAnimator extends GestureAnimator {

    private Animator mPeekInAnimator, mPlugInAnimator, mRetreatAnimator, mPlugPeekAnimator, mCircularRevealAnimator;
    private static final String TAG = "[SocketAnimator]";
    private Point mScreenDims;
    private View mCircularRevealView, mMainLayout;

    public SocketAnimator(Context context, View view, Point dims) {
        super(context, view);
        mScreenDims = dims;
        mView.setTranslationX(mScreenDims.x);

        mPeekInAnimator = ObjectAnimator.ofFloat(mView, "translationX", mScreenDims.x, mScreenDims.x * .7f);
        mPeekInAnimator.setDuration(400);
        mPeekInAnimator.addListener(this);
        mPeekInAnimator.setInterpolator(new OvershootInterpolator());

        mRetreatAnimator = ObjectAnimator.ofFloat(mView, "translationX", mScreenDims.x * .7f, mScreenDims.x);
        mRetreatAnimator.setDuration(400);
        mRetreatAnimator.addListener(this);
        mRetreatAnimator.setInterpolator(new OvershootInterpolator());

        mPlugInAnimator = ObjectAnimator.ofFloat(mView, "translationX", mScreenDims.x * .7f, 0);
        mPlugInAnimator.setDuration(700);
        mPlugInAnimator.addListener(this);
        mPlugInAnimator.setInterpolator(new AccelerateInterpolator(3f));
        mPlugInAnimator.addListener(this);

        //TODO: HACKY...
        Activity activity = (Activity) context;
        View plugPins = activity.findViewById(R.id.plug_pins);
        mCircularRevealView = activity.findViewById(R.id.reveal_frame);
        mMainLayout = activity.findViewById(R.id.layout_main);

        mPlugPeekAnimator = ObjectAnimator.ofFloat(plugPins, "translationX", -mScreenDims.x, 0);
        mPlugPeekAnimator.setDuration(125);
        mPlugPeekAnimator.setStartDelay(650);
        mPlugPeekAnimator.addListener(this);
        mPlugPeekAnimator.setInterpolator(new AccelerateInterpolator(3f));
    }

    @Override
    public void play() {
        mPeekInAnimator.start();
    }

    public void plugIn() {
        mPlugInAnimator.start();
        mPlugPeekAnimator.start();
    }

    public void retreat() {
        mRetreatAnimator.start();
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
        if (animation == mPlugInAnimator) {
            Animation shakeAnim = AnimationUtils.loadAnimation(mContext, R.anim.shake);
            mView.startAnimation(shakeAnim);
            circleAnimation();
        }
        if (animation == mCircularRevealAnimator) {
            mCircularRevealView.setVisibility(View.VISIBLE);
            View revealView = ((Activity)mContext).findViewById(R.id.reveal_view);
            revealView.setVisibility(View.VISIBLE);
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

    private void circleAnimation(){
        long finalRadius = Math.round(Math.sqrt(((mScreenDims.y * mScreenDims.y)/4) + (mScreenDims.x * mScreenDims.x)));
        mCircularRevealAnimator= ViewAnimationUtils.createCircularReveal(mCircularRevealView, 0, Math.round((mScreenDims.y/2) - (mScreenDims.y * .1f)), 0, finalRadius);
        mCircularRevealAnimator.setDuration(1000);
        mCircularRevealAnimator.setStartDelay(100);
        mCircularRevealAnimator.addListener(this);
        mCircularRevealAnimator.start();
    }
}
