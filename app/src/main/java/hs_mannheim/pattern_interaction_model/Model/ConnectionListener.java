package hs_mannheim.pattern_interaction_model.model;

public interface ConnectionListener {
    void onConnectionLost();
    void onConnectionEstablished();
    void onDataReceived(Payload data);
}