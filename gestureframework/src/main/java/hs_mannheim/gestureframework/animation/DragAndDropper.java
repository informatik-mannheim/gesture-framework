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
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;

public class DragAndDropper {

    private boolean mShouldDragX, mShouldDragY;
    private View mView;
    private TouchPoint mDeltaPoint;
    private int mOriginalTopMargin, mOriginalLeftMargin;
    private RelativeLayout.LayoutParams mLayoutParams;
    private static final int DEFAULT_DURATION = 500;
    private static final String TAG = "[DragAndDropper]";

    /**
     * Enables Drag and Drop functionality on given View.
     * @param shouldDragX Specifies if horizontal Drag and Drop should occur
     * @param shouldDragY Specifies if vertical Drag and Drop should occur
     * @param view The View that is to be Drag and Dropped
     */
    public DragAndDropper(boolean shouldDragX, boolean shouldDragY, View view){
        mShouldDragX = shouldDragX;
        mShouldDragY = shouldDragY;
        mView = view;


        mLayoutParams = (RelativeLayout.LayoutParams) view
                .getLayoutParams();
        mOriginalTopMargin = mLayoutParams.topMargin;
        mOriginalLeftMargin = mLayoutParams.leftMargin;


    }

    public void setDeltaPoint(TouchPoint touchPoint){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mView.getLayoutParams();
        mDeltaPoint = new TouchPoint((int)touchPoint.getX() - layoutParams.leftMargin,
                (int) touchPoint.getY() - layoutParams.topMargin, touchPoint.getTime());
    }

    public void dragDrop(TouchPoint touchPoint){

        //TODO: still needs work
        if (mShouldDragX){
            mView.setX(touchPoint.getX());
            //mLayoutParams.leftMargin = (int) (touchPoint.getX() - mDeltaPoint.getX());
        }

        if (mShouldDragY){
            //Restricts movement at top of screen
            //if (mLayoutParams.topMargin > 0) {
            //    mLayoutParams.topMargin = (int) (touchPoint.getY() - mDeltaPoint.getY());
            //} else if ((int)(touchPoint.getY() - mDeltaPoint.getY()) > 0){
                mLayoutParams.topMargin = (int) (touchPoint.getY() - mDeltaPoint.getY());
            //}
        }
        mView.setLayoutParams(mLayoutParams);
        mView.requestLayout();
    }

    /**
     * Animates the return of the moved View back to its origin
     * @param duration Duration of the animation in ms
     */
    public void returnToStart(long duration){
        ValueAnimator animator = ValueAnimator.ofInt(mLayoutParams.topMargin, mOriginalTopMargin);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                mLayoutParams.topMargin = (Integer) valueAnimator.getAnimatedValue();
                mView.requestLayout();
            }
        });

        if(duration >= 0) {
            animator.setDuration(duration);
        } else {
            animator.setDuration(DEFAULT_DURATION);
        }

        //TODO: Make sure this is not needed somewhere and throw it out
        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                //Log.d(TAG, "return to start done");
            }
        });

        animator.start();
    }

    public void setOriginalMargins(){
        mLayoutParams.topMargin = mOriginalTopMargin;
        mLayoutParams.leftMargin = mOriginalLeftMargin;
        mView.requestLayout();
    }
}