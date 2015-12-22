package hs_mannheim.gestureframework.gesture.stitch;

import hs_mannheim.gestureframework.model.Packet;
import hs_mannheim.gestureframework.model.PacketType;

public class StitchAckPacket extends Packet {
    public StitchAckPacket() {
        super(PacketType.StitchAck, "Stitch ACK");
    }
}
