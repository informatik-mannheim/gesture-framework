package hs_mannheim.gestureframework.model;

public interface IConnection {
    void connect(String address);
    boolean isConnected();
    void transfer(Packet data);
    void register(IConnectionListener listener);
}
