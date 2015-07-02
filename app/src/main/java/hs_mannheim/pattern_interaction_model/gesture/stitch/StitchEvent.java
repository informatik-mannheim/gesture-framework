package hs_mannheim.pattern_interaction_model.gesture.stitch;

import android.graphics.Point;

import java.io.Serializable;

import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeEvent;
import hs_mannheim.pattern_interaction_model.gesture.swipe.TouchPoint;

public class StitchEvent extends SwipeEvent implements Serializable {

    private final int TOLERANCE = 150;
    private Point mDisplaySize;

    public StitchEvent(TouchPoint start, TouchPoint end, Point displaySize) {
        super(start, end);
        mDisplaySize = displaySize;
    }

    /**
     * Determines whether the Swipe is ingoing, outgoing or internal
     * @return returns the Bounding of the Swipe
     */
    public Bounding getBounding() {
        Bounding bounding;

        int minX = mDisplaySize.x - TOLERANCE;
        int minY = mDisplaySize.y - TOLERANCE;

        switch (getOrientation()) {
            case NORTH:
                if (mEnd.getY() < TOLERANCE) {
                    bounding = Bounding.OUTBOUND;
                } else if (mStart.getY() > minY) {
                    bounding = Bounding.INBOUND;
                } else {
                    bounding = Bounding.INTERNAL;
                }
                break;
            case SOUTH:
                if (mEnd.getY() > minY) {
                    bounding = Bounding.OUTBOUND;
                } else if (mStart.getY() < TOLERANCE) {
                    bounding = Bounding.INBOUND;
                } else {
                    bounding = Bounding.INTERNAL;
                }
                break;
            case WEST:
                if (mEnd.getX() < TOLERANCE) {
                    bounding = Bounding.OUTBOUND;
                } else if (mStart.getX() > minX) {
                    bounding = Bounding.INBOUND;
                } else {
                    bounding = Bounding.INTERNAL;
                }
                break;
            case EAST:
                if (mEnd.getX() > minX) {
                    bounding = Bounding.OUTBOUND;
                } else if (mStart.getX() < TOLERANCE) {
                    bounding = Bounding.INBOUND;
                } else {
                    bounding = Bounding.INTERNAL;
                }
                break;
            default:
                bounding = Bounding.INTERNAL;
        }

        return bounding;
    }

    public enum Bounding {
        INBOUND, OUTBOUND, INTERNAL
    }

    @Override
    public String toString() {
        return super.toString() + String.format("(%s)", getBounding().toString());
    }
}
