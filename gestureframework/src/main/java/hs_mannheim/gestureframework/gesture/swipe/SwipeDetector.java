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
import android.view.View;

import java.util.ArrayList;

import hs_mannheim.gestureframework.gesture.GestureDetector;
import hs_mannheim.gestureframework.model.IViewContext;

public class SwipeDetector extends GestureDetector implements View.OnTouchListener {

    //TODO: make this thread safe
    private final ArrayList<SwipeConstraint> mSwipeConstraints;
    private final ArrayList<SwipeEventListener> mListeners;

    private SwipeEventListener mSwipeListener;
    private TouchPoint mStart;

    public SwipeDetector(IViewContext viewContext) {
        super(viewContext);
        mSwipeConstraints = new ArrayList<>();
        mListeners = new ArrayList<>();
    }

    public SwipeDetector addConstraint(SwipeConstraint constraint) {
        this.mSwipeConstraints.add(constraint);
        return this;
    }

    @Override
    public void setViewContext(IViewContext viewContext) {
        super.setViewContext(viewContext);
        mViewContext.getViewWrapper().registerObserver(this);
    }

    //TODO: Threadsafe
    public SwipeDetector addSwipeListener(SwipeEventListener listener) {
        mListeners.add(listener);
        return this;
    }

    //TODO: Threadsafe
    public SwipeDetector removeSwipeListener(SwipeEventListener listener) {
        mListeners.remove(listener);
        return this;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handle_down(event, v);
                return true;
            case MotionEvent.ACTION_UP:
                handle_up(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                handle_move(event);
                return true;
            default:
                return false;
        }
    }

    private void handle_move(MotionEvent event) {
        for (SwipeEventListener listener : mListeners) {
            listener.onSwiping(this, new TouchPoint(event));
        }
    }

    private void handle_down(MotionEvent event, View view) {
        mStart = new TouchPoint(event);
        for (SwipeEventListener listener : mListeners) {
            listener.onSwipeStart(this, new TouchPoint(event), view);
        }
    }

    private boolean handle_up(MotionEvent event) {
        TouchPoint end = new TouchPoint(event);
        SwipeEvent swipeEvent = new SwipeEvent(mStart, end, mViewContext.getDisplaySize());

        boolean isSwipe = true;

        //TODO: come up with something that doesn't force us to loop manually everytime we fire an event (as in C#)
        for (SwipeConstraint constraint : mSwipeConstraints) {
            if (!constraint.isValid(swipeEvent)) isSwipe = false;
        }

        if (isSwipe) {
            super.fireGestureDetected();

            for (SwipeEventListener listener : mListeners) {
                listener.onSwipeDetected(this, swipeEvent);
            }
        } else {
            for (SwipeEventListener listener : mListeners) {
                listener.onSwipeEnd(this, end);
            }
        }

        return isSwipe;
    }

    public interface SwipeEventListener {
        void onSwipeDetected(SwipeDetector swipeDetector, SwipeEvent event);

        void onSwiping(SwipeDetector swipeDetector, TouchPoint touchPoint);

        void onSwipeStart(SwipeDetector swipeDetector, TouchPoint touchPoint, View view);

        void onSwipeEnd(SwipeDetector swipeDetector, TouchPoint touchPoint);
    }
}