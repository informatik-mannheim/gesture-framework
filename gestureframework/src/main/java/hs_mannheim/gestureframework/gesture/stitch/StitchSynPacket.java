package hs_mannheim.gestureframework.gesture.stitch;

import java.io.Serializable;

import hs_mannheim.gestureframework.messaging.Packet;

public class StitchSynPacket extends Packet implements Serializable {

    public StitchSynPacket() {
        super(PacketType.StitchSyn, "Stitch SYN");
    }
}
