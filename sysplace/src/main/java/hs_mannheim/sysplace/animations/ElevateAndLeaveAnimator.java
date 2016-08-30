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
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import hs_mannheim.gestureframework.animation.DragAndDropper;
import hs_mannheim.gestureframework.animation.GestureTransitionInfo;
import hs_mannheim.gestureframework.animation.TransitionAnimator;
import hs_mannheim.sysplace.R;

public class ElevateAndLeaveAnimator extends TransitionAnimator {


    private DragAndDropper mDragAndDropper;
    private Animator mElevateAnimator, mLowerAnimator, mFlyOutNorthAnimator, mGetNextPicAnimator, mTeleportBackAnimator, mAnticipateLeaveAnimator;
    private ImageView mImageView, mImageViewCopy;

    public ElevateAndLeaveAnimator(Context context, View view) {
        super(context, view);
        mDragAndDropper = new DragAndDropper(false, true, view);

        //TODO: HACKYDIHACKHACK
        mImageView = (ImageView) mView;
        Activity activity = (Activity) mContext;
        mImageViewCopy = (ImageView) activity.findViewById(R.id.imgViewCopy);
    }

    @Override
    public void handleGestureStart(GestureTransitionInfo info) {
        mImageViewCopy.setVisibility(View.VISIBLE);
        mImageViewCopy.setColorFilter(Color.argb(150, 200, 200, 200));

        if (mIsAnimationRunning && !mAnimatorQueue.contains(mElevateAnimator)) {
            //mAnimatorQueue.add(mElevateAnimator);
        } else {
            //mElevateAnimator.start();
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
        /*
        if (mIsAnimationRunning && !mAnimatorQueue.contains(mLowerAnimator)) {
            mAnimatorQueue.add(mLowerAnimator);
        } else {
            mLowerAnimator.start();
        }
        */
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

        mGetNextPicAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.get_next_pic);
        mGetNextPicAnimator.addListener(this);
        mGetNextPicAnimator.setTarget(mView);

        mTeleportBackAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.teleport_back);
        mTeleportBackAnimator.addListener(this);
        mTeleportBackAnimator.setTarget(mView);

        mAnticipateLeaveAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.anticipate_leave_north);
        mAnticipateLeaveAnimator.addListener(this);
        mAnticipateLeaveAnimator.setTarget(mView);
    }

    @Override
    public void onAnimationStart(Animator animator) {
        mIsAnimationRunning = true;

        if (animator == mFlyOutNorthAnimator) {
            resetColorFilter();
        }
    }

    private void resetColorFilter() {
        ValueAnimator grayAwayAnimator = ValueAnimator.ofInt(150, 0);
        grayAwayAnimator.setDuration(2000);
        grayAwayAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mImageViewCopy.setColorFilter(Color.argb((int) valueAnimator.getAnimatedValue(), 200, 200, 200));
            }
        });
        grayAwayAnimator.start();
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        mIsAnimationRunning = false;

        if (animator == mFlyOutNorthAnimator) {
            mDragAndDropper.setOriginalMargins();
            mTeleportBackAnimator.start();
            return;
        }
        if (animator == mTeleportBackAnimator) {
            mImageViewCopy.setImageDrawable(mContext.getResources().getDrawable(R.drawable.polaroid_pick));
            mGetNextPicAnimator.start();
            return;
        }

        if (animator == mGetNextPicAnimator) {
            mImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.polaroid_pick_another_question));


            mImageView.setElevation(5);
            //mImageViewCopy.setImageDrawable(mContext.getResources().getDrawable(R.drawable.polaroid));

/*
            Animation wobbleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.wobble);
            wobbleAnimation.reset();
            wobbleAnimation.setFillAfter(true);
*/
            AnimationsContainer.FramesSequenceAnimation animation = AnimationsContainer.getInstance().createSplashAnim(mImageView);
            animation.start();

            //mImageView.startAnimation(wobbleAnimation);


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
