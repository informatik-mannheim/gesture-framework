package hs_mannheim.gestureframework.model;

import android.database.Observable;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

public class ViewWrapper extends Observable<View.OnTouchListener> implements View.OnTouchListener {
    private final View mView;
    private final static Set<ViewWrapper> mWrappers = new HashSet<>();

    public ViewWrapper(View view)  {
        mView = view;
        mView.setOnTouchListener(this);
    }

    public static ViewWrapper wrap(View view) {
        for(ViewWrapper wrapper : mWrappers) {
            if(wrapper.getView().equals(view)) {
                return wrapper;
            }
        }

        ViewWrapper wrapper = new ViewWrapper(view);
        mWrappers.add(wrapper);
        return wrapper;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        for (View.OnTouchListener listener : mObservers) {
            listener.onTouch(view, motionEvent);
        }

        return true;
    }

    public View getView() {
        return mView;
    }
}
