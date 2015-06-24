package hs_mannheim.pattern_interaction_model.Gestures;

import android.view.MotionEvent;
import android.view.View;

public class Swipe implements View.OnTouchListener {
    private boolean mRecording;

    private Point mStart;
    private Point mEnd;
    private SwipeEventListener mSwipeListener;

    public void attachToView(View view, SwipeEventListener listener) {
        view.setOnTouchListener(this);
        mRecording = false;
        mSwipeListener = listener;
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
        mRecording = true;

        mStart = new Point(event);
    }

    private boolean handle_up(MotionEvent event) {
        mRecording = false;
        mEnd = new Point(event);
        SwipeEvent delta = mEnd.delta(mStart);
        mSwipeListener.onSwipeDetected(delta);
        return true;
    }


    class Point {
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

        public SwipeEvent delta(Point other) {
            return new SwipeEvent(deltaX(other), deltaY(other), distanceTo(other), getTime() - other.getTime());
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

    public class SwipeEvent {
        private final long timeDifference;
        private float deltaX;
        private float deltaY;
        private float distance;

        public SwipeEvent(float deltaX, float deltaY, float distance, long timeDifference) {

            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.distance = distance;
            this.timeDifference = timeDifference;
        }

        public float getDeltaX() {
            return deltaX;
        }

        public void setDeltaX(float deltaX) {
            this.deltaX = deltaX;
        }

        public float getDeltaY() {
            return deltaY;
        }

        public void setDeltaY(float deltaY) {
            this.deltaY = deltaY;
        }

        public float getDistance() {
            return distance;
        }

        public void setDistance(float distance) {
            this.distance = distance;
        }

        public long getTimeDifference() {
            return Math.abs(timeDifference);
        }

        @Override
        public String toString() {
            return String.format("SwipeEvent with distance %.2f and duration %d", getDistance(), getTimeDifference());
        }
    }

    public interface SwipeEventListener {
        void onSwipeDetected(SwipeEvent event);
    }
}