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

package hs_mannheim.gestureframework.messaging;

import android.database.Observable;
import hs_mannheim.gestureframework.connection.IConnection;

/**
 * Manages all packet traffic in an interaction context. It can send packages over a connection or
 * receive packages from the connection and distribute them across the InteractionContext.
 */
public class PostOffice extends Observable<IPacketReceiver> implements IPostOffice {

    private IConnection mConnection;

    public PostOffice(IConnection connection) {
        mConnection = connection;
        mConnection.register(this);
    }

    /**
     * Sends a packet through the connection.
     * @param packet to transfer
     */
    public void send(final Packet packet) {
        // Run the transfer in a new thread so it won't block the application.
        new Thread(new Runnable() {
            @Override
            public void run() {
                mConnection.transfer(packet);
            }
        }).start();
    }

    /**
     * Registers a packet receiver for certain types of packets.
     * @param receiver to register
     */
    @Override
    public void register(IPacketReceiver receiver) {
        this.registerObserver(receiver);
    }

    /**
     * Unregisters a packet receiver from receiving any packets.
     * @param receiver to unregister
     */
    @Override
    public void unregister(IPacketReceiver receiver) {
        this.unregisterObserver(receiver);
    }

    /**
     * Receives all incoming packets and distributes them to the corresponding receivers.
     * @param packet to be distributes
     */
    @Override
    public void receive(Packet packet) {
        for (IPacketReceiver receiver : this.mObservers) {
            if (receiver.accept(packet.getType())) {
                receiver.receive(packet);
            }
        }
    }

    /**
     * If the {@link IConnection} tells us that the connection was lost, propagate it on the bus by
     * sending a {@link ConnectionLostPacket}.
     */
    @Override
    public void onConnectionLost() {
        receive(new ConnectionLostPacket());
    }

    /**
     * If the {@link IConnection} tells us that the connection was established, propagate it on
     * the bus by sending a {@link ConnectionEstablishedPacket}.
     */
    @Override
    public void onConnectionEstablished() {
        receive(new ConnectionEstablishedPacket());
    }

    /**
     * If the {@link IConnection} has any {@link Packet} for us, propagate it on the bus.
     * @param packet The received {@link Packet}.
     */
    @Override
    public void onDataReceived(Packet packet) {
        receive(packet);
    }
}
