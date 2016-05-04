package hs_mannheim.gestureframework.animation;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import hs_mannheim.gestureframework.R;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;


/**
 * Simple SEND Animation
 */
public class ElevateAndLeaveAnimation extends GestureAnimation {

    Context context;
    Animator swipeStartAnimator, swipeEndAnimator;
    DragAndDropper dragAndDropper;

    public ElevateAndLeaveAnimation(Context context, final ImageView view) {
        this.type = AnimationType.SEND;
        this.view = view;
        this.context = context;
        registerAnimators();
        registerDragAndDropper(false, true);
    }

    @Override
    public void play() {
        if(!animationRunning){
            playAnimator.start();
        } else {
            animatorQueue.add(playAnimator);
        }
    }

    @Override
    public void play(Bitmap image) {

    }

    @Override
    protected void handleSwipeStart(TouchPoint touchPoint) {
        if(!animationRunning){
            swipeStartAnimator.start();
        }else {
            animatorQueue.add(swipeStartAnimator);
        }

        dragAndDropper.setDeltaPoint(touchPoint);

    }

    @Override
    protected void handleSwipeEnd(TouchPoint touchPoint) {
        if(!animationRunning){
            swipeEndAnimator.start();
        } else {
            animatorQueue.add(swipeEndAnimator);
        }
    }

    @Override
    protected void handleSwiping(TouchPoint touchPoint) {
        dragAndDropper.dragDrop(touchPoint);
    }

    private void registerDragAndDropper(boolean shouldDragX, boolean shouldDragY){
        this.dragAndDropper = new DragAndDropper(shouldDragX, shouldDragY, view);
    }

    @Override
    protected void registerAnimators() {
        this.swipeStartAnimator =  AnimatorInflater.loadAnimator(context, R.animator.elevate);
        swipeStartAnimator.addListener(this);
        swipeStartAnimator.setTarget(view);

        this.swipeEndAnimator =  AnimatorInflater.loadAnimator(context, R.animator.lower);
        swipeEndAnimator.addListener(this);
        swipeEndAnimator.setTarget(view);

        this.playAnimator =  AnimatorInflater.loadAnimator(context, R.animator.elevate_leave);
        playAnimator.addListener(this);
        playAnimator.setTarget(view);
    }

    @Override
    public void onAnimationStart(Animator animation) {
        animationRunning = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        animationRunning = false;
        if(!animatorQueue.isEmpty()) {
            animatorQueue.get(0).start();
            animatorQueue.remove(animatorQueue.get(0));
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}