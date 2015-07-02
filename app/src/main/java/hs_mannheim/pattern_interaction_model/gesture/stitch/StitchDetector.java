package hs_mannheim.pattern_interaction_model.gesture.stitch;


import hs_mannheim.pattern_interaction_model.connection.PostOffice;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDetector;
import hs_mannheim.pattern_interaction_model.modeltemp.GestureDetector;

import static hs_mannheim.pattern_interaction_model.modeltemp.GestureDetector.GestureEventListener;

/**
 *
 */
public class StitchDetector extends GestureDetector implements GestureEventListener {
    private final SwipeDetector mSwipeDetector;
    private PostOffice mPostOffice;

    public StitchDetector(PostOffice postOffice) {
        this.mPostOffice = postOffice;
        this.mSwipeDetector = new SwipeDetector();
        mSwipeDetector.registerGestureEventListener(this);
    }

    @Override
    public void fireGestureDetected() {
        super.fireGestureDetected();
    }

    @Override
    public void onGestureDetected() {
        // TODO: Initiate Protocol
    }
}
