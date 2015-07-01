package hs_mannheim.pattern_interaction_model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import hs_mannheim.pattern_interaction_model.bluetooth.BluetoothChannel;
import hs_mannheim.pattern_interaction_model.model.ConnectionListener;
import hs_mannheim.pattern_interaction_model.model.IConnection;
import hs_mannheim.pattern_interaction_model.model.Payload;


public class BluetoothActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, ConnectionListener {
    private final String TAG = "[BluetoothActivity]";
    private int REQUEST_ENABLE_BT = 0xfe;

    private BluetoothAdapter mBluetoothAdapter;
    private IConnection mConnection;
    private BroadcastReceiver _receiver;
    private ArrayList<String> devices = new ArrayList<>();
    private ArrayAdapter<String> mArrayAdapter;

    private Button mStartDiscoveryButton;
    private IntentFilter _intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mStartDiscoveryButton = (Button) findViewById(R.id.btnStartDiscovery);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBluetoothAvailability();

        mConnection = ((InteractionApplication) getApplicationContext()).getInteractionContext().getConnection();
        mConnection.register(this);

        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, devices);

        ListView devicesList = (ListView) findViewById(R.id.lvBluetoothDevices);
        devicesList.setAdapter(mArrayAdapter);
        devicesList.setOnItemClickListener(this);

        _intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        _receiver = new BluetoothDeviceFoundReceiver();
    }

    private void checkBluetoothAvailability() {
        if (mBluetoothAdapter == null) {
            TextView warning = new TextView(this);
            warning.setText("Device does not support Bluetooth.");
            setContentView(warning);
        }

        if (!mBluetoothAdapter.isEnabled()) {
            mStartDiscoveryButton.setEnabled(false);
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean bluetoothEnabled = (resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT);

        if (bluetoothEnabled) {
            mStartDiscoveryButton.setEnabled(true);
        }
    }


    private void makeDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    public void startDiscovery(View view) {
        mBluetoothAdapter.cancelDiscovery();
        makeDiscoverable();
        Log.d(TAG, "Discovery Started");
        mArrayAdapter.clear();
        mBluetoothAdapter.startDiscovery();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String address = mArrayAdapter.getItem(position).split("\n")[1];
        mConnection.connect(address);
    }

    public void sendHallo(View view) {
        if (mConnection.isConnected()) {
            mConnection.transfer(new Payload("TYPE", "Hallo!\n"));
        }
    }

    private void showToast(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionEstablished() {
        showToast("Connected to " + ((BluetoothChannel) mConnection).getConnectedDevice());
    }

    @Override
    public void onDataReceived(Payload data) {
        showToast("Data received: " + data.toString());
    }

    @Override
    public void onConnectionLost() {
        showToast("Connection lost");
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(_receiver, _intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(_receiver);
    }

    @Override
    protected void onDestroy() {
        super.onResume();
    }

    class BluetoothDeviceFoundReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceDescription = device.getName() + "\n" + device.getAddress();

                if (device.getAddress().equals(((BluetoothChannel) mConnection).getConnectedDevice())) {
                    deviceDescription += " [CONNECTED]";
                }

                Log.d(TAG, String.format("Device discovered: %s", device.getName()));

                if (mArrayAdapter.getPosition(deviceDescription) == -1) {
                    mArrayAdapter.add(deviceDescription);
                }
            }
        }
    }
}
