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
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;

import hs_mannheim.gestureframework.animation.GestureAnimator;
import hs_mannheim.sysplace.R;

public class MartiniAnimator extends GestureAnimator {

    private Animator mRevealAnimator, mClinkAnimator, mTransitionAnimator;
    private String mBumpDirection;
    private static float ANGLE = 16f;
    private View mRevealView;
    private static final String TAG = "[MartiniAnimator]";

    public MartiniAnimator(Context context, View view) {
        super(context, view);
        mRevealView = ((Activity) mContext).findViewById(R.id.reveal_frame_con);
    }

    @Override
    public void play() {
        if (!mIsAnimationRunning) {
            mRevealAnimator.start();
            mIsAnimationRunning = true;
        }
    }

    @Override
    protected void registerAnimators() {
        long finalRadius = Math.round(Math.sqrt(((mView.getHeight() * mView.getHeight()) / 4) +
                (mView.getWidth() * mView.getWidth())));
        mRevealAnimator = ViewAnimationUtils.createCircularReveal(mView, mView.getMeasuredWidth() / 2, mView.getMeasuredHeight() / 2, 0, finalRadius);
        mRevealAnimator.setDuration(500);
        mRevealAnimator.setStartDelay(100);
        mRevealAnimator.addListener(this);

        mView.setPivotX(mView.getWidth() / 2);
        mView.setPivotY(mView.getHeight());

        mClinkAnimator = ObjectAnimator.ofFloat(mView, "rotation", 0f, ANGLE);
        mClinkAnimator.setTarget(mView);
        mClinkAnimator.setDuration(500);
        mClinkAnimator.setInterpolator(new AccelerateInterpolator());
        mClinkAnimator.addListener(this);
    }

    @Override
    public void onAnimationStart(Animator animation) {

        if (animation == mRevealAnimator) {
            mView.clearAnimation();
            mView.setVisibility(View.VISIBLE);
        } else if (animation == mTransitionAnimator) {
            mRevealView.clearAnimation();
            ((Activity) mContext).findViewById(R.id.cheers_view).setVisibility(View.VISIBLE);
            mRevealView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {

        if (animation == mRevealAnimator) {
            if (mBumpDirection.equals("WEST")) {
                mClinkAnimator = ObjectAnimator.ofFloat(mView, "rotation", 0f, -ANGLE);
            } else {
                mClinkAnimator = ObjectAnimator.ofFloat(mView, "rotation", 0f, ANGLE);
            }
            mClinkAnimator.start();

            if (mBumpDirection.equals("WEST")) {
                mTransitionAnimator = ViewAnimationUtils.createCircularReveal(mRevealView, 0,
                        Math.round(mRevealView.getMeasuredHeight() * 0.33f), 0, mRevealView.getMeasuredHeight());
            } else {
                mTransitionAnimator = ViewAnimationUtils.createCircularReveal(mRevealView, mRevealView.getMeasuredWidth(),
                        Math.round(mRevealView.getMeasuredHeight() * 0.33f), 0, mRevealView.getMeasuredHeight());
            }

            Log.d(TAG, "ClinkAnimator done");
            mTransitionAnimator.setDuration(1000);
            mTransitionAnimator.setStartDelay(100);
            mTransitionAnimator.addListener(this);
            mTransitionAnimator.start();
            mIsAnimationRunning = false;
        } else if (animation == mTransitionAnimator) {
            //TODO: Put this somewhere else!!
            ((Activity) mContext).finish();
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public void setBumpDirection(String bumpDirection) {
        mBumpDirection = bumpDirection;
    }
}