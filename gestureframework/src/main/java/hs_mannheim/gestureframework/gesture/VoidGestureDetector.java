package hs_mannheim.gestureframework.gesture;

import hs_mannheim.gestureframework.gesture.GestureDetector;

/**
 * Stub implementation of {@link GestureDetector} that does nothing.
 */
public class VoidGestureDetector extends GestureDetector{

    /**
     * viewContext is always null as this should never do anything.
     */
    public VoidGestureDetector() {
        super(null);
    }
}
