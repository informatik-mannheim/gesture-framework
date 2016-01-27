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


public class PostcardFlipAnimationReceive extends GestureAnimation{

    Context context;
    Animator receiveAnimator;
    Bitmap postcard;
    Bitmap receivedImage;

    public PostcardFlipAnimationReceive(Context context, final ImageView view) {
        this.type = AnimationType.RECEIVE;
        this.view = view;
        this.context = context;

        postcard = BitmapFactory.decodeResource(context.getResources(), R.drawable.postcard);
        receivedImage = ((BitmapDrawable)view.getDrawable()).getBitmap();

        registerAnimators();
    }

    @Override
    public void play() {
        if(!animationRunning){
            receiveAnimator.start();
            animatorQueue.add(receiveAnimator);
        } else {
            animatorQueue.add(receiveAnimator);
        }
    }

    @Override
    public void play(Bitmap image) {

    }

    @Override
    protected void handleSwipeStart(TouchPoint touchPoint) {

    }

    @Override
    protected void handleSwipeEnd(TouchPoint touchPoint) {

    }

    @Override
    protected void handleSwiping(TouchPoint touchPoint) {

    }

    @Override
    protected void registerAnimators() {
        receiveAnimator = AnimatorInflater.loadAnimator(context, R.animator.postcardreceive);
        receiveAnimator.addListener(this);
        receiveAnimator.setTarget(view);
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
