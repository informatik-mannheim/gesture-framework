package hs_mannheim.pattern_interaction_model.modeltemp;

public interface IConnection {
    boolean isConnected();
    void transfer(Packet data);
    void register(IConnectionListener listener);
    void connect(String address);
}
