package hs_mannheim.gestureframework.gesture.bump;

import java.io.Serializable;

import hs_mannheim.gestureframework.messaging.Packet;

public class BumpSynPacket extends Packet implements Serializable {
    public BumpSynPacket() {
        super(PacketType.BumpSyn, "Bump Syn");
    }
}
