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
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import hs_mannheim.gestureframework.animation.DragAndDropper;
import hs_mannheim.gestureframework.animation.GestureTransitionInfo;
import hs_mannheim.gestureframework.animation.TransitionAnimator;
import hs_mannheim.sysplace.R;

public class ElevateAndLeaveAnimator extends TransitionAnimator {

    private DragAndDropper mDragAndDropper;
    private Animator mElevateAnimator, mLowerAnimator, mFlyOutNorthAnimator, mFlyInSouthAnimator;
    private Drawable
            mPolaroid;
    private ImageView mImageView;
    private float mOriginalYValue, mOriginalTopMargin;

    public ElevateAndLeaveAnimator(Context context, View view) {
        super(context, view);
        mDragAndDropper = new DragAndDropper(false, true, view);
        mPolaroid = mContext.getResources().getDrawable(R.drawable.polaroid);

        //TODO: HACKYDIHACKHACK
        mImageView = (ImageView) mView;

        mOriginalYValue = mImageView.getY();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
        mOriginalTopMargin = params.topMargin;
    }

    @Override
    public void handleGestureStart(GestureTransitionInfo info) {
        if (mIsAnimationRunning) {
            mAnimatorQueue.add(mElevateAnimator);
        } else {
            mElevateAnimator.start();
            if (info.isTouchGesture()) {





                mDragAndDropper.setDeltaPoint(info.getTouchPoint());
            }
        }
    }

    @Override
    public void handleGestureDuring(GestureTransitionInfo info) {
        if (info.isTouchGesture()) {
            mDragAndDropper.dragDrop(info.getTouchPoint());
        }
    }

    @Override
    public void handleGestureEnd(GestureTransitionInfo info) {
        if (mIsAnimationRunning) {
            mAnimatorQueue.add(mLowerAnimator);
        } else {
            mLowerAnimator.start();
        }

        if (info.isTouchGesture()) {





            mDragAndDropper.returnToStart(400);
        }
    }

    @Override
    public void play() {
        if (mIsAnimationRunning) {
            mAnimatorQueue.add(mFlyOutNorthAnimator);
        } else {
            mFlyOutNorthAnimator.start();
        }
    }

    @Override
    protected void registerAnimators() {
        mElevateAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.elevate);
        mElevateAnimator.setDuration(400);
        mElevateAnimator.addListener(this);
        mElevateAnimator.setTarget(mView);

        mFlyOutNorthAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.fly_out_north);
        mFlyOutNorthAnimator.addListener(this);
        mFlyOutNorthAnimator.setTarget(mView);

        mLowerAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.lower);
        mLowerAnimator.setDuration(400);
        mLowerAnimator.addListener(this);
        mLowerAnimator.setTarget(mView);

        mFlyInSouthAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.fly_in_south);
        mFlyInSouthAnimator.addListener(this);
        mFlyInSouthAnimator.setTarget(mView);
    }

    @Override
    public void onAnimationStart(Animator animation) {
        mIsAnimationRunning = true;
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        mIsAnimationRunning = false;

        if (animator == mFlyOutNorthAnimator) {
            mImageView.setImageDrawable(mPolaroid);
            mFlyInSouthAnimator.start();
            return;
        }

        if (animator == mFlyInSouthAnimator) {
            mLowerAnimator.start();
            //mDragAndDropper.returnToStart(500);
            mDragAndDropper.setOriginalMargins();
            return;
        }

        if (mAnimatorQueue.isEmpty()) {
            return;
        } else {
            mAnimatorQueue.get(0).start();
            mAnimatorQueue.remove(0);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        mIsAnimationRunning = false;
        if (mAnimatorQueue.isEmpty()) {
            return;
        } else {
            mAnimatorQueue.get(0).start();
            mAnimatorQueue.remove(0);
        }
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
