package hs_mannheim.pattern_interaction_model.gesture.swipe;

import android.view.MotionEvent;

import java.io.Serializable;

public class TouchPoint implements Serializable {
    private float x;
    private float y;
    private long time;

    public TouchPoint(MotionEvent event) {
        this(event.getRawX(), event.getRawY(), event.getEventTime());
    }

    public TouchPoint(float x, float y, long timestamp) {
        setX(x);
        setY(y);
        setTime(timestamp);
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

    public float deltaX(TouchPoint other) {
        return getX() - other.getX();
    }

    public float deltaY(TouchPoint other) {
        return getY() - other.getY();
    }

    public float distanceTo(TouchPoint other) {
        return (float) Math.sqrt(Math.pow(deltaX(other), 2) + Math.pow(deltaY(other), 2));
    }

    @Override
    public String toString() {
        return String.format("x: %f, y: %f", this.getX(), this.getY());
    }
}