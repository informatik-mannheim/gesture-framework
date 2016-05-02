package hs_mannheim.gestureframework.gesture.pinch;

import hs_mannheim.gestureframework.gesture.GestureDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.model.IViewContext;

/**
 * Detects an asynchronous Pinch. It's enough if the local event is valid in itself without checking
 * for a corresponding remote Event.
 */
public class PinchDetector extends GestureDetector {

    private final SwipeDetector mSwipeDetector;

    /**
     * Creates a {@link SwipeDetector} that is constrained to Outbound Events only. Or can we just
     * subclass the StitchDetector? Also implement a SpreadDetector (it is possible to have a
     * synchronous version for that!)
     * TODO: Implement this!
     * @param viewContext
     */
    public PinchDetector(IViewContext viewContext) {
        super(viewContext);
        mSwipeDetector = new SwipeDetector(viewContext);
    }
}
