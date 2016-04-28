package hs_mannheim.gestureframework.gesture.doubletap;

import android.view.MotionEvent;
import android.view.View;

import hs_mannheim.gestureframework.model.GestureDetector;
import hs_mannheim.gestureframework.model.IViewContext;

public class DoubleTapDetector extends GestureDetector implements View.OnTouchListener {
    private long lastTapTime = 0;
    private static final long MAX_DELTA = 300;

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
        mViewContext.getMultipleTouchView().registerObserver(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        /**
         * TODO: Eventuell von https://github.com/android/platform_frameworks_base/blob/4535e11fb7010f2b104d3f8b3954407b9f330e0f/core/java/android/view/GestureDetector.java#L750
         * inspirieren lassen -> Nicht nur die Zeit nehmen, sondern auch die Region.
         * Außerdem eine Tap-Region definieren.
         *
         */
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
