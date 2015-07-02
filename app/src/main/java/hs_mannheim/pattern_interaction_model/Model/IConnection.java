package hs_mannheim.pattern_interaction_model.model;

public interface IConnection {
    boolean isConnected();
    void transfer(Packet data);
    void register(IConnectionListener listener);
    void connect(String address);
}
