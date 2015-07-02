package hs_mannheim.pattern_interaction_model.gesture.stitch;


import android.graphics.Point;
import android.view.View;

import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeConstraint;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDetector;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeEvent;
import hs_mannheim.pattern_interaction_model.model.GestureDetector;
import hs_mannheim.pattern_interaction_model.model.IPostOffice;
import hs_mannheim.pattern_interaction_model.model.StitchPacket;

import static hs_mannheim.pattern_interaction_model.model.GestureDetector.GestureEventListener;

/**
 * Detector for synchronous Stitch Gestures. Needs connection to another device to have a handshake
 * if the stitch succeeded.
 */
public class StitchDetector extends GestureDetector implements SwipeDetector.SwipeEventListener {
    private final SwipeDetector mSwipeDetector;
    private IPostOffice mPostOffice;
    private Point mDisplay;

    public StitchDetector(IPostOffice postOffice, Point display) {
        this.mPostOffice = postOffice;
        mDisplay = display;
        this.mSwipeDetector = new SwipeDetector();
        mSwipeDetector.addSwipeListener(this);
    }

    public void addConstraint(SwipeConstraint constraint) {
        mSwipeDetector.addConstraint(constraint);
    }

    public void attachToView(View view) {
        mSwipeDetector.attachToView(view);
    }

    @Override
    public void onSwipeDetected(SwipeEvent event) {
        StitchEvent stitchEvent = new StitchEvent(event.getStartOfSwipe(), event.getEndOfSwipe(), mDisplay);
        mPostOffice.send(new StitchPacket("Stitch on other device", stitchEvent.getBounding(), stitchEvent.getOrientation()));
        fireGestureDetected();
    }
}
