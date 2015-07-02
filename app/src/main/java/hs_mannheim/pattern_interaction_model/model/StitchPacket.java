package hs_mannheim.pattern_interaction_model.model;

import java.io.Serializable;

import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeEvent;

public class StitchPacket extends Packet implements Serializable {

    private SwipeEvent mSwipeEvent;

    public StitchPacket(String message, SwipeEvent event) {
        super(PacketType.Stitch, message);

        mSwipeEvent = event;
    }

    public SwipeEvent getSwipeEvent() {
        return mSwipeEvent;
    }
}
