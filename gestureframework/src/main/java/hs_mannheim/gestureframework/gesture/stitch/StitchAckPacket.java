package hs_mannheim.gestureframework.gesture.stitch;

import java.io.Serializable;

import hs_mannheim.gestureframework.messaging.Packet;

public class StitchAckPacket extends Packet implements Serializable {
    public StitchAckPacket() {
        super(PacketType.StitchAck, "Stitch ACK");
    }
}
