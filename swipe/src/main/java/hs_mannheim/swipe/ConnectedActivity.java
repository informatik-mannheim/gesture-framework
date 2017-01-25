/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *     Hochschule Mannheim University of Applied Sciences
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package hs_mannheim.swipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

import hs_mannheim.gestureframework.animation.GestureAnimator;
import hs_mannheim.gestureframework.animation.GestureTransitionInfo;
import hs_mannheim.gestureframework.animation.TransitionAnimator;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;
import hs_mannheim.gestureframework.messaging.IPacketReceiver;
import hs_mannheim.gestureframework.messaging.ImagePacket;
import hs_mannheim.gestureframework.messaging.Packet;
import hs_mannheim.gestureframework.messaging.SerializableImage;
import hs_mannheim.gestureframework.model.ILifecycleListener;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.InteractionApplication;
import hs_mannheim.gestureframework.model.LifecycleEvent;
import hs_mannheim.gestureframework.model.Selection;
import hs_mannheim.gestureframework.model.SysplaceContext;
import hs_mannheim.gestureframework.model.ViewWrapper;
import hs_mannheim.swipe.animations.AnimationMode;
import hs_mannheim.swipe.animations.AnimationsContainer;
import hs_mannheim.swipe.animations.ElevateAndLeaveAnimator;
import hs_mannheim.swipe.animations.FlipSelectAnimator;
import hs_mannheim.swipe.animations.FlyInAndLowerAnimator;
import hs_mannheim.swipe.animations.OnAnimationStoppedListener;

public class ConnectedActivity extends AppCompatActivity implements IViewContext, ILifecycleListener, IPacketReceiver, SwipeDetector.SwipeEventListener, OnAnimationStoppedListener {

    private static int PICK_IMAGE = 1;
    private SysplaceContext mSysplaceContext;
    private final String TAG = "[ConnectedActivity]";
    private ViewWrapper mViewWrapper;
    private ImageView mImageView;
    private GestureAnimator mReceiveAnimator, mSelectAnimator;
    private TransitionAnimator mSendAnimator;
    private boolean mShouldDragDrop = false;
    private String mConnectionOrientation;
    private AnimationMode mAnimationMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        mViewWrapper = new ViewWrapper(findViewById(R.id.imgView));

        mSysplaceContext = ((InteractionApplication) getApplicationContext()).getSysplaceContext();

        mSysplaceContext.registerForLifecycleEvents(this);
        mSysplaceContext.registerPacketReceiver(this);
        mSysplaceContext.updateViewContext(LifecycleEvent.SELECT, this);
        mSysplaceContext.updateViewContext(LifecycleEvent.TRANSFER, this);

        mSendAnimator = new ElevateAndLeaveAnimator(this, mViewWrapper.getView());
        mSelectAnimator = new FlipSelectAnimator(this, mViewWrapper.getView());
        mReceiveAnimator = new FlyInAndLowerAnimator(this, mViewWrapper.getView());

        mSysplaceContext.registerForSwipeEvents(this);
        AnimationsContainer.getInstance().registerListener(this);

