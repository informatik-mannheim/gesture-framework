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
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import hs_mannheim.gestureframework.R;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;

/**
 * Simple SEND Animation
 */
public class PostCardFlipAnimationSend extends GestureAnimation{

    Activity context;
    Animator swipeStartAnimator, swipeEndAnimator, flipLeftInAnimator, flipRightInAnimator;
    DragAndDropper dragAndDropper;
    Bitmap postcard, origImage;
    boolean shouldCopyImageBeforeSending;
    float originalY, originalTopMargin;

    public PostCardFlipAnimationSend(Activity context, final ImageView view) {
        this.type = AnimationType.SEND;
        this.view = view;
        this.originalY = view.getY();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        this.originalTopMargin = layoutParams.topMargin;
        this.context = context;

        postcard = BitmapFactory.decodeResource(context.getResources(), R.drawable.postcard);
        origImage = ((BitmapDrawable)view.getDrawable()).getBitmap();

        //TODO: set shouldCopyImageBeforeSending in params
        shouldCopyImageBeforeSending = true;

        registerAnimators();
        registerDragAndDropper(false, true);
    }

    @Override
    public void play() {
        if (!animationRunning){
            playAnimator.start();
            animatorQueue.add(playAnimator);
        } else {
            animatorQueue.add(playAnimator);
        }
    }

    @Override
    public void play(Bitmap image) {

    }

    @Override
    protected void handleSwipeStart(TouchPoint touchPoint) {
        if(!animationRunning){
            swipeStartAnimator.start();
        }else {
            animatorQueue.add(swipeStartAnimator);
        }

        dragAndDropper.setDeltaPoint(touchPoint);
    }

    @Override
    protected void handleSwipeEnd(TouchPoint touchPoint) {
        dragAndDropper.returnToStart();
        if(!animationRunning){
            animatorQueue.clear();
            swipeEndAnimator.start();
        } else {
            animatorQueue.add(swipeEndAnimator);
        }
    }

    @Override
    protected void handleSwiping(TouchPoint touchPoint) {
        dragAndDropper.dragDrop(touchPoint);
    }

    private void registerDragAndDropper(boolean shouldDragX, boolean shouldDragY){
        this.dragAndDropper = new DragAndDropper(shouldDragX, shouldDragY, view);
    }

    @Override
    protected void registerAnimators() {
        swipeStartAnimator =  AnimatorInflater.loadAnimator(context, R.animator.postcardsend_flip_left_out);
        swipeStartAnimator.addListener(this);
        swipeStartAnimator.setTarget(view);

        swipeEndAnimator =  AnimatorInflater.loadAnimator(context, R.animator.postcardsend_flip_right_out);
        swipeEndAnimator.addListener(this);
        swipeEndAnimator.setTarget(view);

        playAnimator =  AnimatorInflater.loadAnimator(context, R.animator.elevate_anticipate_leave);
        playAnimator.addListener(this);
        playAnimator.setTarget(view);

        flipLeftInAnimator = AnimatorInflater.loadAnimator(context, R.animator.postcardsend_flip_left_in);
        flipLeftInAnimator.addListener(this);
        flipLeftInAnimator.setTarget(view);

        flipRightInAnimator = AnimatorInflater.loadAnimator(context, R.animator.postcardsend_flip_right_in);
        flipRightInAnimator.addListener(this);
        flipRightInAnimator.setTarget(view);
    }

    @Override
    public void onAnimationStart(Animator animation) {
        animationRunning = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        animationRunning = false;

        if (animation.equals(swipeStartAnimator)){
            view.setImageBitmap(postcard);
            flipLeftInAnimator.start();
        }

        if (animation.equals(swipeEndAnimator)){
            view.setImageBitmap(origImage);
            flipRightInAnimator.start();
        }
/*
        //send animation finished. return to idle state
        if (animation.equals(playAnimator)){
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            Log.d("Ys", "orig: " + originalY + ", current: " + view.getY());
            Log.d("TopMargins", "orig: " + originalTopMargin + ", current: " + layoutParams.topMargin);
            view.setElevation(2);
            view.setImageBitmap(origImage);
            layoutParams.topMargin = (int)originalTopMargin;
            view.setY(originalY);
        }
*/

        if (!animatorQueue.isEmpty()) {
            animatorQueue.get(0).start();
            animatorQueue.remove(animatorQueue.get(0));
        }
    }
}