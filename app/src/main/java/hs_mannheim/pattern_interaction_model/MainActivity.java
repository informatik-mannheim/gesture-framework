package hs_mannheim.pattern_interaction_model;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import hs_mannheim.pattern_interaction_model.bluetooth.BluetoothChannel;
import hs_mannheim.pattern_interaction_model.gesture.bump.BumpDetector;
import hs_mannheim.pattern_interaction_model.gesture.bump.Threshold;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDetector;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDirectionConstraint;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDurationConstraint;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeOrientationConstraint;
import hs_mannheim.pattern_interaction_model.model.ConnectionListener;
import hs_mannheim.pattern_interaction_model.model.InteractionContext;
import hs_mannheim.pattern_interaction_model.model.Payload;
import hs_mannheim.pattern_interaction_model.model.Selection;
import hs_mannheim.pattern_interaction_model.wifidirect.Server;
import hs_mannheim.pattern_interaction_model.wifidirect.WifiDirectChannel;


public class MainActivity extends ActionBarActivity implements SwipeDetector.SwipeEventListener, ConnectionListener {

    private BroadcastReceiver mBroadcastReceiver;
    private IntentFilter mIntentFilter;
    public final static String MODEL = Build.MODEL;
    private final String TAG = "[Main Activity]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView dataArea = (TextView) findViewById(R.id.tvDataArea);
        TextView header = (TextView) findViewById(R.id.tvHeaderMain);
        header.setText(MODEL);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Server.ACTION_DATA_RECEIVED);

        this.mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, intent.getStringExtra("data"), Toast.LENGTH_SHORT).show();
            }
        };

        createInteractionContext();
    }

    private void createInteractionContext() {
        InteractionContext interactionContext = new InteractionContext(registerSwipeListener(), new Selection("Swipe!" + "\n"), new BluetoothChannel(BluetoothAdapter.getDefaultAdapter()));
        interactionContext.registerConnectionListener(this);
        InteractionApplication applicationContext = (InteractionApplication) getApplicationContext();
        applicationContext.setInteractionContext(interactionContext);
    }

    private void createInteractionContext2() {

        WifiP2pManager wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel channel = wifiP2pManager.initialize(this, getMainLooper(), null);

        InteractionContext interactionContext = new InteractionContext(registerBumpListener(), new Selection("Bump!" + "\n"), new WifiDirectChannel(wifiP2pManager, channel, getApplicationContext()));
        interactionContext.registerConnectionListener(this);
        InteractionApplication applicationContext = (InteractionApplication) getApplicationContext();
        applicationContext.setInteractionContext(interactionContext);
    }

    private BumpDetector registerBumpListener() {
        return new BumpDetector((SensorManager) getSystemService(SENSOR_SERVICE), Threshold.LOW);
    }

    private SwipeDetector registerSwipeListener() {
        return new SwipeDetector()
                .addConstraint(new SwipeDirectionConstraint(SwipeDetector.Direction.HORIZONTAL))
                .addConstraint(new SwipeDurationConstraint(250))
                .addConstraint(new SwipeOrientationConstraint(SwipeDetector.Orientation.WEST))
                .attachToView(findViewById(R.id.layout_main), this);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Resuming");
        super.onResume();
        registerReceiver(mBroadcastReceiver, mIntentFilter);
        InteractionApplication applicationContext = (InteractionApplication) getApplicationContext();
        applicationContext.getInteractionContext().registerConnectionListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onSwipeDetected(SwipeDetector.SwipeEvent event) {
        Toast.makeText(this, event.toString(), Toast.LENGTH_SHORT).show();
    }

    public void startBluetoothActivity(View view) {
        startActivity(new Intent(this, BluetoothActivity.class));
    }

    public void startWifiDirectActivity(View view) {
        startActivity(new Intent(this, WifiDirectActivity.class));
    }

    @Override
    public void onConnectionLost() {

    }

    @Override
    public void onConnectionEstablished() {

    }

    @Override
    public void onDataReceived(Payload data) {
        Toast.makeText(this, "Data received in MAIN: " + data.toString(), Toast.LENGTH_SHORT).show();
    }
}
