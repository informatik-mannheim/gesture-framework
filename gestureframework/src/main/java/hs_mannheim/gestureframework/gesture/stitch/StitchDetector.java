package hs_mannheim.gestureframework.gesture.stitch;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import hs_mannheim.gestureframework.gesture.GestureDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeConstraint;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;
import hs_mannheim.gestureframework.messaging.IPacketReceiver;
import hs_mannheim.gestureframework.messaging.IPostOffice;
import hs_mannheim.gestureframework.messaging.Packet;
import hs_mannheim.gestureframework.model.IViewContext;

/**
 * Detector for synchronous Stitch Gestures. Needs connection to another device to have a handshake
 * if the stitch succeeded.
 */
public class StitchDetector extends GestureDetector
        implements SwipeDetector.SwipeEventListener, IPacketReceiver {
    private static final int WAIT_TIME = 2000;
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
    public void receive(Packet packet) {
        mState.handle(packet);
    }

    @Override
    public boolean accept(Packet.PacketType type) {
        return type.equals(Packet.PacketType.StitchSyn) || type.equals(Packet.PacketType.StitchAck);
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

    @Override
    public void onSwipeDetected(SwipeDetector swipeDetector, SwipeEvent swipeEvent) {
        mState.handle(swipeEvent);
    }

    @Override
    public void onSwiping(SwipeDetector swipeDetector, TouchPoint touchPoint) {

    }

    @Override
    public void onSwipeStart(SwipeDetector swipeDetector, TouchPoint touchPoint, View view) {

    }

    @Override
    public void onSwipeEnd(SwipeDetector swipeDetector, TouchPoint touchPoint) {

    }

    abstract class StitchState {
        abstract void handle(Packet packet);

        abstract void handle(SwipeEvent event);
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
        void handle(SwipeEvent event) {
            if (event.getBounding().equals(SwipeEvent.Bounding.OUTBOUND)) {
                mPostOffice.send(new StitchSynPacket());
                startWait();
                mState = new OutState();
            } else if (event.getBounding().equals(SwipeEvent.Bounding.INBOUND)) {
                startWait();
                mState = new InState();
            }
        }
    }

    class OutState extends StitchState {

        @Override
        /* Is this the ACK we are waiting for? */
        void handle(Packet packet) {
            if (packet.getType().equals(Packet.PacketType.StitchAck)) {
                fireGestureDetected();
                abortWaiting();
            }
        }

        @Override
        /* We are waiting for an ACK, no need to process further StitchEvents */
        void handle(SwipeEvent event) {
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
        void handle(SwipeEvent event) {
            if (event.getBounding().equals(SwipeEvent.Bounding.INBOUND)) {
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
            if (packet.getType().equals(Packet.PacketType.StitchSyn)) {
                Log.d(TAG, "Received outbound package after inbound recognize. Sending ack.");
                mPostOffice.send(new StitchAckPacket());
                fireGestureDetected();
                abortWaiting();
                //boolean directionMatches = stitchPacket.getOrientation().equals(mLastStitch.getOrientation());
            }
        }

        @Override
        /* In this state, do not process further StitchEvents */
        void handle(SwipeEvent event) {
            // nothing
        }
    }
}
