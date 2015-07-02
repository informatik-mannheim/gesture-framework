package hs_mannheim.pattern_interaction_model.modeltemp;

public interface IConnectionListener {
    void onConnectionLost();
    void onConnectionEstablished();
    void onDataReceived(Packet data);
}