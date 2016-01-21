package hs_mannheim.gestureframework.animation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import hs_mannheim.gestureframework.R;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;

/**
 * Simple SEND Animation
 */
public class PostCardFlipAnimation extends GestureAnimation{

    Context context;
    Animator swipeStartAnimator, swipeEndAnimator, flipLeftInAnimator, flipRightInAnimator;
    DragAndDropper dragAndDropper;
    Bitmap postcard;
    Bitmap origImage;

    public PostCardFlipAnimation(Context context, final ImageView view) {
        this.type = AnimationType.SEND;
        this.view = view;
        this.context = context;

        postcard = BitmapFactory.decodeResource(context.getResources(), R.drawable.postcard);
        origImage = ((BitmapDrawable)view.getDrawable()).getBitmap();

        registerAnimators();
        registerDragAndDropper(false, true);
    }

    @Override
    public void play() {
        if(!animationRunning){
            swipeStartAnimator.start();
            animatorQueue.add(playAnimator);
        } else {
            animatorQueue.add(playAnimator);
        }
    }

    @Override
    protected void handleSwipeStart(TouchPoint touchPoint) {
        //TODO: postcard stays small for some reason
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
        swipeStartAnimator =  AnimatorInflater.loadAnimator(context, R.animator.flip_left_out);
        swipeStartAnimator.addListener(this);
        swipeStartAnimator.setTarget(view);

        swipeEndAnimator =  AnimatorInflater.loadAnimator(context, R.animator.flip_right_out);
        swipeEndAnimator.addListener(this);
        swipeEndAnimator.setTarget(view);

        playAnimator =  AnimatorInflater.loadAnimator(context, R.animator.elevate_leave);
        playAnimator.addListener(this);
        playAnimator.setTarget(view);

        flipLeftInAnimator = AnimatorInflater.loadAnimator(context, R.animator.flip_left_in);
        flipLeftInAnimator.addListener(this);
        flipLeftInAnimator.setTarget(view);

        flipRightInAnimator = AnimatorInflater.loadAnimator(context, R.animator.flip_right_in);
        flipRightInAnimator.addListener(this);
        flipRightInAnimator.setTarget(view);


    }

    @Override
    public void onAnimationStart(Animator animation) {
        animationRunning = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        animationRunning = false;

        if (animation.equals(swipeStartAnimator)){
            view.setImageBitmap(postcard);
            flipLeftInAnimator.start();
        }

        if (animation.equals(swipeEndAnimator)){
            view.setImageBitmap(origImage);
            flipRightInAnimator.start();
        }

        if (!animatorQueue.isEmpty()) {
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