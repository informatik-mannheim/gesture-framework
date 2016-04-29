package hs_mannheim.gestureframework.gesture.stitch;

import java.io.Serializable;

import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.messaging.Packet;

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
