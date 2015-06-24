package hs_mannheim.pattern_interaction_model.Gestures;

public class SwipeDurationConstraint implements SwipeConstraint {
    private final long mMaxDuration;

    public SwipeDurationConstraint(long maxDuration) {
        this.mMaxDuration = maxDuration;
    }

    @Override
    public boolean isValid(SwipeDetector.SwipeEvent event) {
        return event.getDuration() < mMaxDuration;
    }
}
