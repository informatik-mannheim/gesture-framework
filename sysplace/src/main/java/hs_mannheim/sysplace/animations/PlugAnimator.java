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
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import hs_mannheim.gestureframework.animation.GestureAnimator;


public class PlugAnimator extends GestureAnimator {

    private Point mScreenDims;
    private ObjectAnimator mPeekInAnimator, mPlugInAnimatorMove, mPlugInAnimatorStretch, mRetreatAnimator;
    private AnimatorSet mPlugInSet;
    private static final String TAG = "[PlugAnimator]";

    public PlugAnimator(Context context, View view, Point dims) {
        super(context, view);
        mScreenDims = dims;
        Log.d(TAG, "dims: " + mScreenDims.x + ", " + mScreenDims.y);
        mView.setTranslationX(-mScreenDims.x);

        mPeekInAnimator = ObjectAnimator.ofFloat(mView, "translationX", -mScreenDims.x, -mScreenDims.x * .6f);
        mPeekInAnimator.setDuration(400);
        mPeekInAnimator.addListener(this);
        mPeekInAnimator.setInterpolator(new OvershootInterpolator());

        mPlugInAnimatorMove = ObjectAnimator.ofFloat(mView, "translationX", -mScreenDims.x * .6f, .5f * mScreenDims.x);
        mPlugInAnimatorMove.setDuration(1000);
        mPlugInAnimatorMove.addListener(this);
        mPlugInAnimatorMove.setStartDelay(100);
        mPlugInAnimatorMove.setInterpolator(new AccelerateInterpolator(3f));

        mPlugInAnimatorStretch = ObjectAnimator.ofFloat(mView, "scaleX", 1f, 1.3f);
        mPlugInAnimatorStretch.setDuration(300);
        mPlugInAnimatorStretch.addListener(this);
        mPlugInAnimatorStretch.setInterpolator(new DecelerateInterpolator());

        mRetreatAnimator = ObjectAnimator.ofFloat(mView, "translationX", -mScreenDims.x * .7f, -mScreenDims.x);
        mRetreatAnimator.setDuration(400);
        mRetreatAnimator.addListener(this);
        mRetreatAnimator.setInterpolator(new OvershootInterpolator());

        mPlugInSet = new AnimatorSet();
        mPlugInSet.play(mPlugInAnimatorMove).with(mPlugInAnimatorStretch);

    }

    @Override
    public void play() {
        mPeekInAnimator.start();
    }

    public void plugIn() {
        //mPlugInSet.start();
        //mPlugInAnimatorStretch.start();
        mPlugInAnimatorMove.start();
    }

    public void retreat() {
        mRetreatAnimator.start();
    }

    @Override
    protected void registerAnimators() {

    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

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
}
