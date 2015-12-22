package hs_mannheim.gestureframework.animation;

import android.view.View;

/**
 * Created by uselab on 22.12.2015.
 */
public abstract class GestureAnimation {
    protected View view;
    protected AnimationType type;

    public abstract void play();

    public View getView(){
        return view;
    }

    public AnimationType getType(){
        return type;
    }
}
