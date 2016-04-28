package hs_mannheim.gestureframework.model;

import android.database.Observable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MultipleTouchView extends Observable<View.OnTouchListener> implements View.OnTouchListener {
    private final View mView;
    private boolean mWasHandled = false;


    public MultipleTouchView(View view)  {
        mView = view;
        mView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mWasHandled = false;

        for (View.OnTouchListener listener : mObservers) {
            boolean wasHandled = listener.onTouch(view, motionEvent);

            if(!mWasHandled && wasHandled) {
                mWasHandled = true;
            }
        }

        return mWasHandled;
    }

    @Override
    public void registerObserver(View.OnTouchListener observer) {
        super.registerObserver(observer);
        Log.d("MTV", "Registered a new observer: " + observer.toString());
    }
}
