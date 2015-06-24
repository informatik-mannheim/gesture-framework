package hs_mannheim.pattern_interaction_model.Gestures;

public interface SwipeConstraint {
    boolean isValid(SwipeDetector.SwipeEvent event);
}