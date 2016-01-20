package hs_mannheim.gestureframework.animation;

import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.RelativeLayout;

import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;

/**
 * Simple SEND Animation
 */
public class ElevateAndLeaveAnimation extends GestureAnimation{


    public ElevateAndLeaveAnimation(final View view) {
        super.type = AnimationType.SEND;
        super.view = view;
        view.setElevation(0);
    }

    @Override
    public void play() {
        view.setElevation(0);
        view.animate()
            .translationZ(400)
            .setDuration(100);
    }

    @Override
    public void handleSwiping(TouchPoint touchPoint) {
        /*ViewPropertyAnimator animator = view.animate();
        view.animate()
                .translationZ(200)
                .setDuration(1000);*/

        /*RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                .getLayoutParams();
        layoutParams.leftMargin = X - _xDelta;
        layoutParams.topMargin = Y - _yDelta;
        layoutParams.rightMargin = -250;
        layoutParams.bottomMargin = -250;
        view.setLayoutParams(layoutParams);*/

    }

    @Override
    public void startSwipe() {
        /*RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        currentPoint.setX(X - lParams.leftMargin);
        _yDelta = Y - lParams.topMargin;*/
    }

    @Override
    public void onSwipeEnd(TouchPoint touchPoint) {

    }
}
