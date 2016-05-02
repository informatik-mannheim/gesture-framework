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
        mViewContext.getView().registerObserver(this);
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
        SwipeEvent swipeEvent = new SwipeEvent(mStart, end);

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