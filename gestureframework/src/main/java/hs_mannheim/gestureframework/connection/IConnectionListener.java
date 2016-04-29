package hs_mannheim.gestureframework.connection;

import hs_mannheim.gestureframework.messaging.Packet;

public interface IConnectionListener {
    void onConnectionLost();
    void onConnectionEstablished();
    void onDataReceived(Packet data);
}