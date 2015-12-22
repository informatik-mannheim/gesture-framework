package hs_mannheim.pattern_interaction_model.gesture.stitch;

import android.os.Handler;
import android.util.Log;

import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeConstraint;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDetector;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeEvent;
import hs_mannheim.pattern_interaction_model.gesture.swipe.TouchPoint;
import hs_mannheim.pattern_interaction_model.model.GestureDetector;
import hs_mannheim.pattern_interaction_model.model.IPacketReceiver;
import hs_mannheim.pattern_interaction_model.model.IPostOffice;
import hs_mannheim.pattern_interaction_model.model.IViewContext;
import hs_mannheim.pattern_interaction_model.model.Packet;
import hs_mannheim.pattern_interaction_model.model.PacketType;

/**
 * Detector for synchronous Stitch Gestures. Needs connection to another device to have a handshake
 * if the stitch succeeded.
 */
public class StitchDetector extends GestureDetector implements SwipeDetector.SwipeEventListener, IPacketReceiver {
    private final int WAIT_TIME = 2000;
    private static final String TAG = "[StitchDetector]";
    private SwipeDetector mSwipeDetector;
    private IPostOffice mPostOffice;
    private StitchState mState;
    private Handler mHandler;
    private Runnable mRunnable;

    public StitchDetector(IPostOffice postOffice, IViewContext viewContext) {
        super(viewContext);
        mPostOffice = postOffice;
        mSwipeDetector = new SwipeDetector(viewContext);
        mSwipeDetector.addSwipeListener(this);
        mPostOffice.register(this);
        mState = new IdleState();
    }

    public void addConstraint(SwipeConstraint constraint) {
        mSwipeDetector.addConstraint(constraint);
    }

    @Override
    public void setViewContext(IViewContext viewContext) {
        super.setViewContext(viewContext);
        if (mSwipeDetector != null) {
            mSwipeDetector.setViewContext(viewContext);
        }
    }

    @Override
    public void onSwipeDetected(SwipeEvent event) {
        StitchEvent stitchEvent = new StitchEvent(event.getStartOfSwipe(), event.getEndOfSwipe(), mViewContext.getDisplaySize());
        mState.handle(stitchEvent);
    }

    @Override
    public void onSwiping(TouchPoint touchPoint) {
        // ignore for now.
    }

    @Override
    public void receive(Packet packet) {
        mState.handle(packet);
    }

    @Override
    public boolean accept(PacketType type) {
        return type.equals(PacketType.StitchSyn) || type.equals(PacketType.StitchAck);
    }

    public void startWait() {
        mHandler = new android.os.Handler();
        mRunnable = new Runnable() {
            public void run() {
                mState = new IdleState();
            }
        };
        mHandler.postDelayed(mRunnable, WAIT_TIME);
    }

    private void abortWaiting() {
        mHandler.removeCallbacks(mRunnable);
        mState = new IdleState();
    }

    abstract class StitchState {
        abstract void handle(Packet packet);
        abstract void handle(StitchEvent event);
    }

    class IdleState extends StitchState {

        @Override
        /* Must be a SYN packet that arrived before any swipe event */
        void handle(Packet packet) {
            startWait();
            mState = new PrematureInState();
        }

        @Override
        /* Send either a SYN or wait for an ACK */
        void handle(StitchEvent event) {
            if (event.getBounding().equals(StitchEvent.Bounding.OUTBOUND)) {
                mPostOffice.send(new StitchSynPacket(event.getBounding(), event.getOrientation()));
                startWait();
                mState = new OutState();
            } else if (event.getBounding().equals(StitchEvent.Bounding.INBOUND)) {
                startWait();
                mState = new InState();
            }
        }
    }

    class OutState extends StitchState {

        @Override
        /* Is this the ACK we are waiting for? */
        void handle(Packet packet) {
            if (packet.getType().equals(PacketType.StitchAck)) {
                fireGestureDetected();
                abortWaiting();
            }
        }

        @Override
        /* We are waiting for an ACK, no need to process further StitchEvents */
        void handle(StitchEvent event) {
            // nothing
        }
    }

    class PrematureInState extends StitchState {

        @Override
        /* We already received a packet */
        void handle(Packet packet) {
            // nothing
        }

        @Override
        /* Checks whether the StitchEvent is INBOUND, matching the already received SYN */
        void handle(StitchEvent event) {
            if (event.getBounding().equals(StitchEvent.Bounding.INBOUND)) {
                mPostOffice.send(new StitchAckPacket());
                fireGestureDetected();
                abortWaiting();
            }
        }
    }

    class InState extends StitchState {

        @Override
        /* Checks whether the packet is the corresponding SYN after a recognized INBOUND swipe */
        void handle(Packet packet) {
            if (packet.getType().equals(PacketType.StitchSyn)) {
                Log.d(TAG, "Received outbound package after inbound recognize. Sending ack.");
                mPostOffice.send(new StitchAckPacket());
                fireGestureDetected();
                abortWaiting();
                //boolean directionMatches = stitchPacket.getOrientation().equals(mLastStitch.getOrientation());
            }
        }

        @Override
        /* In this state, do not process further StitchEvents */
        void handle(StitchEvent event) {
            // nothing
        }
    }
}
