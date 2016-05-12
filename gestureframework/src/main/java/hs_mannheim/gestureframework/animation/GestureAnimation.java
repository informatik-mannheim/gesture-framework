/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *    Hochschule Mannheim University of Applied Sciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package hs_mannheim.gestureframework.animation;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.ArrayList;

import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;


public abstract class GestureAnimation implements Animator.AnimatorListener{

    //TODO: make separate animatorlistener
    //TODO: split send- and receiveanimations into separate abstract classes and extend from there
    protected ImageView view;
    protected AnimationType type;
    protected TouchPoint startPoint, currentPoint;
    protected boolean animationRunning;
    protected Animator playAnimator;
    protected ArrayList<Animator> animatorQueue = new ArrayList<>();

    public abstract void play();
    public abstract void play(Bitmap image);

    public void onSwiping(TouchPoint touchPoint){
        if(this.type.equals(AnimationType.SEND)){
            handleSwiping(touchPoint);
        }
    }

    public ImageView getView(){
        return view;
    }

    public AnimationType getType(){
        return type;
    }

    public void onSwipeStart(TouchPoint touchPoint) {
        handleSwipeStart(touchPoint);
    }

    public void onSwipeEnd(TouchPoint touchPoint) {
        handleSwipeEnd(touchPoint);
    }

    protected abstract void handleSwipeStart(TouchPoint touchPoint);

    protected abstract void handleSwipeEnd(TouchPoint touchPoint);

    protected abstract void handleSwiping(TouchPoint touchPoint);

    protected abstract void registerAnimators();
}
