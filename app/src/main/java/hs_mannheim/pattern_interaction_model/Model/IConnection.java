package hs_mannheim.pattern_interaction_model.model;

public interface IConnection {
    boolean isConnected();
    void transfer(Payload data);
    void register(ConnectionListener listener);
    void connect(String address);
}
