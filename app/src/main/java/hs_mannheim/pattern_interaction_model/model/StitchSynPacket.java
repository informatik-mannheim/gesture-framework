package hs_mannheim.pattern_interaction_model.model;

import java.io.Serializable;

import hs_mannheim.pattern_interaction_model.gesture.stitch.StitchEvent;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeEvent;

public class StitchSynPacket extends Packet implements Serializable {

    private StitchEvent.Bounding mBounding;
    private SwipeEvent.Orientation mOrientation;

    public StitchSynPacket(StitchEvent.Bounding bounding, SwipeEvent.Orientation orientation) {
        super(PacketType.StitchSyn, "Stitch SYN");
        mBounding = bounding;
        mOrientation = orientation;
    }

    public StitchEvent.Bounding getBounding() {
        return mBounding;
    }

    public SwipeEvent.Orientation getOrientation() {
        return mOrientation;
    }
}
