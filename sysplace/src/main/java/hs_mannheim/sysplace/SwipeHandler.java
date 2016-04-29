package hs_mannheim.sysplace;

import android.view.View;

import hs_mannheim.gestureframework.animation.GestureAnimation;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;

public class SwipeHandler implements SwipeDetector.SwipeEventListener{

    private GestureAnimation sendAnimation;

    public SwipeHandler(GestureAnimation sendAnimation){
        this.sendAnimation = sendAnimation;
    }

    @Override
    public void onSwipeDetected(SwipeDetector swipeDetector, SwipeEvent event) {
        sendAnimation.play();
    }

    @Override
    public void onSwiping(SwipeDetector swipeDetector, TouchPoint touchPoint) {
        sendAnimation.onSwiping(touchPoint);
    }

    @Override
    public void onSwipeStart(SwipeDetector swipeDetector, TouchPoint touchPoint, View view) {
        sendAnimation.onSwipeStart(touchPoint);
    }

    @Override
    public void onSwipeEnd(SwipeDetector swipeDetector, TouchPoint touchPoint) {
        sendAnimation.onSwipeEnd(touchPoint);
    }
}
