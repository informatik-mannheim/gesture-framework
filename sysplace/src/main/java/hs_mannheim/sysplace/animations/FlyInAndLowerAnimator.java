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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import hs_mannheim.gestureframework.animation.GestureAnimator;
import hs_mannheim.gestureframework.animation.ImageViewUpdater;
import hs_mannheim.sysplace.R;

public class FlyInAndLowerAnimator extends GestureAnimator {

    private Animator mElevateAnimator, mFlyOutAnimator, mFlyInAnimator, mLowerAnimator;
    private ImageViewUpdater mImageViewUpdater;

    public FlyInAndLowerAnimator(Context context, View view) {
        super(context, view);

        Bitmap polaroidFrame = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.polaroid_frame);
        mImageViewUpdater = new ImageViewUpdater(mContext, (ImageView) view, polaroidFrame);
    }

    @Override
    public void play() {
        mElevateAnimator.start();
    }

    @Override
    protected void registerAnimators() {
        mElevateAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.elevate);
        mElevateAnimator.addListener(this);
        mElevateAnimator.setTarget(mView);

        mFlyOutAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.fly_out_south);
        mFlyOutAnimator.addListener(this);
        mFlyOutAnimator.setTarget(mView);

        mFlyInAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.fly_in_north);
        mFlyInAnimator.addListener(this);
        mFlyInAnimator.setTarget(mView);

        mLowerAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.lower);
        mLowerAnimator.addListener(this);
        mLowerAnimator.setTarget(mView);
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {

        if (animator == mElevateAnimator) {
            mFlyOutAnimator.start();
        }

        if (animator == mFlyOutAnimator) {
            mImageViewUpdater.updateImageView(mReplacementBitmap);
            mFlyInAnimator.start();
        }

        if (animator == mFlyInAnimator) {
            mLowerAnimator.start();
            registerAnimators();
        }
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
