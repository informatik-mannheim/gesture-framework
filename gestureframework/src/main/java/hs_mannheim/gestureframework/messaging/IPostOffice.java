package hs_mannheim.gestureframework.messaging;

import hs_mannheim.gestureframework.connection.IConnectionListener;

public interface IPostOffice extends IConnectionListener {
    void send(Packet packet);
    void register(IPacketReceiver receiver);
    void unregister(IPacketReceiver receiver);
    void receive(Packet packet);
}
