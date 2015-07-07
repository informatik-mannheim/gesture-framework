package hs_mannheim.pattern_interaction_model.model;

import android.graphics.Point;
import android.view.View;

public interface IViewContext {
    public View getInteractionView();
    public Point getDisplaySize();
}