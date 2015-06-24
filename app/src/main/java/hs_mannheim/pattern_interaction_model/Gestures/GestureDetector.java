package hs_mannheim.pattern_interaction_model.Gestures;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GestureDetector implements android.view.GestureDetector.OnGestureListener{
    private final String TAG = "GestureDetection";
    private final android.view.GestureDetector mGestureDetector;

    public GestureDetector(Context context) {
        this.mGestureDetector = new android.view.GestureDetector(context, this);
    }

    public void attachToView(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(TAG, "finger down");
        Log.d(TAG, "event: " + e.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "single tap");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(TAG, "scroll with distance (" + distanceX + ", " + distanceY + ")");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(TAG, "long press");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "fling");
        return true;
    }
}
