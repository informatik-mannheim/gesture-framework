package hs_mannheim.pattern_interaction_model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import hs_mannheim.gestureframework.animation.GestureAnimation;
import hs_mannheim.gestureframework.animation.MovementSpring;
import hs_mannheim.gestureframework.animation.PostcardFlipAnimationReceive;
import hs_mannheim.gestureframework.animation.PostcardFlipAnimationSend;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;
import hs_mannheim.gestureframework.model.IPacketReceiver;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.ImagePacket;
import hs_mannheim.gestureframework.model.InteractionContext;
import hs_mannheim.gestureframework.model.Packet;
import hs_mannheim.gestureframework.model.PacketType;
import hs_mannheim.gestureframework.model.SerializableImage;


public class InteractionActivity extends ActionBarActivity implements SwipeDetector.SwipeEventListener, IPacketReceiver, TextWatcher, IViewContext {

    public final static String MODEL = Build.MODEL;
    private static final String TAG = "[InteractionActivity]";
    private ImageView mImageView;
    private GestureAnimation sendAnimation, receiveAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction);

        TextView header = (TextView) findViewById(R.id.tv_header_interaction);
        ((EditText) findViewById(R.id.etMessage)).addTextChangedListener(this);

        mImageView = (ImageView) findViewById(R.id.ivPic);

        ///////////////////////////// TODO: Do this somewhere else
        this.sendAnimation = new PostcardFlipAnimationSend(this, mImageView);
        this.receiveAnimation = new PostcardFlipAnimationReceive(this, mImageView);
        /////////////////////////////

        header.setText(MODEL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        InteractionContext interactionContext = ((InteractionApplication) getApplicationContext()).getInteractionContext();
        interactionContext.getPostOffice().register(this);
        interactionContext.updateViewContext(this);

        //TODO: VERY HACKY! works only for swipe
        SwipeDetector mSwipeDetector = (SwipeDetector) interactionContext.getGestureDetector();
        mSwipeDetector.addSwipeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((InteractionApplication) getApplicationContext()).getInteractionContext().getPostOffice().unregister(this);
    }

    @Override
    public void onSwipeDetected(SwipeEvent event) {
        Toast.makeText(this, event.toString(), Toast.LENGTH_SHORT).show();
        sendAnimation.play();
    }

    @Override
    public void onSwiping(TouchPoint touchPoint) {
        Log.d(TAG, touchPoint.toString());
        sendAnimation.onSwiping(touchPoint);
    }

    @Override
    public void onSwipeStart(TouchPoint touchPoint) {
        sendAnimation.onSwipeStart(touchPoint);
    }

    @Override
    public void onSwipeEnd(TouchPoint touchPoint) {
        sendAnimation.onSwipeEnd(touchPoint);
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
            mImageView.setImageBitmap(image);

            receiveAnimation.play();
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

    private class CircleOutlineProvider extends ViewOutlineProvider {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, view.getWidth(), view.getHeight());
        }
    }
}