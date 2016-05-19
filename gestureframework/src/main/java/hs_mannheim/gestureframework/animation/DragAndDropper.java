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

import android.animation.ValueAnimator;
import android.view.View;
import android.widget.RelativeLayout;

import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;


public class DragAndDropper {

    boolean shouldDragX, shouldDragY;
    View view;
    TouchPoint deltaPoint;
    int originalTopMargin, originalLeftMargin;
    RelativeLayout.LayoutParams layoutParams;

    public DragAndDropper(boolean shouldDragX, boolean shouldDragY, View view){
        this.shouldDragX = shouldDragX;
        this.shouldDragY = shouldDragY;
        this.view = view;

        layoutParams = (RelativeLayout.LayoutParams) view
                .getLayoutParams();
        originalTopMargin = layoutParams.topMargin;
        originalLeftMargin = layoutParams.leftMargin;

    }

    public void setDeltaPoint(TouchPoint touchPoint){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        deltaPoint = new TouchPoint((int)touchPoint.getX() - layoutParams.leftMargin,
                (int) touchPoint.getY() - layoutParams.topMargin, touchPoint.getTime());
    }

    public void dragDrop(TouchPoint touchPoint){

        if (shouldDragX){
            layoutParams.leftMargin = (int) (touchPoint.getX() - deltaPoint.getX());
        }

        if (shouldDragY){
            //Restricts movement at top of screen
            if (layoutParams.topMargin > 0) {
                layoutParams.topMargin = (int) (touchPoint.getY() - deltaPoint.getY());
            } else if ((int)(touchPoint.getY() - deltaPoint.getY()) > 0){
                layoutParams.topMargin = (int) (touchPoint.getY() - deltaPoint.getY());
            }
        }
        view.setLayoutParams(layoutParams);
    }

    public void returnToStart(){
        ValueAnimator animator = ValueAnimator.ofInt(layoutParams.topMargin, originalTopMargin);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                layoutParams.topMargin = (Integer) valueAnimator.getAnimatedValue();
                view.requestLayout();
            }
        });
        animator.setDuration(500);
        animator.start();
    }
}
