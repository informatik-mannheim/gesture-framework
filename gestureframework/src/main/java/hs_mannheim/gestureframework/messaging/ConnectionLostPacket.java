package hs_mannheim.gestureframework.messaging;

public class ConnectionLostPacket extends Packet {
    public ConnectionLostPacket() {
        super(PacketType.ConnectionLost, "Connection lost");
    }
}
