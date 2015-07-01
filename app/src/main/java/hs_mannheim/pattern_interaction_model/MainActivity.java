package hs_mannheim.pattern_interaction_model;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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
import hs_mannheim.pattern_interaction_model.wifidirect.WifiDirectChannel;


public class MainActivity extends ActionBarActivity implements SwipeDetector.SwipeEventListener, ConnectionListener, TextWatcher {

    public final static String MODEL = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView header = (TextView) findViewById(R.id.tvHeaderMain);
        ((EditText) findViewById(R.id.etMessage)).addTextChangedListener(this);

        header.setText(MODEL);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        createInteractionContext();
    }

    private void createInteractionContext() {
        InteractionContext interactionContext = new InteractionContext(registerSwipeListener(), new Selection(new Payload("DATA", "swipe default")), new BluetoothChannel(BluetoothAdapter.getDefaultAdapter()));
        interactionContext.registerConnectionListener(this);
        InteractionApplication applicationContext = (InteractionApplication) getApplicationContext();
        applicationContext.setInteractionContext(interactionContext);
    }

    @SuppressWarnings("unused")
    private void createInteractionContext2() {

        WifiP2pManager wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel channel = wifiP2pManager.initialize(this, getMainLooper(), null);

        InteractionContext interactionContext = new InteractionContext(registerBumpListener(), new Selection(new Payload("DATA", "bump default")), new WifiDirectChannel(wifiP2pManager, channel, getApplicationContext()));
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
        super.onResume();

        InteractionApplication applicationContext = (InteractionApplication) getApplicationContext();
        applicationContext.getInteractionContext().registerConnectionListener(this);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        ((InteractionApplication) getApplicationContext()).getInteractionContext().updateSelection(new Payload("DATA", s.toString()));
    }
}
