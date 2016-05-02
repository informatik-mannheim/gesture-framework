package hs_mannheim.gestureframework.gesture.bump;

import android.os.Handler;
import android.util.Log;

import hs_mannheim.gestureframework.gesture.GestureDetector;
import hs_mannheim.gestureframework.messaging.IPacketReceiver;
import hs_mannheim.gestureframework.messaging.IPostOffice;
import hs_mannheim.gestureframework.messaging.Packet;
import hs_mannheim.gestureframework.model.IViewContext;

public class SyncBumpDetector extends GestureDetector implements IPacketReceiver, GestureDetector.GestureEventListener {
    private static final int WAIT_TIME = 2000;
    private static final String TAG = "[SyncBumpDetector]";
    private BumpDetector mBumpDetector;
    private IPostOffice mPostOffice;
    private BumpState mState;
    private Handler mHandler;
    private Runnable mRunnable;

    public SyncBumpDetector(IPostOffice postOffice, IViewContext viewContext, BumpDetector bumpDetector) {
        super(viewContext);
        mPostOffice = postOffice;
        mBumpDetector = bumpDetector;
        mBumpDetector.registerGestureEventListener(this);
        mPostOffice.register(this);
        mState = new IdleState();
    }


    @Override
    public void receive(Packet packet) {
        mState.handle(packet);
    }

    @Override
    public boolean accept(Packet.PacketType type) {
        return type.equals(Packet.PacketType.BumpSyn) || type.equals(Packet.PacketType.BumpAck);
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
    public void onGestureDetected(GestureDetector gestureDetector) {
        Log.d(TAG, "Handling local bump");
        mState.handleBump();
    }

    abstract class BumpState {
        abstract void handle(Packet packet);
        abstract void handleBump();
    }

    class IdleState extends BumpState {

        @Override
        /* Must be a SYN packet that arrived before any local bump */
        void handle(Packet packet) {
            if(packet.getType() == Packet.PacketType.BumpSyn) {
                startWait();
                mState = new PrematureInState();
            }
        }

        @Override
        /* Local bump, no SYN. Sending SYN and waiting for ACK */
        void handleBump() {
            mPostOffice.send(new BumpSynPacket());
            startWait();
            mState = new OutState();
        }
    }

    class OutState extends BumpState {
        @Override
        /* Is this the ACK we are waiting for? */
        void handle(Packet packet) {
            if (packet.getType() == Packet.PacketType.BumpSyn) {
                mPostOffice.send(new BumpAckPacket());
                fireGestureDetected();
                abortWaiting();
            }
        }

        @Override
        /* We are waiting for an ACK, no need to process further StitchEvents */
        void handleBump() {
            // nothing
        }
    }

    class PrematureInState extends BumpState {

        @Override
        /* We already received a packet */
        void handle(Packet packet) {
            // nothing
        }

        @Override
        /* The local bump happenend, send ack packet. */
        void handleBump() {
            mPostOffice.send(new BumpAckPacket());
            fireGestureDetected();
            abortWaiting();
        }
    }
}
