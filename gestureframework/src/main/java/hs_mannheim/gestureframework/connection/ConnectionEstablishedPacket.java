package hs_mannheim.gestureframework.connection;

import hs_mannheim.gestureframework.model.Packet;
import hs_mannheim.gestureframework.model.PacketType;

public class ConnectionEstablishedPacket extends Packet {
    public ConnectionEstablishedPacket() {
        super(PacketType.ConnectionEstablished, "Connection established");
    }
}
