/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *    Hochschule Mannheim University of Applied Sciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package hs_mannheim.gestureframework;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;

import hs_mannheim.gestureframework.connection.IConnection;
import hs_mannheim.gestureframework.connection.bluetooth.BluetoothChannel;
import hs_mannheim.gestureframework.connection.wifidirect.WifiDirectChannel;
import hs_mannheim.gestureframework.gesture.GestureDetector;
import hs_mannheim.gestureframework.gesture.GestureDetectorBuilder;
import hs_mannheim.gestureframework.gesture.bump.BumpDetector;
import hs_mannheim.gestureframework.gesture.bump.SyncBumpDetector;
import hs_mannheim.gestureframework.gesture.doubletap.DoubleTapDetector;
import hs_mannheim.gestureframework.gesture.shake.ShakeDetector;
import hs_mannheim.gestureframework.gesture.stitch.StitchDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.messaging.IPacketReceiver;
import hs_mannheim.gestureframework.messaging.PostOffice;
import hs_mannheim.gestureframework.model.GestureManager;
import hs_mannheim.gestureframework.model.ILifecycleListener;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.InteractionApplication;
import hs_mannheim.gestureframework.model.LifecycleEvent;
import hs_mannheim.gestureframework.model.Selection;
import hs_mannheim.gestureframework.model.SysplaceContext;

public class ConfigurationBuilder {

    private final Context mContext;
    private IViewContext mViewContext;
    private IConnection mChannel;
    private Selection mSelection;
    private GestureManager mGestureManager;
    private GestureDetectorBuilder mBuilder;
    private PostOffice mPostOffice;
    private ILifecycleListener mLifecycleListener;
    private IPacketReceiver mPacketReceiver;

    /**
     * Helps bootstrapping and registering a {@link SysplaceContext} to the current
     * {@link InteractionApplication}
     *
     * @param context     The context of the current application.
     *                    Needs to be an {@link InteractionApplication}.
     * @param viewContext An {@link IViewContext} that is needed to register for View-related
     *                    events such as {@link android.view.MotionEvent}.
     */
    public ConfigurationBuilder(Context context, IViewContext viewContext) {
        mContext = context;
        mViewContext = viewContext;
        mSelection = Selection.Empty;
        mGestureManager = new GestureManager();
    }

    /**
     * Choose Bluetooth for connecting devices.
     *
     * @return The {@link ConfigurationBuilder} instance.
     */
    public ConfigurationBuilder withBluetooth() {
        mChannel = new BluetoothChannel(BluetoothAdapter.getDefaultAdapter());
        mPostOffice = new PostOffice(mChannel);
        mBuilder = new GestureDetectorBuilder(mPostOffice, mViewContext, mContext);
        return this;
    }

    /**
     * Choose Wifi P2P for connecting devices.
     *
     * @return The {@link ConfigurationBuilder} instance.
     */
    @SuppressWarnings("unused")
    public ConfigurationBuilder withWifiDirect() {
        WifiP2pManager wifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel channel = wifiP2pManager.initialize(mContext, mContext.getMainLooper(), null);
        mChannel = new WifiDirectChannel(wifiP2pManager, channel, mContext);
        mPostOffice = new PostOffice(mChannel);
        mBuilder = new GestureDetectorBuilder(mPostOffice, mViewContext, mContext);
        return this;
    }

    /**
     * Specify which GestureDetector triggers the {@link LifecycleEvent} CONNECT.
     *
     * @param gestureDetector The {@link GestureDetector}.
     * @return The {@link ConfigurationBuilder} instance.
     */
    public ConfigurationBuilder toConnect(GestureDetector gestureDetector) {
        mGestureManager.setGestureDetector(LifecycleEvent.CONNECT, gestureDetector);
        return this;
    }

    /**
     * Specify which GestureDetector triggers the {@link LifecycleEvent} SELECT.
     *
     * @param gestureDetector The {@link GestureDetector}.
     * @return The {@link ConfigurationBuilder} instance.
     */
    public ConfigurationBuilder toSelect(GestureDetector gestureDetector) {
        mGestureManager.setGestureDetector(LifecycleEvent.SELECT, gestureDetector);
        return this;
    }

