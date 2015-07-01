package hs_mannheim.pattern_interaction_model.gesture.swipe;

public class SwipeOrientationConstraint implements SwipeConstraint {

    private final SwipeEvent.Orientation mOrientation;

    public SwipeOrientationConstraint(SwipeEvent.Orientation orientation) {
        mOrientation = orientation;
    }

    @Override
    public boolean isValid(SwipeEvent event) {
        return mOrientation.equals(event.getOrientation());
    }
}
