package hs_mannheim.pattern_interaction_model.Model;

public interface IConnection {
    boolean isConnected();
    void transfer(String data, OnTransferDoneListener listener);
}
