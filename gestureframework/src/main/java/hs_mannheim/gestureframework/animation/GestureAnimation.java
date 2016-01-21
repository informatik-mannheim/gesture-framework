package hs_mannheim.gestureframework.animation;

import android.view.View;

import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;

/**
 * Created by uselab on 22.12.2015.
 */
public abstract class GestureAnimation {
    protected View view;
    protected AnimationType type;
    protected TouchPoint startPoint, currentPoint;

    public abstract void play();

    public abstract void handleSwiping(TouchPoint touchPoint);

    public void onSwiping(TouchPoint touchPoint){
        if(this.type.equals(AnimationType.SEND)){
            handleSwiping(touchPoint);
        }
    }

    public View getView(){
        return view;
    }

    public AnimationType getType(){
        return type;
    }

    public void onSwipeStart(TouchPoint touchPoint) {
        this.startPoint = touchPoint;
        startSwipe();
    }

    public abstract void startSwipe();

    public abstract void onSwipeEnd(TouchPoint touchPoint);
}
