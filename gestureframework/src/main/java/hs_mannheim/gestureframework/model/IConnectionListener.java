package hs_mannheim.gestureframework.model;

public interface IConnectionListener {
    void onConnectionLost();
    void onConnectionEstablished();
    void onDataReceived(Packet data);
}