package hs_mannheim.gestureframework.gesture.swipe;

/**
 * Created by Dome on 06.01.2016.
 */
public class SwipeMinDistanceConstraint implements SwipeConstraint {

    private float mMinDistance;

    public SwipeMinDistanceConstraint(float minDistance) {
        mMinDistance = minDistance;
    }

    @Override
    public boolean isValid(SwipeEvent event) {
        return event.getDistance() >= mMinDistance;
    }
}