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

        //RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

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
