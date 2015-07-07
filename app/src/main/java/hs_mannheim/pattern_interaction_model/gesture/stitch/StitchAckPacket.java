package hs_mannheim.pattern_interaction_model.gesture.stitch;

import hs_mannheim.pattern_interaction_model.model.Packet;
import hs_mannheim.pattern_interaction_model.model.PacketType;

public class StitchAckPacket extends Packet {
    public StitchAckPacket() {
        super(PacketType.StitchAck, "Stitch ACK");
    }
}
