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
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import hs_mannheim.gestureframework.animation.AnimationType;
import hs_mannheim.gestureframework.animation.GestureAnimation;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;

/**
 * Scales a view with a bouncing effect, using the rebound spring framework.
 */
public class ScaleSpring extends GestureAnimation {

    /**
     * Calling the constructor attaches to bounce effect to the view and starts it immediately.
     * @param view The view to attach to
     */
    public ScaleSpring(final ImageView view) {
        super.type = AnimationType.RECEIVE;
        super.view = view;
    }

    @Override
    public void play() {

        SpringSystem springSystem = SpringSystem.create();
        Spring spring = springSystem.createSpring();
        spring.setSpringConfig(new SpringConfig(55, 3));
        spring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring spring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.5f);
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
        });

        spring.setEndValue(1);
    }

    @Override
    public void play(Bitmap image) {

    }

    @Override
    public void handleSwiping(TouchPoint touchPoint) {

    }

    @Override
    protected void registerAnimators() {

    }

    @Override
    public void onSwipeEnd(TouchPoint touchPoint) {

    }

    @Override
    protected void handleSwipeStart(TouchPoint touchPoint) {

    }

    @Override
    protected void handleSwipeEnd(TouchPoint touchPoint) {

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
}
