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

package hs_mannheim.gestureframework.gesture.doubletap;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import hs_mannheim.gestureframework.gesture.GestureDetector;
import hs_mannheim.gestureframework.model.IViewContext;

public class DoubleTapDetector extends GestureDetector implements View.OnTouchListener {
    private MotionEvent mLastDown, mLastUp;
    private boolean mMovedOutOfTapRegion;
    private static final long MAX_TIME_DELTA = 300, MIN_TIME_DELTA = 50;
    private static final int MAX_DISTANCE = 150;
    private static final String TAG = "[DoubleTapDetector]";

    /**
     * Detects a DoubleTap Gesture. Needs an {@link IViewContext} to listen for TouchEvents.
     *
     * @param viewContext The {@link IViewContext} that generates TouchEvents.
     */
    public DoubleTapDetector(IViewContext viewContext) {
        super(viewContext);
    }

    @Override
    public void setViewContext(IViewContext viewContext) {
        super.setViewContext(viewContext);
        mViewContext.getView().registerObserver(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mLastDown != null && mLastUp != null && isDoubleTap(mLastDown, mLastUp, motionEvent)) {
                    fireGestureDetected();
                }
                mMovedOutOfTapRegion = false;
                mLastDown = MotionEvent.obtain(motionEvent);
                break;
            case MotionEvent.ACTION_UP:
                mLastUp = MotionEvent.obtain(motionEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveDistanceX = Math.abs((int) mLastDown.getX() - (int) motionEvent.getX());
                int moveDistanceY = Math.abs((int) mLastDown.getY() - (int) motionEvent.getY());

                if (moveDistanceX > MAX_DISTANCE || moveDistanceY > MAX_DISTANCE) {
                    mMovedOutOfTapRegion = true;
                }
                break;
        }
        return false;
    }

    private boolean isDoubleTap(MotionEvent firstDown, MotionEvent firstUp,
                                MotionEvent secondDown) {
        if (mMovedOutOfTapRegion) {
            Log.d(TAG, "Moved out of tap region!");
            return false;
        }

        final long deltaTime = secondDown.getEventTime() - firstUp.getEventTime();
        if (deltaTime > MAX_TIME_DELTA || deltaTime < MIN_TIME_DELTA) {
            Log.d(TAG, "Tap timing bad!" + deltaTime);
            return false;
        }

        int deltaX = Math.abs((int) firstDown.getX() - (int) secondDown.getX());
        int deltaY = Math.abs((int) firstDown.getY() - (int) secondDown.getY());
        if (deltaX > MAX_DISTANCE || deltaY > MAX_DISTANCE) {
            Log.d(TAG, "Tap distance too big!");
            return false;
        } else {
            return true;
        }
    }
}
