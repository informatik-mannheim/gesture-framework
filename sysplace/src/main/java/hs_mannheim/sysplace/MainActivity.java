package hs_mannheim.sysplace;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import hs_mannheim.gestureframework.ConfigurationBuilder;
import hs_mannheim.gestureframework.model.InteractionApplication;
import hs_mannheim.gestureframework.connection.bluetooth.ConnectionInfo;
import hs_mannheim.gestureframework.connection.IConnection;
import hs_mannheim.gestureframework.model.ILifecycleListener;
import hs_mannheim.gestureframework.messaging.IPacketReceiver;
import hs_mannheim.gestureframework.model.ISysplaceContext;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.MultipleTouchView;
import hs_mannheim.gestureframework.messaging.Packet;
import hs_mannheim.gestureframework.model.Selection;
import hs_mannheim.gestureframework.model.SysplaceContext;

public class MainActivity extends AppCompatActivity implements IViewContext, IPacketReceiver, ILifecycleListener {
    protected final int REQUEST_ENABLE_BT = 100;

    private String TAG = "[Main Activity]";

    private BluetoothAdapter mBluetoothAdapter;
    private String mOldName;
    private String mCurrentName;
    private IConnection mConn;

    private MultipleTouchView mInteractionView;
    private TextView mTextView;
    private Button mPingButton;
    private Button mPhotoButton;
    private Button mDisconnectButton;
    private EditText mTextfield;
    private SysplaceContext mSysplaceContext;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        bootstrapBluetooth();

        // TODO: handle this somewhere else!
        mInteractionView = new MultipleTouchView(findViewById(R.id.layout_main));

        mTextView = ((TextView) findViewById(R.id.textView));
        mPingButton = ((Button) findViewById(R.id.btn_ping));
        mPhotoButton = ((Button) findViewById(R.id.btn_send_photo));
        mDisconnectButton = ((Button) findViewById(R.id.btn_disconnect));
        mTextfield = ((EditText) findViewById(R.id.et_tosend));

        ConfigurationBuilder builder = new ConfigurationBuilder(getApplicationContext(), this);
        builder
                .withBluetooth()
                .toConnect(builder.swipeLeftRight())
                .toSelect(builder.doubleTap())
                .toTransfer(builder.swipeUpDown())
                .toDisconnect(builder.bump())
                .select(Selection.Empty)
                .registerForLifecycleEvents(this)
                .registerPacketReceiver(this)
                .buildAndRegister();

        mSysplaceContext = ((InteractionApplication) getApplicationContext()).getSysplaceContext();

        // TODO: should be forbidden
        mConn = mSysplaceContext.getConnection();

        mTextfield.addTextChangedListener(new SelectTextWatcher(mSysplaceContext));
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "Found " + device.getAddress());

                // TODO: if the server is faster, the client can not connect. Fix this.

                if (device.getName() != null && device.getName().contains("-sysplace-")) {
                    mConn.connect(ConnectionInfo.from(mCurrentName, device.getName(), device.getAddress()));
                    Toast.makeText(MainActivity.this, "Found " + device.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void bootstrapBluetooth() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        mOldName = mBluetoothAdapter.getName();
        mCurrentName = mOldName + "-sysplace-" + Integer.toString(new Random().nextInt(10000));
    }

    private void doBluetoothMagic() {
        setDiscoverable();
        mBluetoothAdapter.startDiscovery();
    }

    private void setDiscoverable() {
        if (!(mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 10);
            startActivity(discoverableIntent);
        }
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
    public MultipleTouchView getMultipleTouchView() {
        return mInteractionView;
    }

    @Override
    public Point getDisplaySize() {
        //TODO: this only works properly in portrait mode. We have to subtract everything that
        // does not belong to the App (such as the StatusBar)
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new Point(metrics.widthPixels, metrics.heightPixels);
    }

    public void disconnect(View view) {
        mSysplaceContext.getConnection().disconnect();
    }

    @Override
    public void receive(Packet packet) {
        switch (packet.getType()) {
            case ConnectionEstablished:
                mTextView.setText(R.string.connected_info);
                mTextView.setTextColor(Color.GREEN);
                mPingButton.setEnabled(true);
                mPhotoButton.setEnabled(true);
                mDisconnectButton.setEnabled(true);
                break;
            case ConnectionLost:
                mTextView.setText(R.string.not_connected_info);
                mTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light, null));
                mPingButton.setEnabled(false);
                mPhotoButton.setEnabled(false);
                mDisconnectButton.setEnabled(false);
                break;
            default:
                Toast.makeText(this, packet.getMessage(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean accept(Packet.PacketType type) {
        return true;
    }

    public void switchToConnectedActivity(View view) {
        Intent intent = new Intent(this, ConnectedActivity.class);
        startActivity(intent);
    }

    public void ping(View view) {
        ((InteractionApplication) getApplicationContext()).getSysplaceContext().send(new Packet("Ping!"));
    }

    @Override
    public void onConnect() {
        Log.d(TAG, "Connect Happened");

        //startService(new Intent(this, BluetoothPairingService.class));

        doBluetoothMagic();
    }

    @Override
    public void onSelect() {
        Toast.makeText(this, "SELECT", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransfer() {
        Toast.makeText(this, "TRANSFER", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnect() {
        Toast.makeText(this, "DISCONNECT", Toast.LENGTH_SHORT).show();
    }

    public class SelectTextWatcher implements TextWatcher {
        private final ISysplaceContext mSysplaceContext;

        public SelectTextWatcher(ISysplaceContext sysplaceContext) {
            mSysplaceContext = sysplaceContext;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // ignore
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().isEmpty()) {
                mSysplaceContext.select(Selection.Empty);
            } else {
                mSysplaceContext.select(new Selection(new Packet(s.toString())));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // ignore
        }
    }
}

