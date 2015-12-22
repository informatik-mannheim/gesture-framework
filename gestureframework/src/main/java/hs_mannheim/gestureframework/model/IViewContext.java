package hs_mannheim.gestureframework.model;

import android.graphics.Point;
import android.view.View;

public interface IViewContext {
    public View getInteractionView();
    public Point getDisplaySize();
}