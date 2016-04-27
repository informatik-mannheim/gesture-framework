package hs_mannheim.gestureframework.model;

import hs_mannheim.gestureframework.connection.bluetooth.ConnectionInfo;

public interface IConnection {
    void connect(String address);
    void connect(ConnectionInfo connectionInfo);
    boolean isConnected();
    void transfer(Packet data);
    void register(IConnectionListener listener);
    void disconnect();
}
