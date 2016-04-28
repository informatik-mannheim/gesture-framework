package hs_mannheim.pattern_interaction_model;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import hs_mannheim.gestureframework.animation.GestureAnimation;
import hs_mannheim.gestureframework.animation.PostCardFlipAnimationSend;
import hs_mannheim.gestureframework.animation.PostcardFlipAnimationReceive;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;
import hs_mannheim.gestureframework.model.LifecycleEvent;
import hs_mannheim.gestureframework.model.IPacketReceiver;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.ImagePacket;
import hs_mannheim.gestureframework.model.SysplaceContext;
import hs_mannheim.gestureframework.model.Packet;
import hs_mannheim.gestureframework.model.PacketType;
import hs_mannheim.gestureframework.model.SerializableImage;


public class InteractionActivity extends ActionBarActivity implements SwipeDetector.SwipeEventListener, IPacketReceiver, TextWatcher, IViewContext {

    public final static String MODEL = Build.MODEL;
    private ImageView mImageView, mImageViewCopy;
    private GestureAnimation sendAnimation, receiveAnimation;
    private SwipeDetector mSwipeDetector;

    private boolean shouldSendCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction);

        TextView header = (TextView) findViewById(R.id.tv_header_interaction);
        ((EditText) findViewById(R.id.etMessage)).addTextChangedListener(this);

        mImageView = (ImageView) findViewById(R.id.ivPic);


        ///////////////////////////// TODO: Do this somewhere else (configurator)
        shouldSendCopy = true;
        this.sendAnimation = new PostCardFlipAnimationSend(this, mImageView);
        this.receiveAnimation = new PostcardFlipAnimationReceive(this, mImageView);
        /////////////////////////////

        if (shouldSendCopy) {
            //TODO: this part should be in the framework, not the app
            mImageViewCopy = (ImageView) findViewById(R.id.imageViewCopy);

        }
        header.setText(MODEL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SysplaceContext sysplaceContext = ((InteractionApplication) getApplicationContext()).getInteractionContext();

        //sysplaceContext.updateViewContextAll(this);

        clearImage(mImageView);

        //TODO: VERY HACKY! works only for swipe

        mSwipeDetector = (SwipeDetector) sysplaceContext.getGestureManager().getGestureDetectorFor(LifecycleEvent.CONNECT);
        mSwipeDetector.addSwipeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSwipeDetector.removeSwipeListener(this);

    }

    public void startBluetoothActivity(View view) {
        startActivity(new Intent(this, BluetoothActivity.class));
    }

    public void startWifiDirectActivity(View view) {
        startActivity(new Intent(this, WifiDirectActivity.class));
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        ((InteractionApplication) getApplicationContext()).getInteractionContext().updateSelection(new Packet(s.toString()));
    }

    @Override
    public void receive(Packet packet) {
        if (packet.getType().equals(PacketType.Image)) {
            Bitmap image = ((ImagePacket) packet).getImage().getImage();
            receiveAnimation.play(image);
        }

        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(700);
        Toast.makeText(this, packet.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean accept(PacketType type) {
        return type.equals(PacketType.PlainStringPacket) || type.equals(PacketType.Image);
    }

    @Override
    public View getInteractionView() {
        return findViewById(R.id.layout_interaction);
    }

    @Override
    public Point getDisplaySize() {
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(displaySize);
        return displaySize;
    }

    public void clearImage(View view) {
        mImageView.setImageResource(0);
        ((InteractionApplication) getApplicationContext()).getInteractionContext().updateSelection(new Packet("Nothing selected"));

    }

    public void loadImage(View view) {
        mImageView.setImageResource(R.drawable.cats);
        BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getBitmap());
        SerializableImage image = new SerializableImage(bitmap);

        ((InteractionApplication) getApplicationContext()).getInteractionContext().updateSelection(new ImagePacket(image));
    }

    public void playReceiveAnim(View view) {
        loadImage(view);
        BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getBitmap());
        receiveAnimation.play(bitmap);
    }

    @Override
    public void onSwipeDetected(SwipeDetector swipeDetector, SwipeEvent event) {
        ValueAnimator grayAwayAnimation = ValueAnimator.ofInt(150, 0);
        grayAwayAnimation.setDuration(2000);
        grayAwayAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mImageViewCopy.setColorFilter(Color.argb((int) valueAnimator.getAnimatedValue(), 200, 200, 200));
            }
        });
        grayAwayAnimation.start();
        sendAnimation.play();
    }

    @Override
    public void onSwiping(SwipeDetector swipeDetector, TouchPoint touchPoint) {
        sendAnimation.onSwiping(touchPoint);
    }

    @Override
    public void onSwipeStart(SwipeDetector swipeDetector, TouchPoint touchPoint, View view) {
        mImageViewCopy.setVisibility(View.VISIBLE);
        mImageViewCopy.setColorFilter(Color.argb(150, 200, 200, 200));
        sendAnimation.onSwipeStart(touchPoint);
    }

    @Override
    public void onSwipeEnd(SwipeDetector swipeDetector, TouchPoint touchPoint) {
        sendAnimation.onSwipeEnd(touchPoint);
    }
}