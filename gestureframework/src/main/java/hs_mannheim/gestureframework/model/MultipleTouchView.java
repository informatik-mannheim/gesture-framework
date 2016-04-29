package hs_mannheim.gestureframework.model;

import android.database.Observable;
import android.view.MotionEvent;
import android.view.View;

public class MultipleTouchView extends Observable<View.OnTouchListener> implements View.OnTouchListener {
    private final View mView;

    public MultipleTouchView(View view)  {
        mView = view;
        mView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        for (View.OnTouchListener listener : mObservers) {
            listener.onTouch(view, motionEvent);
        }

        return true;
    }
}
