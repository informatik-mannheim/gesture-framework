package hs_mannheim.gestureframework.gesture.swipe;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import hs_mannheim.gestureframework.model.GestureDetector;
import hs_mannheim.gestureframework.model.IViewContext;

public class SwipeDetector extends GestureDetector implements View.OnTouchListener{

    private final ArrayList<SwipeConstraint> mSwipeConstraints;
    private SwipeEventListener mSwipeListener;
    private TouchPoint mStart;

    public SwipeDetector(IViewContext viewContext) {
        super(viewContext);
        mViewContext.getInteractionView().setOnTouchListener(this);
        this.mSwipeConstraints = new ArrayList<>();
    }

    public SwipeDetector addConstraint(SwipeConstraint constraint) {
        this.mSwipeConstraints.add(constraint);
        return this;
    }

    @Override
    public void setViewContext(IViewContext viewContext) {
        super.setViewContext(viewContext);
        mViewContext.getInteractionView().setOnTouchListener(this);
    }

    public SwipeDetector addSwipeListener(SwipeEventListener listener) {
        this.mSwipeListener = listener;
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
            case MotionEvent.ACTION_MOVE:
                handle_move(event);
                return true;
            default:
                return false;
        }
    }

    private void handle_move(MotionEvent event) {
        if(mSwipeListener != null ){
            mSwipeListener.onSwiping(new TouchPoint(event));
        }
    }

    private void handle_down(MotionEvent event){
        mStart = new TouchPoint(event);
        mSwipeListener.onSwipeStart(new TouchPoint(event));
    }

    private boolean handle_up(MotionEvent event) {
        TouchPoint end = new TouchPoint(event);
        SwipeEvent swipeEvent = new SwipeEvent(mStart, end);

        for (SwipeConstraint constraint : mSwipeConstraints) {
            if (!constraint.isValid(swipeEvent)) return false;
        }

        if(mSwipeListener != null ){
            mSwipeListener.onSwipeDetected(swipeEvent);
        } else {
            mSwipeListener.onSwipeEnd(end);
        }

        super.fireGestureDetected();

        return true;
    }

    public interface SwipeEventListener {
        void onSwipeDetected(SwipeEvent event);
        void onSwiping(TouchPoint touchPoint);
        void onSwipeStart(TouchPoint touchPoint);
        void onSwipeEnd(TouchPoint touchPoint);
    }
}