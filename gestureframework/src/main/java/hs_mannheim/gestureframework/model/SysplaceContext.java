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

package hs_mannheim.gestureframework.model;

import android.content.Intent;

import hs_mannheim.gestureframework.connection.BluetoothPairingService;
import hs_mannheim.gestureframework.connection.IConnection;
import hs_mannheim.gestureframework.connection.bluetooth.ConnectionInfo;
import hs_mannheim.gestureframework.messaging.IPacketReceiver;
import hs_mannheim.gestureframework.messaging.IPostOffice;
import hs_mannheim.gestureframework.messaging.Packet;

/**
 * Manages the whole Lifecycle of a gesture enabled Application. It references an
 * {@link IPostOffice} for messaging, connects an {@link IConnection} to it and also registers
 * {@link android.view.GestureDetector} instances to {@link LifecycleEvent} events trough a
 * {@link GestureManager}. It also knows about the current {@link Selection} and enforces certain
 * Lifecycle rules (e.g. only send non-empty Selection, start device discovery on CONNECT etc).
 */
public class SysplaceContext implements ILifecycleListener, ISysplaceContext {

    private final GestureManager mGestureManager;
    private final IConnection mConnection;
    private final IPostOffice mPostOffice;
    private Selection mSelection;
    private InteractionApplication mApplication;

    /**
     * The InteractionContext is the one and only global object to manage all Sysplace related
     * Gestures, corresponding events, states etc.
     *
     * @param gestureManager The GestureManager for registerign GestureDetectors to LifecycleEvents
     * @param selection The selected data to be transferred
     * @param connection The underlying connection for Peer-To-Peer communication
     * @param postOffice The broker for messages between devices
     */
    public SysplaceContext(GestureManager gestureManager,
                           Selection selection,
                           IConnection connection,
                           IPostOffice postOffice) {
        mGestureManager = gestureManager;
        mConnection = connection;
        mPostOffice = postOffice; /* only PostOffice talks to the connection */

        mGestureManager.registerLifecycleListener(this);
        mPostOffice.register(mGestureManager);

        select(selection);
    }

    @SuppressWarnings("unused")
    public void updateViewContextAll(IViewContext viewContext) {
        mGestureManager.setViewContextAll(viewContext);
    }

    @SuppressWarnings("unused")
    public void updateViewContext(LifecycleEvent lifecycleEvent, IViewContext viewContext) {
        mGestureManager.setViewContext(lifecycleEvent, viewContext);
    }

    @Override
    public void registerPacketReceiver(IPacketReceiver packetReceiver) {
        mPostOffice.register(packetReceiver);
    }

    @Override
    public void unregisterPacketReceiver(IPacketReceiver packetReceiver) {
        mPostOffice.unregister(packetReceiver);
    }

    @Override
    public void onConnect() {
        mApplication.startService(new Intent(mApplication, BluetoothPairingService.class));
    }

    @Override
    public void onSelect() {

    }

    @Override
    public void onTransfer() {
        if(mSelection != Selection.Empty) {
            mPostOffice.send(mSelection.getData());
        }
    }

    @Override
    public void onDisconnect() {
        mConnection.disconnect();
    }

    @Override
    public void select(Selection selection) {
        mSelection = selection;
    }

    /**
     * Send packet through an established connection, if any.
     *
     * @param packet The packet to send
     */
    @Override
    public void send(Packet packet) {
        mPostOffice.send(packet);
    }


    /**
     * Register a LifecycleListener to be notified of all LifecycleEvents happening in the
     * {@link SysplaceContext}.
     */
    @Override
    public void registerForLifecycleEvents(ILifecycleListener listener) {
        mGestureManager.registerLifecycleListener(listener);
    }

    public void setApplication(InteractionApplication application) {
        mApplication = application;
    }

    public void applicationPaused() {
        mApplication.toggleName(false);
    }

    public void applicationResumed() {
        mApplication.toggleName(true);
    }

    public void connect(ConnectionInfo info) {
        mConnection.connect(info);
    }

    public void disconnect() {
        mConnection.disconnect();
    }
}
