package hs_mannheim.gestureframework;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;

import hs_mannheim.gestureframework.connection.PostOffice;
import hs_mannheim.gestureframework.connection.bluetooth.BluetoothChannel;
import hs_mannheim.gestureframework.connection.wifidirect.WifiDirectChannel;
import hs_mannheim.gestureframework.gesture.bump.BumpDetector;
import hs_mannheim.gestureframework.gesture.shake.ShakeDetector;
import hs_mannheim.gestureframework.gesture.stitch.StitchDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.model.GestureDetector;
import hs_mannheim.gestureframework.model.GestureDetectorBuilder;
import hs_mannheim.gestureframework.model.GestureManager;
import hs_mannheim.gestureframework.model.IConnection;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.InteractionContext;
import hs_mannheim.gestureframework.model.Selection;

public class ConfigurationBuilder {

    private final Context mContext;
    private IViewContext mViewContext;
    private IConnection mChannel;
    private Selection mSelection;
    private GestureManager mGestureManager;
    private GestureDetectorBuilder mBuilder;
    private PostOffice mPostOffice;

    public ConfigurationBuilder(Context context, IViewContext viewContext) {
        mContext = context;
        mViewContext = viewContext;
        mSelection = Selection.Empty;
    }

    public void specifyGestureComposition(GestureDetector connectDetector, GestureDetector selectDetector, GestureDetector transferDetector, GestureDetector disconnectDetector){
        mGestureManager = new GestureManager(connectDetector, selectDetector, transferDetector, disconnectDetector);
    }

    public ConfigurationBuilder withBluetooth() {
        mChannel = new BluetoothChannel(BluetoothAdapter.getDefaultAdapter());
        mPostOffice = new PostOffice(mChannel);
        mBuilder = new GestureDetectorBuilder(mPostOffice, mViewContext, mContext);
        return this;
    }

    public ConfigurationBuilder withWifiDirect() {
        WifiP2pManager wifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel channel = wifiP2pManager.initialize(mContext, mContext.getMainLooper(), null);
        mChannel = new WifiDirectChannel(wifiP2pManager, channel, mContext);
        mPostOffice = new PostOffice(mChannel);
        mBuilder = new GestureDetectorBuilder(mPostOffice, mViewContext, mContext);
        return this;
    }

    public ConfigurationBuilder select(Selection selection) {
        mSelection = selection;
        return this;
    }

    public void buildAndRegister() {

        InteractionContext interactionContext = new InteractionContext(mGestureManager, mSelection, mChannel, mPostOffice);
        ((InteractionApplication) mContext).setInteractionContext(interactionContext);
    }

    public SwipeDetector swipe(){
        return mBuilder.createSwipeDetector();
    }

    public BumpDetector bump(){
        return mBuilder.createBumpDetector();
    }

    public StitchDetector stitch(){
        return mBuilder.createStitchDetector();
    }

    public ShakeDetector shake(){
        return mBuilder.createShakeDetector();
    }
}
