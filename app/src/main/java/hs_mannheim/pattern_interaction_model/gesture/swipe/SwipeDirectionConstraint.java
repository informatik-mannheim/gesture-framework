package hs_mannheim.pattern_interaction_model.gesture.swipe;

public class SwipeDirectionConstraint implements SwipeConstraint {
    private final SwipeEvent.Direction mDirection;

    public SwipeDirectionConstraint(SwipeEvent.Direction direction) {
        this.mDirection = direction;
    }

    public boolean isValid(SwipeEvent event) {
        return event.getDirection().equals(mDirection);
    }
}
