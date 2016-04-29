package hs_mannheim.gestureframework.gesture.stitch;

import hs_mannheim.gestureframework.messaging.Packet;

public class StitchAckPacket extends Packet {
    public StitchAckPacket() {
        super(PacketType.StitchAck, "Stitch ACK");
    }
}
