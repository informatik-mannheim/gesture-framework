package hs_mannheim.pattern_interaction_model.Gestures;

import android.util.Log;

public class SwipeDirectionConstraint implements SwipeConstraint {

    private final SwipeDetector.Direction mDirection;

    public SwipeDirectionConstraint(SwipeDetector.Direction direction) {
        this.mDirection = direction;
    }

    @Override
    public boolean isValid(SwipeDetector.SwipeEvent event) {
        Log.d("..", event.getDirection().getFieldDescription());
        Log.d("..", mDirection.getFieldDescription());
        return event.getDirection().equals(mDirection);
    }
}
