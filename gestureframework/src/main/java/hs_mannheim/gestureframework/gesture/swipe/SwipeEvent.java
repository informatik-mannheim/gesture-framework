package hs_mannheim.gestureframework.gesture.swipe;

import java.io.Serializable;

public class SwipeEvent implements Serializable {
    protected final int TOLERANCE = 150;
    protected final TouchPoint mStart;
    protected final TouchPoint mEnd;
    private final float mDeltaX;
    private final float mDeltaY;

    public SwipeEvent(TouchPoint start, TouchPoint end) {
        this.mStart = start;
        this.mEnd = end;
        this.mDeltaX = end.deltaX(start);
        this.mDeltaY = end.deltaY(start);
    }

    /**
     * Returns the euclidean distance between start and end of the swipe
     * @return the swiped distance
     */
    public float getDistance() {
        return this.mEnd.distanceTo(this.mStart);
    }

    /**
     *  Returns the direction in which the swipe was performed (horizontal or vertical)
     * @return the swipe direction
     */
    public Direction getDirection() {
        return Math.abs(mDeltaX) > Math.abs(mDeltaY) ? Direction.HORIZONTAL : Direction.VERTICAL;
    }

    /**
     * Return the orientation in which the swipe was performed, according to the device
     * @return the orientation
     */
    public Orientation getOrientation() {
        if (getDirection().equals(Direction.HORIZONTAL)) {
            return mDeltaX < 0 ? Orientation.WEST : Orientation.EAST;
        } else {
            return mDeltaY < 0 ? Orientation.NORTH : Orientation.SOUTH;
        }
    }

    /**
     * Calculates the duration between the start and end of the swipe.
     * @return the duration of the swipe
     */
    public long getDuration() {
        return Math.abs(this.mEnd.getTime() - this.mStart.getTime());
    }

    /**
     * Returns the point where the swipe was started.
     * @return the start point of the swipe
     */
    public TouchPoint getStartOfSwipe() {
        return mStart;
    }

    /**
     * Returns the point where the swipe was ended.
     * @return the end point of the swipe
     */
    public TouchPoint getEndOfSwipe() {
        return mEnd;
    }

    @Override
    public String toString() {
        return String.format("SwipeEvent with distance %.2f and duration %d in %s direction %s ending at x: %.0f and y: %.0f",
                getDistance(), getDuration(), getDirection(), getOrientation(), getEndOfSwipe().getX(), getEndOfSwipe().getY());
    }

    public enum Direction {
        HORIZONTAL, VERTICAL
    }

    public enum Orientation {
        NORTH, WEST, SOUTH, EAST
    }
}
