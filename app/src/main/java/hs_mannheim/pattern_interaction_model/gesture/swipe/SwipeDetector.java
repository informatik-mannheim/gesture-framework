package hs_mannheim.pattern_interaction_model.gesture.swipe;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import hs_mannheim.pattern_interaction_model.model.GestureDetector;

public class SwipeDetector extends GestureDetector implements View.OnTouchListener{

    private final ArrayList<SwipeConstraint> mSwipeConstraints;
    private SwipeEventListener mSwipeListener;
    private Point mStart;

    public SwipeDetector() {
        this.mSwipeConstraints = new ArrayList<>();
    }

    public SwipeDetector addConstraint(SwipeConstraint constraint) {
        this.mSwipeConstraints.add(constraint);
        return this;
    }

    public SwipeDetector attachToView(View view, SwipeEventListener listener) {
        view.setOnTouchListener(this);
        mSwipeListener = listener;
        return this;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handle_down(event);
                return true;
            case MotionEvent.ACTION_UP:
                return handle_up(event);
            default:
                return false;
        }
    }

    private void handle_down(MotionEvent event) {
        mStart = new Point(event);
    }

    private boolean handle_up(MotionEvent event) {
        SwipeEvent swipeEvent = new SwipeEvent(mStart, new Point(event));

        for (SwipeConstraint constraint : mSwipeConstraints) {
            if (!constraint.isValid(swipeEvent)) return false;
        }

        mSwipeListener.onSwipeDetected(swipeEvent);
        super.onGestureDetected();

        return true;
    }

    public class Point {
        private float x;
        private float y;
        private long time;

        public Point(MotionEvent event) {
            setX(event.getX());
            setY(event.getY());
            setTime(event.getEventTime());
        }

        public long getTime() {
            return time;
        }

        private void setTime(long time) {
            this.time = time;
        }

        public float getX() {
            return x;
        }

        private void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        private void setY(float y) {
            this.y = y;
        }

        private float deltaX(Point other) {
            return getX() - other.getX();
        }

        private float deltaY(Point other) {
            return getY() - other.getY();
        }

        private float distanceTo(Point other) {
            return (float) Math.sqrt(Math.pow(deltaX(other), 2) + Math.pow(deltaY(other), 2));
        }
    }

    public enum Direction {
        HORIZONTAL, VERTICAL
    }

    public enum Orientation {
        NORTH, WEST, SOUTH, EAST
    }

    public class SwipeEvent {
        private final Point mStart;
        private final Point mEnd;
        private final float mDeltaX;
        private final float mDeltaY;

        public SwipeEvent(Point start, Point end) {
            this.mStart = start;
            this.mEnd = end;
            this.mDeltaX = end.deltaX(start);
            this.mDeltaY = end.deltaY(start);
        }

        public float getDistance() {
            return this.mEnd.distanceTo(this.mStart);
        }

        public Direction getDirection() {
            return Math.abs(mDeltaX) > Math.abs(mDeltaY) ? Direction.HORIZONTAL : Direction.VERTICAL;
        }

        public Orientation getOrientation() {
            if (getDirection().equals(Direction.HORIZONTAL)) {
                return mDeltaX < 0 ? Orientation.WEST : Orientation.EAST;
            } else
                return mDeltaY < 0 ? Orientation.NORTH : Orientation.SOUTH;
        }

        public long getDuration() {
            return Math.abs(this.mEnd.getTime() - this.mStart.getTime());
        }

        @Override
        public String toString() {
            return String.format("SwipeEvent with distance %.2f and duration %d in %s direction %s.",
                    getDistance(), getDuration(), getDirection(), getOrientation());
        }
    }

    public interface SwipeEventListener {
        void onSwipeDetected(SwipeEvent event);
    }
}