package hs_mannheim.pattern_interaction_model.gesture.stitch;

import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeConstraint;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeEvent;

public class StitchEdgeConstraint implements SwipeConstraint {

    private final int mScreenX;
    private final int mScreenY;
    private final StitchEvent.Bounding mBounding;

    public StitchEdgeConstraint(int screenX, int screenY, StitchEvent.Bounding bounding) {
        this.mScreenX = screenX;
        this.mScreenY = screenY;
        this.mBounding = bounding;
    }

    @Override
    public boolean isValid(SwipeEvent event) {
        return true;
        //return event.getBounding(mScreenX, mScreenY).equals(mBounding);
    }
}
