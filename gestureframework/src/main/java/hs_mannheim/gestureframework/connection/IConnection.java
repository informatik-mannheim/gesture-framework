package hs_mannheim.gestureframework.connection;

import hs_mannheim.gestureframework.connection.bluetooth.ConnectionInfo;
import hs_mannheim.gestureframework.messaging.Packet;

@SuppressWarnings("unused")
public interface IConnection {
    void connect(String address);
    void connect(ConnectionInfo connectionInfo);
    boolean isConnected();
    void transfer(Packet data);
    void register(IConnectionListener listener);
    void unregister(IConnectionListener listener);
    void disconnect();
}
