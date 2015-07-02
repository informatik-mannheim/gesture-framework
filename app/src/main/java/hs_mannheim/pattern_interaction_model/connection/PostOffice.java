package hs_mannheim.pattern_interaction_model.connection;

import android.database.Observable;
import android.util.Log;

import hs_mannheim.pattern_interaction_model.modeltemp.IConnection;
import hs_mannheim.pattern_interaction_model.modeltemp.IPacketReceiver;
import hs_mannheim.pattern_interaction_model.modeltemp.IPostOffice;
import hs_mannheim.pattern_interaction_model.modeltemp.Packet;

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
    public void send(Packet packet) {
        mConnection.transfer(packet);
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
            if (receiver.accept(packet.get_type())) {
                receiver.receive(packet);
            }
        }
    }

    @Override
    public void onConnectionLost() {
        receive(new Packet("DATA", "Connection lost"));
    }

    @Override
    public void onConnectionEstablished() {
        receive(new Packet("DATA", "Connection established"));
    }

    @Override
    public void onDataReceived(Packet packet) {
        Log.d("[PostOffice]", "Received packet: " + packet.toString());
        receive(packet);
    }
}
