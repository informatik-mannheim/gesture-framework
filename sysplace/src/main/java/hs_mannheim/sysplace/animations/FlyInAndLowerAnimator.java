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
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import hs_mannheim.gestureframework.animation.GestureAnimator;
import hs_mannheim.gestureframework.animation.ImageViewUpdater;
import hs_mannheim.sysplace.R;

public class FlyInAndLowerAnimator extends GestureAnimator {

    private Animator mElevateAnimator, mFlyOutAnimator, mFlyInAnimator, mLowerAnimator, mTeleportOutAnimator;
    private ImageViewUpdater mImageViewUpdater;
    private static final String TAG = "[FlyInAndLowerAnimator]";
    private ImageView mImageViewCopy;
    private Bitmap mPolaroidFrame;

    public FlyInAndLowerAnimator(Context context, View view) {
        super(context, view);

        mPolaroidFrame = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.polaroid_frame_receive);
        mImageViewUpdater = new ImageViewUpdater(mContext);

        //TODO: HACKY!
        Activity activity = (Activity) mContext;
        mImageViewCopy = (ImageView) activity.findViewById(R.id.imgViewCopy);
    }

    @Override
    public void play() {
        ImageView imageView = (ImageView) mView;
        Bitmap currentBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        mImageViewCopy.setVisibility(View.VISIBLE);
        mImageViewCopy.setImageBitmap(currentBitmap.copy(currentBitmap.getConfig(), true));
        mTeleportOutAnimator.start();
    }

    @Override
    protected void registerAnimators() {
        mElevateAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.elevate);
        mElevateAnimator.addListener(this);
        mElevateAnimator.setTarget(mView);
        mElevateAnimator.setDuration(1);

        mFlyOutAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.fly_out_south);
        mFlyOutAnimator.addListener(this);
        mFlyOutAnimator.setTarget(mView);

        mFlyInAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.fly_in_north);
        mFlyInAnimator.addListener(this);
        mFlyInAnimator.setTarget(mView);

        mLowerAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.lower);
        mLowerAnimator.addListener(this);
        mLowerAnimator.setTarget(mView);

        mTeleportOutAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.teleport_north);
        mTeleportOutAnimator.addListener(this);
        mTeleportOutAnimator.setTarget(mView);
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {

        if (animator == mTeleportOutAnimator) {
            mImageViewUpdater.updateImageView((ImageView) mView, mReplacementBitmap, mPolaroidFrame);
            mElevateAnimator.start();
        }

        if (animator == mElevateAnimator) {
            mFlyInAnimator.start();
        }

        if (animator == mFlyOutAnimator) {
            mImageViewUpdater.updateImageView((ImageView) mView, mReplacementBitmap, mPolaroidFrame);
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