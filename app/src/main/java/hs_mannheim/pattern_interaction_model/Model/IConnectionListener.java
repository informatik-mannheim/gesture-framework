package hs_mannheim.pattern_interaction_model.model;

public interface IConnectionListener {
    void onConnectionLost();
    void onConnectionEstablished();
    void onDataReceived(Packet data);
}