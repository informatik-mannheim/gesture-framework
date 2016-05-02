package hs_mannheim.gestureframework.messaging;

import java.io.Serializable;

public class ConnectionEstablishedPacket extends Packet implements Serializable {
    public ConnectionEstablishedPacket() {
        super(PacketType.ConnectionEstablished, "Connection established");
    }
}
