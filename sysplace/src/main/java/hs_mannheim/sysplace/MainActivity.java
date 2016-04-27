package hs_mannheim.sysplace;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import hs_mannheim.gestureframework.ConfigurationBuilder;
import hs_mannheim.gestureframework.InteractionApplication;
import hs_mannheim.gestureframework.connection.bluetooth.BluetoothChannel;
import hs_mannheim.gestureframework.connection.bluetooth.ConnectionInfo;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;
import hs_mannheim.gestureframework.model.GestureContext;
import hs_mannheim.gestureframework.model.GestureManager;
import hs_mannheim.gestureframework.model.IConnection;
import hs_mannheim.gestureframework.model.IConnectionListener;
import hs_mannheim.gestureframework.model.IPacketReceiver;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.Packet;
import hs_mannheim.gestureframework.model.PacketType;
import hs_mannheim.gestureframework.model.Selection;

public class MainActivity extends AppCompatActivity implements IViewContext, GestureManager.GestureListener, IPacketReceiver, IInteractionListener {

    private String TAG = "[Main Activity]";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;


    private final int REQUEST_ENABLE_BT = 100;

    private static final int LOCATION_REQUEST = 1337;
    private String mOldName;
    private String mCurrentName;
    private IConnection mConn;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ConfigurationBuilder builder = new ConfigurationBuilder(getApplicationContext(), this);
        builder.withBluetooth();
        builder.specifyGestureComposition(builder.swipe(), null, builder.swipe(), null);
        builder.select(new Selection(new Packet("Empty")));

        // SwipeListener sl = new SwipeListener(constraints) --> horizontal, < 1000ms, length = 300px
        // sl.registerSwipeListener(this) = ... --> implementes ISwipeListener
        // framework.setConnectGesture(swipeListener)
        // framework.onConnect(this) --> implements ILifecycleListener (oder sogar: IConnectListener, ITransferListener ....)
        // --> connect()

        builder.buildAndRegister();
        ((InteractionApplication) getApplicationContext()).getInteractionContext().getGestureManager().registerGestureEventListener(GestureContext.CONNECT, this);
        ((InteractionApplication) getApplicationContext()).getInteractionContext().getPostOffice().register(this);

        mConn = ((InteractionApplication) getApplicationContext()).getInteractionContext().getConnection();

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        // enable bluetooth
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);


        mOldName = mBluetoothAdapter.getName();
        mCurrentName = mOldName + "-sysplace-" + Integer.toString(new Random().nextInt(10000));
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "Found " + device.getAddress());

                if (device.getName() != null && device.getName().contains("-sysplace-")) {
                    mConn.connect(ConnectionInfo.from(mCurrentName, device.getName(), device.getAddress()));
                    Toast.makeText(MainActivity.this, "Found " + device.getName(), Toast.LENGTH_SHORT).show();
                    mBluetoothAdapter.cancelDiscovery();
                }
            }
        }
    };

    private void doBluetoothMagic() {
        setDiscoverable();
        mBluetoothAdapter.startDiscovery();
    }

    private void setDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 10);
        startActivity(discoverableIntent);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        Log.d(TAG, "Renaming to " + mCurrentName);
        mBluetoothAdapter.setName(mCurrentName);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
        Log.d(TAG, "Renaming to " + mOldName);
        mBluetoothAdapter.setName(mOldName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        Log.d(TAG, "Renaming to " + mOldName);
        mBluetoothAdapter.setName(mOldName);
    }

    @Override
    public View getInteractionView() {
        return findViewById(R.id.layout_main);
    }

    @Override
    public Point getDisplaySize() {
        return null;
    }


    @Override
    public void onGestureDetected() {
        doBluetoothMagic();
    }

    @Override
    public void onSwipeDetected(SwipeEvent event) {
        Log.d(TAG, "Doing bluetooth magic");
        doBluetoothMagic();
    }

    @Override
    public void onSwiping(TouchPoint touchPoint) {

    }

    @Override
    public void onSwipeStart(TouchPoint touchPoint, View view) {

    }

    @Override
    public void onSwipeEnd(TouchPoint touchPoint) {

    }

    public void disconnect(View view) {
        ((InteractionApplication) getApplicationContext()).getInteractionContext().getConnection().disconnect();
    }

    @Override
    public void receive(Packet packet) {
        Log.d(TAG, "Packet received!");
        if (packet.getMessage().equals("Connection established")) {
            ((TextView) findViewById(R.id.textView)).setText("Connected");
        } else if (packet.getMessage().equals("Connection lost")) {
            ((TextView) findViewById(R.id.textView)).setText("NOT Connected");
        } else {
            Toast.makeText(this, packet.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean accept(PacketType type) {
        return true;
    }

    public void switchToConnectedActivity(View view) {
        Intent intent = new Intent(this, ConnectedActivity.class);
        startActivity(intent);
    }

    public void ping(View view) {
        ((InteractionApplication) getApplicationContext()).getInteractionContext().getPostOffice().send(new Packet("Ping!"));
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onSelect() {

    }

    @Override
    public void onTransfer() {

    }

    @Override
    public void onDisconnect() {

    }
}

