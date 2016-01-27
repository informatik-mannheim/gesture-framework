package hs_mannheim.gestureframework.animation;

import android.view.View;
import android.widget.RelativeLayout;

import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;


public class DragAndDropper {

    boolean shouldDragX, shouldDragY;
    View view;
    TouchPoint deltaPoint;

    public DragAndDropper(boolean shouldDragX, boolean shouldDragY, View view){
        this.shouldDragX = shouldDragX;
        this.shouldDragY = shouldDragY;
        this.view = view;
    }

    public void setDeltaPoint(TouchPoint touchPoint){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        deltaPoint = new TouchPoint((int)touchPoint.getX() - layoutParams.leftMargin, (int) touchPoint.getY() - layoutParams.topMargin, touchPoint.getTime());
    }

    public void dragDrop(TouchPoint touchPoint){

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                .getLayoutParams();

        if (shouldDragX){
            layoutParams.leftMargin = (int) (touchPoint.getX() - deltaPoint.getX());
        }

        if (shouldDragY){
            if (layoutParams.topMargin > 0) {
                layoutParams.topMargin = (int) (touchPoint.getY() - deltaPoint.getY());
            } else if ((int)(touchPoint.getY() - deltaPoint.getY()) > 0){
                layoutParams.topMargin = (int) (touchPoint.getY() - deltaPoint.getY());
            }
        }

        view.setLayoutParams(layoutParams);
    }
}
