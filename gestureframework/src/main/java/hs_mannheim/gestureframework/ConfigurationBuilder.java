package hs_mannheim.gestureframework;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.SensorManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.view.View;

import hs_mannheim.gestureframework.connection.PostOffice;
import hs_mannheim.gestureframework.connection.bluetooth.BluetoothChannel;
import hs_mannheim.gestureframework.connection.wifidirect.WifiDirectChannel;
import hs_mannheim.gestureframework.gesture.bump.BumpDetector;
import hs_mannheim.gestureframework.gesture.bump.Threshold;
import hs_mannheim.gestureframework.gesture.shake.ShakeDetector;
import hs_mannheim.gestureframework.gesture.stitch.StitchDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDirectionConstraint;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDurationConstraint;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.SwipeMinDistanceConstraint;
import hs_mannheim.gestureframework.gesture.swipe.SwipeOrientationConstraint;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;
import hs_mannheim.gestureframework.model.GestureDetector;
import hs_mannheim.gestureframework.model.IConnection;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.InteractionContext;
import hs_mannheim.gestureframework.model.Selection;

public class ConfigurationBuilder {

    private final Context mContext;
    private IViewContext mViewContext;
    private IConnection mChannel;
    private Selection mSelection;
    private GestureDetector mDetector;
    private PostOffice mPostOffice;

    public ConfigurationBuilder(Context context, IViewContext viewContext) {
        mContext = context;
        mViewContext = viewContext;
        mSelection = Selection.Empty;
    }

    public ConfigurationBuilder withBluetooth() {
        mChannel = new BluetoothChannel(BluetoothAdapter.getDefaultAdapter());
        mPostOffice = new PostOffice(mChannel);
        return this;
    }

    public ConfigurationBuilder withWifiDirect() {
        WifiP2pManager wifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel channel = wifiP2pManager.initialize(mContext, mContext.getMainLooper(), null);
        mChannel = new WifiDirectChannel(wifiP2pManager, channel, mContext);
        mPostOffice = new PostOffice(mChannel);
        return this;
    }

    public ConfigurationBuilder shake() {
        mDetector = new ShakeDetector((SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE), mViewContext);
        return this;
    }

    public ConfigurationBuilder bump() {
        mDetector = new BumpDetector((SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE), Threshold.LOW, mViewContext);
        return this;
    }

    public ConfigurationBuilder swipe() {
        mDetector = createSwipeDetector(null);
        return this;
    }

    public ConfigurationBuilder stitch() {
        mDetector = createStitchDetector();
        return this;
    }

    public ConfigurationBuilder select(Selection selection) {
        mSelection = selection;
        return this;
    }

    public void buildAndRegister() {
        InteractionContext interactionContext = new InteractionContext(mDetector, mSelection, mChannel, mPostOffice);
        ((InteractionApplication) mContext).setInteractionContext(interactionContext);
    }

    private StitchDetector createStitchDetector() {
        StitchDetector stitchDetector = new StitchDetector(mPostOffice, mViewContext);
        stitchDetector.addConstraint(new SwipeOrientationConstraint(SwipeEvent.Orientation.WEST));
        stitchDetector.addConstraint(new SwipeDurationConstraint(1000));
        return stitchDetector;

    }

    private SwipeDetector createSwipeDetector(SwipeDetector.SwipeEventListener listener) {
        return new SwipeDetector(mViewContext)
                .addConstraint(new SwipeDirectionConstraint(SwipeEvent.Direction.HORIZONTAL))
                .addConstraint(new SwipeDurationConstraint(1000))
                .addConstraint(new SwipeMinDistanceConstraint(500))
                .addSwipeListener(new DebugSwipeListener());
    }

    public GestureDetector getDetector(){
        return this.mDetector;
    }

    public class DebugSwipeListener implements SwipeDetector.SwipeEventListener {
        @Override
        public void onSwipeDetected(SwipeEvent event) {
            Log.d("[SwipeDetector]", "Swipe Detected");
        }

        @Override
        public void onSwiping(TouchPoint touchPoint) { }

        @Override
        public void onSwipeStart(TouchPoint touchPoint, View view) { }

        @Override
        public void onSwipeEnd(TouchPoint touchPoint) {

        }
    }
}
