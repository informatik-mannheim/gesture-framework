package hs_mannheim.pattern_interaction_model.gesture.stitch;


import android.graphics.Point;
import android.os.Handler;
import android.util.Log;

import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeConstraint;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDetector;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeEvent;
import hs_mannheim.pattern_interaction_model.model.GestureDetector;
import hs_mannheim.pattern_interaction_model.model.IPacketReceiver;
import hs_mannheim.pattern_interaction_model.model.IPostOffice;
import hs_mannheim.pattern_interaction_model.model.IViewContext;
import hs_mannheim.pattern_interaction_model.model.Packet;
import hs_mannheim.pattern_interaction_model.model.PacketType;
import hs_mannheim.pattern_interaction_model.model.StitchPacket;

/**
 * Detector for synchronous Stitch Gestures. Needs connection to another device to have a handshake
 * if the stitch succeeded.
 */
public class StitchDetector extends GestureDetector implements SwipeDetector.SwipeEventListener, IPacketReceiver {
    private final int WAIT_TIME = 2000;
    private static final String TAG = "[StitchDetector]";
    private final SwipeDetector mSwipeDetector;
    private IPostOffice mPostOffice;
    private State mState;
    private Handler mHandler;
    private Runnable mRunnable;


    public StitchDetector(IPostOffice postOffice, IViewContext viewContext) {
        super(viewContext);
        mPostOffice = postOffice;
        mSwipeDetector = new SwipeDetector(viewContext);
        mSwipeDetector.addSwipeListener(this);
        mPostOffice.register(this);
        mState = State.IDLE;
    }

    public void addConstraint(SwipeConstraint constraint) {
        mSwipeDetector.addConstraint(constraint);
    }

    @Override
    public void setViewContext(IViewContext viewContext) {
        super.setViewContext(viewContext);
        if(mSwipeDetector != null ){
            mSwipeDetector.setViewContext(viewContext);
        }

    }

    @Override
    public void onSwipeDetected(SwipeEvent event) {
        StitchEvent stitchEvent = new StitchEvent(event.getStartOfSwipe(), event.getEndOfSwipe(), mViewContext.getDisplaySize());

        if (stitchEvent.getBounding().equals(StitchEvent.Bounding.OUTBOUND)) {
            mPostOffice.send(new StitchPacket("Stitch on other device", stitchEvent.getBounding(), stitchEvent.getOrientation()));
            mState = State.OUTBOUND_SENT;
            Log.d(TAG, "State changed to OUTBOUND SENT");
            startWait();
            // wait for ack
        } else if (stitchEvent.getBounding().equals(StitchEvent.Bounding.INBOUND)) {
            mState = State.INBOUND_RECOGNIZE;
            Log.d(TAG, "State changed to INBOUND RECOGNIZED");
            startWait();
            // check for recv already gotten
            // wait for recv

        }
    }

    public void startWait() {
        mHandler = new android.os.Handler();
        mRunnable = new Runnable() {
            public void run() {
                reset();
            }
        };
        mHandler.postDelayed(mRunnable, WAIT_TIME);

    }

    private void abortWaiting() {
        mHandler.removeCallbacks(mRunnable);
        reset();
    }

    private void reset() {
        mState = State.IDLE;
    }

    @Override
    public void receive(Packet packet) {
        StitchPacket stitchPacket = (StitchPacket) packet;
        Log.d(TAG, "Received packet:" + stitchPacket.getBounding().toString() + ";" + stitchPacket.getOrientation().toString());
        //TODO: Check for direction
        if (mState.equals(State.INBOUND_RECOGNIZE) && stitchPacket.getBounding().equals(StitchEvent.Bounding.OUTBOUND)) {
            // lol
            Log.d(TAG, "Received outbound package after inbound recognize. Sending ack.");
            mPostOffice.send(new StitchPacket("Stitch on other device", StitchEvent.Bounding.INTERNAL, SwipeEvent.Orientation.NORTH));
            fireGestureDetected();
            abortWaiting();
        } else if (mState.equals(State.OUTBOUND_SENT) && stitchPacket.getBounding().equals(StitchEvent.Bounding.INTERNAL)) {
            // we got a stitch here!
            Log.d(TAG, "Ack received!");
            fireGestureDetected();
            abortWaiting();
        }
//        boolean directionMatches = stitchPacket.getOrientation().equals(mLastStitch.getOrientation());
    }

    @Override
    public boolean accept(PacketType type) {
        return type.equals(PacketType.Stitch);
    }

    enum State {
        OUTBOUND_SENT, INBOUND_RECOGNIZE, IDLE
    }
}
