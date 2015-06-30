package hs_mannheim.pattern_interaction_model.gesture.swipe;

public interface SwipeConstraint {
    boolean isValid(SwipeDetector.SwipeEvent event);
}