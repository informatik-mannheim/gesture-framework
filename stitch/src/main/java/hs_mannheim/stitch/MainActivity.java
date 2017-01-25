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

package hs_mannheim.stitch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import hs_mannheim.gestureframework.ConfigurationBuilder;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;
import hs_mannheim.gestureframework.messaging.IPacketReceiver;
import hs_mannheim.gestureframework.messaging.Packet;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.InteractionApplication;
import hs_mannheim.gestureframework.model.Selection;
import hs_mannheim.gestureframework.model.SysplaceContext;
import hs_mannheim.gestureframework.model.ViewWrapper;

public class MainActivity extends AppCompatActivity implements IViewContext, IPacketReceiver, SwipeDetector.SwipeEventListener {
    private static final String TAG = "[Main Activity]";

    private SysplaceContext mSysplaceContext;
    private boolean mIsConnectionEstablished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "App starts");

        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getApplicationContext();
        ConfigurationBuilder builder = new ConfigurationBuilder(getApplicationContext(), this);
        builder
                .withBluetooth()
                .toConnect(builder.swipeLeftRight())
                .toSelect(builder.doubleTap())
                .toTransfer(builder.stitch())
                .toDisconnect(builder.syncBump())
                .select(Selection.Empty)
                .registerForLifecycleEvents(new ToastLifecycleListener(this))
                .registerPacketReceiver(this)
                .buildAndRegister();

        mSysplaceContext = ((InteractionApplication) getApplicationContext()).getSysplaceContext();
        mSysplaceContext.activate(this);
        mSysplaceContext.registerForSwipeEvents(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
        mSysplaceContext.applicationPaused();
    }

    public void ping(View view) {
        mSysplaceContext.send(new Packet("Ping!"));
    }

    public void switchToConnectedActivity(SwipeEvent.Orientation orientation) {
        Intent intent =new Intent(this, ConnectedActivity.class);
        intent.putExtra("orientation", "" + orientation);
        startActivity(intent);
    }

    public void disconnect(View view) {
        mSysplaceContext.disconnect();
    }

    // IViewContext Stuff

    @Override
    public ViewWrapper getViewWrapper() {
        return ViewWrapper.wrap(findViewById(R.id.layout_main));
    }

    @Override
    public Point getDisplaySize() {
        //TODO: this only works properly in portrait mode. We have to subtract everything that
        // does not belong to the App (such as the StatusBar)
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new Point(metrics.widthPixels, metrics.heightPixels);
    }

    // IPacketReceiver Stuff
    @Override
    public boolean accept(Packet.PacketType type) {
        return true;
    }

    @Override
    public void receive(Packet packet) {
        switch (packet.getType()) {
            case ConnectionEstablished:
                mIsConnectionEstablished = true;
                switchToConnectedActivity(SwipeEvent.Orientation.EAST);
            case ConnectionLost:
                ((Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(350);
                break;
            case PlainString:
                Toast.makeText(this, packet.getMessage(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSwipeDetected(SwipeDetector swipeDetector, SwipeEvent event) {
    }

    @Override
    public void onSwiping(SwipeDetector swipeDetector, TouchPoint touchPoint) {

    }

    @Override
    public void onSwipeStart(SwipeDetector swipeDetector, TouchPoint touchPoint, View view) {

    }

    @Override
    public void onSwipeEnd(SwipeDetector swipeDetector, TouchPoint touchPoint) {

    }

    public void connect(View view) {
        mSysplaceContext.onConnect();
    }
}