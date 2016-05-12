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

/*
 * Copyright (C) 2015, 2016 Horst Schneider
 * This file is part of Sysplace Gesture Framework
 *
 * Sysplace Gesture Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sysplace Gesture Framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 */

package hs_mannheim.gestureframework.model;

import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hs_mannheim.gestureframework.gesture.GestureDetector;
import hs_mannheim.gestureframework.gesture.VoidGestureDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;
import hs_mannheim.gestureframework.messaging.IPacketReceiver;
import hs_mannheim.gestureframework.messaging.Packet;

public class GestureManager implements GestureDetector.GestureEventListener,
        SwipeDetector.SwipeEventListener, IPacketReceiver {

    private final Map<LifecycleEvent, GestureDetector> mDetectors = new HashMap<>();
    private final List<ILifecycleListener> mLifeCycleEventListeners = new ArrayList<>();
    private boolean mConnected = false;

    public GestureManager() {
        setGestureDetector(LifecycleEvent.CONNECT, new VoidGestureDetector());
        setGestureDetector(LifecycleEvent.SELECT, new VoidGestureDetector());
        setGestureDetector(LifecycleEvent.TRANSFER, new VoidGestureDetector());
        setGestureDetector(LifecycleEvent.DISCONNECT, new VoidGestureDetector());
    }

    /**
     * Set a {@link IViewContext} for a specific LifecycleEvent registered.
     * @param lifecycleEvent The {@link LifecycleEvent} to set the {@link IViewContext} for.
     * @param viewContext The {@link IViewContext} to set.
     */
    public void setViewContext(LifecycleEvent lifecycleEvent, IViewContext viewContext) {
        mDetectors.get(lifecycleEvent).setViewContext(viewContext);
    }

    /**
     * Set the same {@link IViewContext} on every GestureDetector registered.
     * @param viewContext The {@link IViewContext} to set.
     */
    public void setViewContextAll(IViewContext viewContext) {
        for (GestureDetector detector : mDetectors.values()) {
            detector.setViewContext(viewContext);
        }
    }

    /**
     * Returns the currently registered {@link GestureDetector} for a given {@link LifecycleEvent}.
     *
     * @param lifecycleEvent The queried {@link LifecycleEvent}
     * @return The desired {@link GestureDetector}. Might be a {@link VoidGestureDetector}!
     */
    @SuppressWarnings("unused")
    public GestureDetector getGestureDetectorFor(LifecycleEvent lifecycleEvent) {
        return mDetectors.get(lifecycleEvent);
    }

    /**
     * Registers a new {@link GestureDetector} for a specific {@link LifecycleEvent}.
     *
     * @param lifecycleEvent  The context (Connect, Select, Transfer, Disconnect) for which to
     *                        register a new listener (if there was an old one, it will be replaced)
     * @param gestureDetector The GestureDetector to register to the given GestureContext
     */
    public void setGestureDetector(LifecycleEvent lifecycleEvent, GestureDetector gestureDetector) {
        if (gestureDetector == null) {
            gestureDetector = new VoidGestureDetector();
        }

        gestureDetector.registerGestureEventListener(this);
        mDetectors.put(lifecycleEvent, gestureDetector);
    }

    /**
     * Registers a listener for all LifecycleEvents.
     * TODO: make this Threadsafe
     *
     * @param lifecycleListener the listener to register
     */
    public void registerLifecycleListener(ILifecycleListener lifecycleListener) {
        mLifeCycleEventListeners.add(lifecycleListener);
    }

    /**
     * Unregisters a listener for all LifecycleEvents.
     * TODO: make this Threadsafe
     *
     * @param lifecycleListener the listener to register
     */
    @SuppressWarnings("unused")
    public void unregisterLifecycleListener(ILifecycleListener lifecycleListener) {
        if (mLifeCycleEventListeners.contains(lifecycleListener)) {
            mLifeCycleEventListeners.remove(lifecycleListener);
        }
    }

    private LifecycleEvent getLifecycleEventFor(GestureDetector detector) {
        for (Map.Entry<LifecycleEvent, GestureDetector> entry : mDetectors.entrySet()) {
            if (entry.getValue().equals(detector)) {
                return entry.getKey();
            }
        }

        return LifecycleEvent.NONE;
    }

    @Override
    public void onGestureDetected(GestureDetector gestureDetector) {
        for (ILifecycleListener gestureListener : mLifeCycleEventListeners) {
            switch (getLifecycleEventFor(gestureDetector)) {
                case CONNECT:
                    if(mConnected) {
                        return;
                    }
                    gestureListener.onConnect();
                    break;
                case SELECT:
                    gestureListener.onSelect();
                    break;
                case TRANSFER:
                    if(!mConnected) {
                        return;
                    }
                    gestureListener.onTransfer();
                    break;
                case DISCONNECT:
                    if(!mConnected) {
                        return;
                    }
                    gestureListener.onDisconnect();
                    break;
                default:
                    break;
            }
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

    @Override
    public void receive(Packet packet) {
        mConnected = packet.getType() == Packet.PacketType.ConnectionEstablished;
    }

    @Override
    public boolean accept(Packet.PacketType type) {
        return type == Packet.PacketType.ConnectionEstablished || type == Packet.PacketType.ConnectionLost;
    }
}