        //TODO: UGLY
        mImageView = (ImageView) mViewWrapper.getView();
        Drawable polaroid = ResourcesCompat.getDrawable(getResources(), R.drawable.polaroid, null);
        RippleDrawable ripplePolaroid = new RippleDrawable(ColorStateList.valueOf(Color.argb(255, 62, 62, 62)), polaroid, null);
        //mImageView.setImageDrawable(ripplePolaroid);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        String orientation = getIntent().getStringExtra("orientation");
        mAnimationMode = AnimationMode.valueOf(getIntent().getStringExtra("animationmode"));
        enterReveal(orientation);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Fires image chooser intent.
     */
    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
    }

    /**
     * Callback on image chooser Activity. Transforms to Bitmap and adds to ImageView
     * //TODO: turn off Toasts for production version
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

                if (data == null) {
                    Toast.makeText(this, "Something went wrong... data returned null", Toast.LENGTH_LONG).show();
                    return;
                }

                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                updatePicture(bitmap);
            } else {
                Toast.makeText(this, "You haven't picked an Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Combines the chosen image file with the polaroid frame and displays the resulting Bitmap
     * in the ImageView
     *
     * @param chosenBitmap
     */
    private void updatePicture(Bitmap chosenBitmap) {
        mSelectAnimator.setReplacementBitmap(chosenBitmap);
        mSelectAnimator.play();
        mSysplaceContext.select(new Selection(new ImagePacket(new SerializableImage(chosenBitmap))));
    }

    @Override
    public ViewWrapper getViewWrapper() {
        return mViewWrapper;
    }

    @Override
    public Point getDisplaySize() {
        //TODO: implement
        return null;
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onSelect() {
        if (mAnimationMode.equals(AnimationMode.NONE) || mAnimationMode.equals(AnimationMode.NO_DD)) {
            mShouldDragDrop = false;
        } else {
            mShouldDragDrop = true;
        }
        chooseImage();
    }

    @Override
    public void onTransfer() { //TODO: Put the checks and logic into the Animators
        if (!mAnimationMode.equals(AnimationMode.NONE) && !mAnimationMode.equals(AnimationMode.NO_OUTGOING)) {
            mSendAnimator.play();
        }
        mSysplaceContext.select(Selection.Empty);
    }

    @Override
    public void onDisconnect() {
        finish();
    }

    @Override
    public void receive(Packet packet) {
        if (packet.getType() == Packet.PacketType.Image) {
            Bitmap receivedBitmap = ((ImagePacket) packet).getImage().getImage();
            mReceiveAnimator.setReplacementBitmap(receivedBitmap);
            if (!mAnimationMode.equals(AnimationMode.NONE) && !mAnimationMode.equals(AnimationMode.NO_INCOMING)) {
                mReceiveAnimator.play();
            }

            mSysplaceContext.select(new Selection(new ImagePacket(new SerializableImage(receivedBitmap))));
        } else if (packet.getType() == Packet.PacketType.ConnectionLost) {
            onDisconnect();
        }
    }

    @Override
    public boolean accept(Packet.PacketType type) {
        return type == Packet.PacketType.Image || type == Packet.PacketType.ConnectionLost;
    }

    @Override
    public void onSwipeDetected(SwipeDetector swipeDetector, SwipeEvent event) {
    }

    @Override
    public void onSwiping(SwipeDetector swipeDetector, TouchPoint touchPoint) {
        if (mShouldDragDrop) {
            mSendAnimator.handleGestureDuring(new GestureTransitionInfo(touchPoint));
        }
    }

    @Override
    public void onSwipeStart(SwipeDetector swipeDetector, TouchPoint touchPoint, View view) {
        if (mShouldDragDrop) {
            mSendAnimator.handleGestureStart(new GestureTransitionInfo(touchPoint));
        }
    }

    @Override
    public void onSwipeEnd(SwipeDetector swipeDetector, TouchPoint touchPoint) {
        if (mShouldDragDrop) {
            mSendAnimator.handleGestureEnd(new GestureTransitionInfo(touchPoint));
        }
    }

    @Override
    public void animationStopped() {
        mShouldDragDrop = false;
    }

    private void enterReveal(String orientation) {
        final View myView = findViewById(R.id.reveal_frame_con);

        // get the center for the clipping circle
        int cx = 0, cy = 0;
        if (orientation.equals("WEST")) {
            cx = 0;
            cy = myView.getMeasuredHeight() / 2;
        } else if (orientation.equals("EAST")) {
            cx = myView.getMeasuredWidth();
            cy = myView.getMeasuredHeight() / 2;
        }
        // get the initial radius for the clipping circle
        int initialRadius = myView.getHeight();

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);
        anim.setDuration(1000);
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }

    public void disconnectWithButton(View v) {
        Log.d(TAG, "disconnectButton clicked");
        mSysplaceContext.disconnect();
        onDisconnect();
    }
}
