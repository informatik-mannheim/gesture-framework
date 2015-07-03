package hs_mannheim.pattern_interaction_model.model;

public interface IConnection {
    void connect(String address);
    boolean isConnected();
    void transfer(Packet data);
    void register(IConnectionListener listener);
}
