package hs_mannheim.pattern_interaction_model.Gestures;

public class SwipeDirectionConstraint implements SwipeConstraint {

    private final SwipeDetector.Direction mDirection;

    public SwipeDirectionConstraint(SwipeDetector.Direction direction) {
        this.mDirection = direction;
    }

    public boolean isValid(SwipeDetector.SwipeEvent event) {
        return event.getDirection().equals(mDirection);
    }
}
