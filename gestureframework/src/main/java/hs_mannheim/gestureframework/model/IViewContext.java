package hs_mannheim.gestureframework.model;

import android.graphics.Point;

/**
 * Provides information about the {@link android.view.View} (wrapped in a {@link ViewWrapper}) to
 * which to connect {@link android.view.GestureDetector} instances (e.g. to listen for TouchEvents).
 */
public interface IViewContext {
    ViewWrapper getView();
    Point getDisplaySize();
}