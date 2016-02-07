package hs_mannheim.gestureframework.animation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import hs_mannheim.gestureframework.R;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;


public class PostcardFlipAnimationReceive extends GestureAnimation{

    Context context;
    Animator flyInAnimator;
    Bitmap postcard;
    Bitmap receivedImage;


    public PostcardFlipAnimationReceive(Context context, final ImageView view) {
        this.type = AnimationType.RECEIVE;
        this.view = view;
        this.context = context;

        postcard = BitmapFactory.decodeResource(context.getResources(), R.drawable.postcard);

        registerAnimators();
    }

    @Override
    public void play() {}

    @Override
    public void play(Bitmap image) {
        receivedImage = image;
        flyInAnimator.start();
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

    Animator flipLeftInAnimator, flipRightInAnimator, flipLeftOutAnimator, flipRightOutAnimator, lowerAnimator;
    @Override
    protected void registerAnimators() {
        flyInAnimator = AnimatorInflater.loadAnimator(context, R.animator.postcardreceive);
        flyInAnimator.addListener(this);
        flyInAnimator.setTarget(view);

        flipLeftOutAnimator =  AnimatorInflater.loadAnimator(context, R.animator.postcardsend_flip_left_out);
        flipLeftOutAnimator.addListener(this);
        flipLeftOutAnimator.setTarget(view);

        flipRightOutAnimator =  AnimatorInflater.loadAnimator(context, R.animator.postcardsend_flip_right_out);
        flipRightOutAnimator.addListener(this);
        flipRightOutAnimator.setTarget(view);

        flipLeftInAnimator = AnimatorInflater.loadAnimator(context, R.animator.postcardsend_flip_left_in);
        flipLeftInAnimator.addListener(this);
        flipLeftInAnimator.setTarget(view);

        flipRightInAnimator = AnimatorInflater.loadAnimator(context, R.animator.postcardsend_flip_right_in);
        flipRightInAnimator.addListener(this);
        flipRightInAnimator.setTarget(view);

        lowerAnimator = AnimatorInflater.loadAnimator(context, R.animator.lower);
        lowerAnimator.addListener(this);
        lowerAnimator.setTarget(view);
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if (animation.equals(flyInAnimator)){
            view.setImageBitmap(postcard);
            flipLeftOutAnimator.setStartDelay(1500);
            flipLeftOutAnimator.start();
        }

        if (!animatorQueue.isEmpty()) {
            animatorQueue.get(0).start();
            animatorQueue.remove(animatorQueue.get(0));
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {

        Log.d("Animation Ended", animation.toString());
        if (animation.equals(flipLeftOutAnimator)){
            view.setImageBitmap(receivedImage);
            flipLeftInAnimator.start();
        }

        if (animation.equals(flipLeftInAnimator)){
            lowerAnimator.start();
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