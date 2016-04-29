package hs_mannheim.gestureframework.connection;

import hs_mannheim.gestureframework.model.Packet;
import hs_mannheim.gestureframework.model.PacketType;

public class ConnectionLostPacket extends Packet {
    public ConnectionLostPacket() {
        super(PacketType.ConnectionLost, "Connection lost");
    }
}