    /**
     * Specify which GestureDetector triggers the {@link LifecycleEvent} TRANSFER.
     *
     * @param gestureDetector The {@link GestureDetector}.
     * @return The {@link ConfigurationBuilder} instance.
     */
    public ConfigurationBuilder toTransfer(GestureDetector gestureDetector) {
        mGestureManager.setGestureDetector(LifecycleEvent.TRANSFER, gestureDetector);
        return this;
    }

    /**
     * Specify which GestureDetector triggers the {@link LifecycleEvent} DISCONNECT.
     *
     * @param gestureDetector The {@link GestureDetector}.
     * @return The {@link ConfigurationBuilder} instance.
     */
    public ConfigurationBuilder toDisconnect(GestureDetector gestureDetector) {
        mGestureManager.setGestureDetector(LifecycleEvent.DISCONNECT, gestureDetector);
        return this;
    }

    /**
     * Make an initial {@link Selection} that will be transferred on TRANSFER.
     *
     * @param selection The {@link Selection} to be transferred on TRANSFER.
     * @return The {@link ConfigurationBuilder} instance.
     */
    public ConfigurationBuilder select(Selection selection) {
        mSelection = selection;
        return this;
    }

    public ConfigurationBuilder registerForLifecycleEvents(ILifecycleListener lifecycleListener) {
        mLifecycleListener = lifecycleListener;
        return this;
    }

    public ConfigurationBuilder registerPacketReceiver(IPacketReceiver packetReceiver) {
        mPacketReceiver = packetReceiver;
        return this;
    }

    /**
     * Bootstrap the {@link SysplaceContext} and register it to the {@link InteractionApplication}.
     */
    public void buildAndRegister() {
        SysplaceContext sysplaceContext = new SysplaceContext(mGestureManager, mSelection, mChannel, mPostOffice);
        if (mLifecycleListener != null) {
            sysplaceContext.registerForLifecycleEvents(mLifecycleListener);
        }
        if (mPacketReceiver != null) {
            sysplaceContext.registerPacketReceiver(mPacketReceiver);
        }

        InteractionApplication interactionApplication = (InteractionApplication) mContext;
        interactionApplication.setInteractionContext(sysplaceContext);
    }

    /**
     * Helper to quickly get a left-right {@link SwipeDetector}.
     *
     * @return A {@link SwipeDetector} instance.
     */
    public SwipeDetector swipeLeftRight() {
        return mBuilder.createSwipeLeftRightDetector();
    }

    /**
     * Helper to quickly get an up-down {@link SwipeDetector}.
     *
     * @return A {@link SwipeDetector} instance.
     */
    public SwipeDetector swipeUpDown() {
        return mBuilder.createSwipeUpDownDetector();
    }

    /**
     * Helper to quickly get a {@link BumpDetector}.
     *
     * @return A {@link BumpDetector} instance.
     */
    public BumpDetector bump() {
        return mBuilder.createBumpDetector();
    }

    /**
     * Helper to quickly get a {@link StitchDetector}.
     *
     * @return A {@link StitchDetector} instance.
     */
    @SuppressWarnings("unused")
    public StitchDetector stitch() {
        return mBuilder.createStitchDetector();
    }

    public SyncBumpDetector syncBump() {
        BumpDetector bumpDetector = mBuilder.createBumpDetector();
        return new SyncBumpDetector(mPostOffice, mViewContext, bumpDetector);
    }

    /**
     * Helper to quickly get a {@link ShakeDetector}.
     *
     * @return A {@link ShakeDetector} instance.
     */
    @SuppressWarnings("unused")
    public ShakeDetector shake() {
        return mBuilder.createShakeDetector();
    }

    /**
     * Helper to quickly get a {@link DoubleTapDetector}.
     *
     * @return A {@link DoubleTapDetector} instance.
     */
    public DoubleTapDetector doubleTap() {
        return mBuilder.createDoubleTapDetector();
    }
}
