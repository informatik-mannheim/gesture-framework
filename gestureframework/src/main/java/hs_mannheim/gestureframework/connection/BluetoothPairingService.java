package hs_mannheim.gestureframework.connection;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Service that starts device discovery to find other Bluetooth devices.
 * A broadcast receiver listening for ACTION_FOUND intents will receiver a notification when
 * devices are found.
 */
public class BluetoothPairingService extends IntentService {

    private final String TAG = "[Bt Pairing Service]";
    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothPairingService() {
        super("Bluetooth Pairing Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Starting discovery");
        setDiscoverable();
        mBluetoothAdapter.startDiscovery();
    }

    private void setDiscoverable() {
        if (!(mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 10);
            discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(discoverableIntent);
        }
    }
}
