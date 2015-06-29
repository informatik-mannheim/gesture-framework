package hs_mannheim.pattern_interaction_model.Model;

public interface ConnectionListener {
    void onConnectionLost();
    void onConnectionEstablished();
    void onDataReceived(String data);
}