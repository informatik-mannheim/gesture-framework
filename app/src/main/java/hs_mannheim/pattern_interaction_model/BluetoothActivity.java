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
import android.widget.Toast;

import java.util.ArrayList;

import hs_mannheim.pattern_interaction_model.Bluetooth.BluetoothChannel;
import hs_mannheim.pattern_interaction_model.Model.ConnectionListener;
import hs_mannheim.pattern_interaction_model.Model.IConnection;


public class BluetoothActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, ConnectionListener {
    private final String TAG = "[BluetoothActivity]";
    private int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private IConnection mConnection;
    private BroadcastReceiver mReceiver;
    private ArrayList<String> devices = new ArrayList<>();
    private ArrayAdapter<String> mArrayAdapter;

    private Button mStartDiscoveryButton;
    private ListView mDevicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mStartDiscoveryButton = (Button) findViewById(R.id.btnStartDiscovery);
        mDevicesList = (ListView) findViewById(R.id.lvBluetoothDevices);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        if (!mBluetoothAdapter.isEnabled()) {
            mStartDiscoveryButton.setEnabled(false);
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        mConnection = ((InteractionApplication)getApplicationContext()).getInteractionContext().getConnection();
        mConnection.register(this);

        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, devices);
        mDevicesList.setAdapter(mArrayAdapter);
        mDevicesList.setOnItemClickListener(this);

        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        mReceiver = new BluetoothDeviceFoundReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT) {
            mStartDiscoveryButton.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onResume();
        unregisterReceiver(mReceiver);
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
            mConnection.transfer("Hallo!\n");
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
    public void onDataReceived(String data) {
        showToast("Data received: " + data);
    }

    @Override
    public void onConnectionLost() {
        showToast("Connection lost");
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
