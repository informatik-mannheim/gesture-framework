package hs_mannheim.gestureframework.gesture.doubletap;

import android.view.MotionEvent;
import android.view.View;

import hs_mannheim.gestureframework.model.GestureDetector;
import hs_mannheim.gestureframework.model.IViewContext;

public class DoubleTapDetector extends GestureDetector implements View.OnTouchListener {
    private long lastTapTime = 0;
    private static final long MAX_DELTA = 300;

    public DoubleTapDetector(IViewContext viewContext) {
        super(viewContext);
    }

    @Override
    public void setViewContext(IViewContext viewContext) {
        super.setViewContext(viewContext);
        mViewContext.getMultipleTouchView().registerObserver(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            long tapTime = System.currentTimeMillis();
            if (tapTime - lastTapTime < MAX_DELTA) {
                fireGestureDetected();
            }
            lastTapTime = tapTime;
        }
        return false;
    }
}
