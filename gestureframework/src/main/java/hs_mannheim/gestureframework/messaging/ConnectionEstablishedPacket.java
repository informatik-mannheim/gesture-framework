package hs_mannheim.gestureframework.messaging;

public class ConnectionEstablishedPacket extends Packet {
    public ConnectionEstablishedPacket() {
        super(PacketType.ConnectionEstablished, "Connection established");
    }
}
