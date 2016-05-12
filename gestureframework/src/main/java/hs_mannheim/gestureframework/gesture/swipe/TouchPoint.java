/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *    Hochschule Mannheim University of Applied Sciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package hs_mannheim.gestureframework.gesture.swipe;

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