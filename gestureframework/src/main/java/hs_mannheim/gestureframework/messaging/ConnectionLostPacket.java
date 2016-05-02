package hs_mannheim.gestureframework.messaging;

import java.io.Serializable;

public class ConnectionLostPacket extends Packet implements Serializable {
    public ConnectionLostPacket() {
        super(PacketType.ConnectionLost, "Connection lost");
    }
}
