package hs_mannheim.gestureframework.model;

import android.graphics.Point;

public interface IViewContext {
    MultipleTouchView getInteractionView();

    Point getDisplaySize();
}