package hs_mannheim.pattern_interaction_model;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.SensorManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.View;

import hs_mannheim.pattern_interaction_model.bluetooth.BluetoothChannel;
import hs_mannheim.pattern_interaction_model.gesture.bump.BumpDetector;
import hs_mannheim.pattern_interaction_model.gesture.bump.Threshold;
import hs_mannheim.pattern_interaction_model.gesture.shake.ShakeDetector;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDetector;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDirectionConstraint;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDurationConstraint;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeEvent;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeOrientationConstraint;
import hs_mannheim.pattern_interaction_model.model.InteractionContext;
import hs_mannheim.pattern_interaction_model.model.Selection;
import hs_mannheim.pattern_interaction_model.wifidirect.WifiDirectChannel;

public class Configuration {

    public InteractionContext wifiShake(InteractionApplication context) {
        WifiP2pManager wifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel channel = wifiP2pManager.initialize(context, context.getMainLooper(), null);
        //ShakeDetector shakeDetector = new ShakeDetector((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
        ShakeDetector shakeDetector = new ShakeDetector((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
        InteractionContext interactionContext = new InteractionContext(shakeDetector, Selection.Empty, new WifiDirectChannel(wifiP2pManager, channel, context));
        return interactionContext;
    }

    public InteractionContext wifiBump(InteractionApplication context) {
        WifiP2pManager wifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel channel = wifiP2pManager.initialize(context, context.getMainLooper(), null);
        BumpDetector bumpDetector = new BumpDetector((SensorManager) context.getSystemService(Context.SENSOR_SERVICE), Threshold.LOW);
        InteractionContext interactionContext = new InteractionContext(bumpDetector, Selection.Empty, new WifiDirectChannel(wifiP2pManager, channel, context));
        return interactionContext;
    }

    public InteractionContext bluetoothSwipe(Context context, View attachView, SwipeDetector.SwipeEventListener listener) {
        return new InteractionContext(registerSwipeListener(attachView, listener), Selection.Empty, new BluetoothChannel(BluetoothAdapter.getDefaultAdapter()));
    }

    private SwipeDetector registerSwipeListener(View attachView, SwipeDetector.SwipeEventListener listener) {
        return new SwipeDetector()
                .addConstraint(new SwipeDirectionConstraint(SwipeEvent.Direction.HORIZONTAL))
                .addConstraint(new SwipeDurationConstraint(250))
                .addConstraint(new SwipeOrientationConstraint(SwipeEvent.Orientation.WEST))
                .attachToView(attachView, listener);
    }
}
