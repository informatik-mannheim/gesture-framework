package hs_mannheim.pattern_interaction_model;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.SensorManager;
import android.net.wifi.p2p.WifiP2pManager;

import hs_mannheim.pattern_interaction_model.connection.PostOffice;
import hs_mannheim.pattern_interaction_model.connection.bluetooth.BluetoothChannel;
import hs_mannheim.pattern_interaction_model.connection.wifidirect.WifiDirectChannel;
import hs_mannheim.pattern_interaction_model.gesture.bump.BumpDetector;
import hs_mannheim.pattern_interaction_model.gesture.bump.Threshold;
import hs_mannheim.pattern_interaction_model.gesture.shake.ShakeDetector;
import hs_mannheim.pattern_interaction_model.gesture.stitch.StitchDetector;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDetector;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDirectionConstraint;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDurationConstraint;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeEvent;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeOrientationConstraint;
import hs_mannheim.pattern_interaction_model.model.GestureDetector;
import hs_mannheim.pattern_interaction_model.model.IConnection;
import hs_mannheim.pattern_interaction_model.model.IViewContext;
import hs_mannheim.pattern_interaction_model.model.InteractionContext;
import hs_mannheim.pattern_interaction_model.model.Selection;

public class ConfigurationBuilder {

    private final Context mContext;
    private IViewContext mViewContext;
    private IConnection mConnection;
    private Selection mSelection;
    private GestureDetector mDetector;
    private PostOffice mPostOffice;

    public ConfigurationBuilder(Context context, IViewContext viewContext) {
        mContext = context;
        mViewContext = viewContext;
        mSelection = Selection.Empty;
    }

    public ConfigurationBuilder withBluetooth() {
        mConnection = new BluetoothChannel(BluetoothAdapter.getDefaultAdapter());
        mPostOffice = new PostOffice(mConnection);
        return this;
    }

    public ConfigurationBuilder withWifiDirect() {
        WifiP2pManager wifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel channel = wifiP2pManager.initialize(mContext, mContext.getMainLooper(), null);
        mConnection = new WifiDirectChannel(wifiP2pManager, channel, mContext);
        mPostOffice = new PostOffice(mConnection);
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
        InteractionContext interactionContext = new InteractionContext(mDetector, mSelection, mConnection, mPostOffice);
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
                .addConstraint(new SwipeDurationConstraint(250))
                .addConstraint(new SwipeOrientationConstraint(SwipeEvent.Orientation.WEST))
                .addSwipeListener(listener);
    }
}
