package hs_mannheim.gestureframework.animation;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;


import java.util.ArrayList;

import hs_mannheim.gestureframework.animation.AnimationType;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;


public abstract class GestureAnimation implements Animator.AnimatorListener{

    //TODO: make separate animatorlistener
    //TODO: split send- and receiveanimations into separate abstract classes and extend from there
    protected ImageView view;
    protected AnimationType type;
    protected TouchPoint startPoint, currentPoint;
    protected boolean animationRunning;
    protected Animator playAnimator;
    protected ArrayList<Animator> animatorQueue = new ArrayList<>();

    public abstract void play();
    public abstract void play(Bitmap image);

    public void onSwiping(TouchPoint touchPoint){
        if(this.type.equals(AnimationType.SEND)){
            handleSwiping(touchPoint);
        }
    }

    public ImageView getView(){
        return view;
    }

    public AnimationType getType(){
        return type;
    }

    public void onSwipeStart(TouchPoint touchPoint) {
        handleSwipeStart(touchPoint);
    }

    public void onSwipeEnd(TouchPoint touchPoint) {
        handleSwipeEnd(touchPoint);
    }

    protected abstract void handleSwipeStart(TouchPoint touchPoint);

    protected abstract void handleSwipeEnd(TouchPoint touchPoint);

    protected abstract void handleSwiping(TouchPoint touchPoint);

    protected abstract void registerAnimators();
}
