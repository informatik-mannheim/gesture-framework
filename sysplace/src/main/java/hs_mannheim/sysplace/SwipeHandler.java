package hs_mannheim.sysplace;

import android.view.View;

import hs_mannheim.gestureframework.animation.DragAndDropper;
import hs_mannheim.gestureframework.animation.GestureAnimation;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;
import hs_mannheim.gestureframework.model.GestureDetector;

/**
 * Created by Dominick Madden on 20.04.2016.
 */
public class SwipeHandler implements SwipeDetector.SwipeEventListener{

    private GestureAnimation sendAnimation;

    public SwipeHandler(GestureAnimation sendAnimation){
        this.sendAnimation = sendAnimation;
    }

    @Override
    public void onSwipeDetected(SwipeEvent event) {
        sendAnimation.play();
    }

    @Override
    public void onSwiping(TouchPoint touchPoint) {
        sendAnimation.onSwiping(touchPoint);
    }

    @Override
    public void onSwipeStart(TouchPoint touchPoint, View view) {
        sendAnimation.onSwipeStart(touchPoint);
    }

    @Override
    public void onSwipeEnd(TouchPoint touchPoint) {
        sendAnimation.onSwipeEnd(touchPoint);
    }
}
