package hs_mannheim.pattern_interaction_model.gesture.swipe;

public class SwipeDistanceConstraint implements SwipeConstraint {

    private float mMaxDistance;

    public SwipeDistanceConstraint(float maxDistance) {
        mMaxDistance = maxDistance;
    }

    @Override
    public boolean isValid(SwipeDetector.SwipeEvent event) {
        return event.getDistance() < mMaxDistance;
    }
}
