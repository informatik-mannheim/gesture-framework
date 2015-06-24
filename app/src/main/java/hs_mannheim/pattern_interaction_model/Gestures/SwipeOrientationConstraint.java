package hs_mannheim.pattern_interaction_model.Gestures;

public class SwipeOrientationConstraint implements SwipeConstraint {

    private final SwipeDetector.Orientation mOrientation;

    public SwipeOrientationConstraint(SwipeDetector.Orientation orientation) {
        mOrientation = orientation;
    }

    @Override
    public boolean isValid(SwipeDetector.SwipeEvent event) {
        return mOrientation.equals(event.getOrientation());
    }
}
