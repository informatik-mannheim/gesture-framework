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
import android.content.Context;
import android.view.View;
import android.view.ViewAnimationUtils;

import hs_mannheim.gestureframework.animation.GestureAnimator;

public class MartiniAnimator extends GestureAnimator {

    private Animator mRevealAnimator;
    public MartiniAnimator (Context context, View view) {
        super(context, view);
    }
    @Override
    public void play() {
        mRevealAnimator.start();
    }

    @Override
    protected void registerAnimators() {
        long finalRadius = Math.round(Math.sqrt(((mView.getHeight() * mView.getHeight()) / 4) + (mView.getWidth() * mView.getWidth())));
        mRevealAnimator = ViewAnimationUtils.createCircularReveal(mView, mView.getMeasuredWidth() / 2, mView.getMeasuredHeight() / 2, 0, finalRadius);
        mRevealAnimator.setDuration(500);
        mRevealAnimator.setStartDelay(100);
        mRevealAnimator.addListener(this);
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if (animation == mRevealAnimator) {
            mView.clearAnimation();
            mView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (animation == mRevealAnimator) {

        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
