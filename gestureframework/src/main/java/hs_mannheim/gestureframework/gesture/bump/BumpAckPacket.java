package hs_mannheim.gestureframework.gesture.bump;

import java.io.Serializable;

import hs_mannheim.gestureframework.messaging.Packet;

public class BumpAckPacket extends Packet implements Serializable {
    public BumpAckPacket() {
        super(PacketType.BumpAck, "Bump Ack");
    }
}
