package hs_mannheim.pattern_interaction_model;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import hs_mannheim.pattern_interaction_model.Bluetooth.BluetoothChannel;
import hs_mannheim.pattern_interaction_model.Gestures.SwipeDetector;
import hs_mannheim.pattern_interaction_model.Gestures.SwipeDirectionConstraint;
import hs_mannheim.pattern_interaction_model.Gestures.SwipeDurationConstraint;
import hs_mannheim.pattern_interaction_model.Gestures.SwipeOrientationConstraint;
import hs_mannheim.pattern_interaction_model.Model.Connection;
import hs_mannheim.pattern_interaction_model.Model.ConnectionListener;
import hs_mannheim.pattern_interaction_model.Model.InteractionContext;
import hs_mannheim.pattern_interaction_model.Model.Selection;


public class MainActivity extends ActionBarActivity implements SwipeDetector.SwipeEventListener, ConnectionListener {

    private BroadcastReceiver mBroadcastReceiver;
    private IntentFilter mIntentFilter;
    public final static String MODEL = Build.MODEL;
    private final String TAG = "[Main Activity]";

    private TextView dataArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataArea = (TextView) findViewById(R.id.tvDataArea);
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

        //InteractionContext interactionContext = new InteractionContext(registerSwipeListener(), new Selection("Send me"), new Connection(getApplicationContext()));
        InteractionContext interactionContext = new InteractionContext(registerSwipeListener(), new Selection(dataArea.getText().toString() + "\n"), new BluetoothChannel(BluetoothAdapter.getDefaultAdapter()));
        interactionContext.registerConnectionListener(this);
        InteractionApplication applicationContext = (InteractionApplication) getApplicationContext();
        applicationContext.setInteractionContext(interactionContext);
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
    public void onDataReceived(String data) {
        Toast.makeText(this, "Data received in MAIN: " + data, Toast.LENGTH_SHORT).show();
    }
}
