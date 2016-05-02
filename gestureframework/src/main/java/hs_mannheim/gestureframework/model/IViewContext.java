package hs_mannheim.gestureframework.model;

import android.graphics.Point;

public interface IViewContext {
    ViewWrapper getView();
    Point getDisplaySize();
}